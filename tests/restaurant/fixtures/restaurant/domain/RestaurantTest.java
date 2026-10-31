package restaurant.domain;

import java.util.List;

public final class RestaurantTest {
    private static int checks;

    private RestaurantTest() { }

    public static void main(String[] args) {
        tableCapacity();
        queueOrderAndRemoval();
        abandonment();
        paymentAndDuplicatePayment();
        zeroPayments();
        exactThresholds();
        System.out.println("Passed " + checks + " restaurant checks (isolated Order fixture).");
    }

    private static void tableCapacity() {
        Restaurant restaurant = new Restaurant();
        List<Table> tables = restaurant.getTables();
        check(tables.size() == 2);
        check(tables.get(0).getTableId() == 1 && tables.get(1).getTableId() == 2);
        expect(UnsupportedOperationException.class, () -> tables.add(null));
        check(restaurant.findTable(2) == tables.get(1));
        expect(IllegalArgumentException.class, () -> restaurant.findTable(0));
        expect(IllegalArgumentException.class, () -> restaurant.findTable(3));

        Table table = restaurant.findTable(1);
        Customer first = new RegularCustomer(1, 1);
        Customer second = new RegularCustomer(2, 1);
        expect(IllegalArgumentException.class, () -> table.seat(null));
        check(table.isFree());
        table.seat(first);
        first.markSeated();
        check(!table.isFree() && table.getOccupant() == first);
        expect(IllegalStateException.class, () -> table.seat(second));
        check(table.getOccupant() == first);
        check(restaurant.findSeatedCustomer(1) == first);
        expect(IllegalArgumentException.class, () -> restaurant.findSeatedCustomer(2));
        expect(IllegalStateException.class, table::clear);
        check(table.getOccupant() == first);
    }

    private static void queueOrderAndRemoval() {
        Restaurant restaurant = new Restaurant();
        WaitingQueue queue = restaurant.getWaitingQueue();
        Customer first = new RegularCustomer(1, 1);
        Customer second = new RegularCustomer(2, 4);
        Customer third = new RegularCustomer(3, 7);
        check(queue.isEmpty() && queue.peek().isEmpty());
        expect(IllegalArgumentException.class, () -> queue.add(null));
        queue.add(first);
        queue.add(second);
        queue.add(third);
        expect(IllegalStateException.class, () -> queue.add(second));
        check(queue.getCustomers().equals(List.of(first, second, third)));
        check(queue.peek().orElseThrow() == first);
        expect(UnsupportedOperationException.class, () -> queue.getCustomers().clear());
        check(queue.getCustomers().size() == 3);

        queue.remove(second);
        check(queue.getCustomers().equals(List.of(first, third)));
        expect(IllegalArgumentException.class, () -> queue.remove(second));
        expect(IllegalArgumentException.class, () -> queue.remove(null));
        check(queue.getCustomers().size() == 2);

        Customer seated = new RegularCustomer(4, 1);
        seated.markSeated();
        expect(IllegalStateException.class, () -> queue.add(seated));
        restaurant.findTable(1).seat(seated);
        check(restaurant.getPresentCustomers().equals(List.of(first, third, seated)));
        expect(UnsupportedOperationException.class, () -> restaurant.getPresentCustomers().clear());
    }

    private static void abandonment() {
        Restaurant restaurant = new Restaurant();
        Customer queued = new RegularCustomer(1, 1);
        restaurant.getWaitingQueue().add(queued);

        Customer waitingForFood = new RegularCustomer(2, 1);
        Order unserved = seatWithOrder(restaurant, waitingForFood, 1, new MenuItem("Salad", 900L, 1));
        unserved.finishPreparation();

        Customer eating = new RegularCustomer(3, 1);
        Order served = seatWithOrder(restaurant, eating, 2, new MenuItem("Burger", 1500L, 1));
        serve(eating, served, 2);

        for (int i = 0; i < 12; i++) {
            check(restaurant.applyWaitingPenalties().isEmpty());
        }
        check(queued.getSatisfaction() == 4 && waitingForFood.getSatisfaction() == 4);
        check(eating.getSatisfaction() == 100);
        check(restaurant.getOutstandingBillCents() == 2400L && restaurant.getLostBillCents() == 0);

        List<Customer> departed = restaurant.applyWaitingPenalties();
        check(departed.equals(List.of(queued, waitingForFood)));
        check(queued.getState() == CustomerState.LEFT && waitingForFood.getState() == CustomerState.LEFT);
        check(restaurant.getWaitingQueue().isEmpty());
        check(restaurant.findTable(1).isFree());
        check(unserved.getStatus() == OrderStatus.CANCELLED && unserved.cancelCalls == 1);
        check(restaurant.getAbandonedCount() == 2);
        check(restaurant.findTable(2).getOccupant() == eating && eating.getSatisfaction() == 100);
        check(served.getStatus() == OrderStatus.SERVED && served.cancelCalls == 0);
        check(restaurant.getOutstandingBillCents() == 1500L && restaurant.getLostBillCents() == 900L);

        check(restaurant.applyWaitingPenalties().isEmpty());
        check(restaurant.getAbandonedCount() == 2 && restaurant.getLostBillCents() == 900L);
        check(restaurant.getRevenueCents() == 0 && restaurant.getServedCount() == 0);
    }

