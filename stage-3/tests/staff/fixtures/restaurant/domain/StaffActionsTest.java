package restaurant.domain;

public final class StaffActionsTest {
    private static int checks;

    private StaffActionsTest() { }

    public static void main(String[] args) {
        namesAndChef();
        seating();
        orderingAndServing();
        payments();
        System.out.println("Passed " + checks + " staff checks (isolated collaborator fixtures).");
    }

    private static void namesAndChef() {
        expect(IllegalArgumentException.class, () -> new Chef(null));
        expect(IllegalArgumentException.class, () -> new Waiter("  "));
        expect(IllegalArgumentException.class, () -> new Cashier(""));
        Chef chef = new Chef("Chef");
        check(chef.getName().equals("Chef") && !chef.hasActedThisTurn());
        expect(IllegalArgumentException.class, () -> chef.prepare(null));
        Order pasta = new Order(new MenuItem("Pasta", 1800L, 2));
        pasta.rejectPreparation = true;
        expect(IllegalStateException.class, () -> chef.prepare(pasta));
        check(!chef.hasActedThisTurn() && pasta.units == 0);
        pasta.rejectPreparation = false;
        chef.prepare(pasta);
        check(chef.hasActedThisTurn() && pasta.units == 1);
        expect(IllegalStateException.class, () -> chef.prepare(pasta));
        check(pasta.units == 1);
        chef.resetForTurn();
        check(!chef.hasActedThisTurn());
        chef.prepare(pasta);
        check(pasta.units == 2 && pasta.getStatus() == OrderStatus.READY);
        chef.resetForTurn();
        expect(IllegalStateException.class, () -> chef.prepare(pasta));
        check(!chef.hasActedThisTurn() && pasta.units == 2);
    }

    private static void seating() {
        Restaurant restaurant = new Restaurant();
        Waiter waiter = new Waiter("Waiter");
        Customer first = new RegularCustomer(1, 1);
        Customer second = new RegularCustomer(2, 4);
        Table table = restaurant.getTables().get(0);
        expect(IllegalArgumentException.class, () -> waiter.seat(null, first, table));
        expect(IllegalStateException.class, () -> waiter.seat(restaurant, first, table));
        restaurant.getWaitingQueue().customers.add(first);
        restaurant.getWaitingQueue().customers.add(second);
        expect(IllegalStateException.class, () -> waiter.seat(restaurant, second, table));
        expect(IllegalArgumentException.class, () -> waiter.seat(restaurant, first, new Table()));
        check(table.isFree() && first.getState() == CustomerState.WAITING);
        check(!waiter.hasActedThisTurn() && restaurant.getWaitingQueue().customers.size() == 2);
        waiter.seat(restaurant, first, table);
        check(waiter.hasActedThisTurn() && table.getOccupant() == first);
        check(first.getState() == CustomerState.SEATED && table.seatCalls == 1);
        check(restaurant.getWaitingQueue().removeCalls == 1);
        check(restaurant.getWaitingQueue().peek().orElseThrow() == second);
        expect(IllegalStateException.class, () -> waiter.takeOrder(restaurant, first));
        waiter.resetForTurn();
        expect(IllegalStateException.class, () -> waiter.seat(restaurant, second, table));
        check(!waiter.hasActedThisTurn() && second.getState() == CustomerState.WAITING);
    }

    private static void orderingAndServing() {
        Restaurant restaurant = new Restaurant();
        Waiter waiter = new Waiter("Waiter");
        Customer customer = new RegularCustomer(1, 4);
        expect(IllegalArgumentException.class, () -> waiter.takeOrder(restaurant, null));
        expect(IllegalStateException.class, () -> waiter.takeOrder(restaurant, customer));
        customer.markSeated();
        expect(IllegalArgumentException.class, () -> waiter.takeOrder(restaurant, customer));
        restaurant.getTables().get(0).seat(customer);
        Order order = waiter.takeOrder(restaurant, customer);
        check(customer.getOrder() == order && waiter.hasActedThisTurn());
        check(customer.getState() == CustomerState.SEATED);
        waiter.resetForTurn();
        expect(IllegalStateException.class, () -> waiter.takeOrder(restaurant, customer));
        expect(IllegalStateException.class, () -> waiter.serve(restaurant, customer, 5));
        check(!waiter.hasActedThisTurn() && order.getStatus() == OrderStatus.PLACED);
        order.advanceOneUnit();
        expect(IllegalArgumentException.class, () -> waiter.serve(restaurant, customer, 3));
        check(order.getStatus() == OrderStatus.READY && order.serveCalls == 0);
        check(customer.getState() == CustomerState.SEATED && !waiter.hasActedThisTurn());
        expect(IllegalArgumentException.class, () -> waiter.serve(new Restaurant(), customer, 6));
        waiter.serve(restaurant, customer, 6);
        check(order.getStatus() == OrderStatus.SERVED && order.serveCalls == 1);
        check(customer.getState() == CustomerState.EATING && customer.getServedTurn() == 6);
        check(waiter.hasActedThisTurn());
        waiter.resetForTurn();
        expect(IllegalStateException.class, () -> waiter.serve(restaurant, customer, 7));
        check(order.serveCalls == 1 && !waiter.hasActedThisTurn());
    }

    private static void payments() {
        Restaurant restaurant = new Restaurant();
        Cashier cashier = new Cashier("Cashier");
        Customer first = new RegularCustomer(1, 1);
        Customer second = new RegularCustomer(2, 4);
        expect(IllegalArgumentException.class, () -> cashier.collect(null, first, 6));
        expect(IllegalArgumentException.class, () -> cashier.collect(restaurant, null, 6));
        expect(IllegalArgumentException.class, () -> cashier.collect(restaurant, first, 0));
        expect(IllegalStateException.class, () -> cashier.collect(restaurant, first, 6));
        restaurant.eligible = first;
        expect(IllegalStateException.class, () -> cashier.collect(restaurant, second, 6));
        restaurant.rejectPayment = true;
        expect(IllegalStateException.class, () -> cashier.collect(restaurant, first, 6));
        check(!cashier.hasActedThisTurn() && restaurant.paymentCalls == 0);
        restaurant.rejectPayment = false;
        cashier.collect(restaurant, first, 6);
        check(cashier.hasActedThisTurn() && restaurant.paymentCalls == 1);
        check(restaurant.collected == first && restaurant.paymentTurn == 6);
        expect(IllegalStateException.class, () -> cashier.collect(restaurant, first, 6));
        check(restaurant.paymentCalls == 1);
        cashier.resetForTurn();
        restaurant.eligible = second;
        cashier.collect(restaurant, second, 7);
        check(restaurant.paymentCalls == 2 && restaurant.collected == second);
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