    private static void paymentAndDuplicatePayment() {
        Restaurant restaurant = new Restaurant();
        Customer later = new RegularCustomer(2, 4);
        Order laterOrder = seatWithOrder(restaurant, later, 1, new MenuItem("Burger", 1500L, 1));
        Customer earlier = new RegularCustomer(1, 1);
        Order earlierOrder = seatWithOrder(restaurant, earlier, 2, new MenuItem("Pasta", 1800L, 2));
        serve(later, laterOrder, 4);
        serve(earlier, earlierOrder, 4);

        restaurant.advanceEating(4);
        check(earlier.getState() == CustomerState.EATING && restaurant.nextReadyToPay(5).isEmpty());
        restaurant.advanceEating(5);
        check(earlier.getState() == CustomerState.READY_TO_PAY && earlier.getReadyToPayTurn() == 5);

        check(restaurant.nextReadyToPay(5).isEmpty());
        expect(IllegalStateException.class, () -> restaurant.recordPayment(earlier, 5));
        check(earlierOrder.getStatus() == OrderStatus.SERVED && earlier.getState() == CustomerState.READY_TO_PAY);
        check(restaurant.getRevenueCents() == 0 && restaurant.getServedCount() == 0);
        check(restaurant.getOutstandingBillCents() == 3300L && restaurant.getPaidCustomers().isEmpty());

        check(restaurant.nextReadyToPay(6).orElseThrow() == earlier);
        expect(IllegalArgumentException.class, () -> restaurant.recordPayment(null, 6));
        restaurant.recordPayment(earlier, 6);
        check(earlierOrder.getStatus() == OrderStatus.PAID && earlier.getState() == CustomerState.PAID);
        check(restaurant.getRevenueCents() == 1800L && restaurant.getServedCount() == 1);
        check(restaurant.getOutstandingBillCents() == 1500L);
        check(restaurant.getPaidCustomers().equals(List.of(earlier)));
        expect(UnsupportedOperationException.class, () -> restaurant.getPaidCustomers().clear());
        check(restaurant.findTable(2).isFree());
        check(restaurant.getAverageSatisfaction().getAsDouble() == 100.0);

        expect(IllegalStateException.class, () -> restaurant.recordPayment(earlier, 7));
        check(earlierOrder.paidCalls == 1);
        check(restaurant.getRevenueCents() == 1800L && restaurant.getServedCount() == 1);

        Customer stranger = new RegularCustomer(9, 1);
        expect(IllegalArgumentException.class, () -> restaurant.recordPayment(stranger, 7));

        check(restaurant.nextReadyToPay(7).orElseThrow() == later);
        restaurant.recordPayment(later, 7);
        check(restaurant.getRevenueCents() == 3300L && restaurant.getServedCount() == 2);
        check(restaurant.nextReadyToPay(8).isEmpty());
        check(restaurant.getPaidCustomers().equals(List.of(earlier, later)));
        check(earlier.getBillCents() + later.getBillCents() == restaurant.getRevenueCents());
        check(restaurant.getOutstandingBillCents() == 0 && restaurant.getLostBillCents() == 0);
    }

    private static void zeroPayments() {
        Restaurant restaurant = new Restaurant();
        check(restaurant.getAverageSatisfaction().isEmpty());
        GameResult result = restaurant.createResult(18);
        check(!result.isVictory());
        check(result.getServedCount() == 0 && result.getRevenueCents() == 0);
        check(result.getAverageSatisfaction().isEmpty() && result.getTurnsUsed() == 18);
    }

    private static void exactThresholds() {
        GameResult exact = play(new long[] {1125, 1125, 1125, 1125}, new int[] {40, 40, 40, 40});
        check(exact.isVictory());
        check(exact.getServedCount() == 4 && exact.getRevenueCents() == 4500L);
        check(exact.getAverageSatisfaction().getAsDouble() == 60.0);
        check(exact.getAbandonedCount() == 0 && exact.getTurnsUsed() == 18);

        GameResult threePaid = play(new long[] {1500, 1500, 1500}, new int[] {0, 0, 0});
        check(threePaid.getRevenueCents() == 4500L && !threePaid.isVictory());

        GameResult lowRevenue = play(new long[] {1125, 1125, 1125, 1124}, new int[] {0, 0, 0, 0});
        check(lowRevenue.getRevenueCents() == 4499L && !lowRevenue.isVictory());

        GameResult lowAverage = play(new long[] {1500, 1500, 1500, 1500}, new int[] {40, 40, 40, 44});
        check(lowAverage.getAverageSatisfaction().getAsDouble() == 59.0 && !lowAverage.isVictory());
    }

    private static GameResult play(long[] pricesCents, int[] satisfactionLoss) {
        Restaurant restaurant = new Restaurant();
        for (int i = 0; i < pricesCents.length; i++) {
            Customer customer = new RegularCustomer(i + 1, 1);
            Order order = seatWithOrder(restaurant, customer, 1, new MenuItem("Dish", pricesCents[i], 1));
            customer.loseSatisfaction(satisfactionLoss[i]);
            serve(customer, order, 2);
            restaurant.advanceEating(3);
            restaurant.recordPayment(customer, 4);
        }
        return restaurant.createResult(18);
    }

    private static Order seatWithOrder(Restaurant restaurant, Customer customer, int tableId, MenuItem item) {
        restaurant.findTable(tableId).seat(customer);
        customer.markSeated();
        Order order = new Order(item);
        customer.placeOrder(order);
        return order;
    }

    private static void serve(Customer customer, Order order, int turn) {
        if (order.getStatus() == OrderStatus.PLACED) { order.finishPreparation(); }
        order.markServed();
        customer.markServed(turn);
    }

    private static void check(boolean condition) {
        checks++;
        if (!condition) { throw new AssertionError("Check " + checks + " failed"); }
    }

    private static void expect(Class<? extends RuntimeException> type, Runnable action) {
        checks++;
        try {
            action.run();
        } catch (RuntimeException exception) {
            if (type.isInstance(exception)) { return; }
            throw new AssertionError("Expected " + type.getSimpleName(), exception);
        }
        throw new AssertionError("Expected " + type.getSimpleName());
    }
}
