package restaurant.domain;

public final class DomainBehaviorTest {
    private static int checks;

    private DomainBehaviorTest() { }

    public static void main(String[] args) {
        menuAndFoodChoice();
        customerTransitionsAndSatisfaction();
        orderPreparationAndCancellation();
        System.out.println("Passed " + checks + " domain behavior checks.");
    }

    private static void menuAndFoodChoice() {
        Menu menu = new Menu();
        check(menu.findByName("Salad").getPriceCents() == 900L);
        check(menu.findByName("Burger").getPriceCents() == 1500L);
        check(menu.findByName("Pasta").getPreparationUnits() == 2);
        expect(IllegalArgumentException.class, () -> menu.findByName("Soup"));
        check(new RegularCustomer(1, 1).chooseFood(menu) == menu.getItems().get(0));
    }

    private static void customerTransitionsAndSatisfaction() {
        Customer waiting = new RegularCustomer(1, 1);
        check(waiting.waitingPenalty() == 8);
        waiting.loseSatisfaction(101);
        check(waiting.getSatisfaction() == 0);
        waiting.loseSatisfaction(8);
        check(waiting.getSatisfaction() == 0);
        expect(IllegalStateException.class, () -> waiting.advanceEating(1));
        waiting.leave();
        expect(IllegalStateException.class, () -> waiting.advanceEating(2));

        Customer customer = new RegularCustomer(2, 1);
        customer.markSeated();
        expect(IllegalStateException.class, () -> customer.advanceEating(1));
        check(customer.getBillCents() == 0 && !customer.hasUnpaidBill());
        Order order = new Order(customer.chooseFood(new Menu()));
        customer.placeOrder(order);
        check(customer.getBillCents() == 900L && customer.hasUnpaidBill());
        expect(IllegalStateException.class, () -> customer.markServed(1));
        order.advanceOneUnit();
        order.markServed();
        customer.markServed(1);
        customer.advanceEating(1);
        check(customer.getState() == CustomerState.EATING);
        customer.loseSatisfaction(8);
        check(customer.getSatisfaction() == 100);
        customer.advanceEating(2);
        check(customer.getState() == CustomerState.READY_TO_PAY);
        check(customer.getReadyToPayTurn() == 2);
        expect(IllegalStateException.class, () -> customer.advanceEating(3));
        order.markPaid();
        customer.markPaid();
        customer.loseSatisfaction(8);
        check(customer.getState() == CustomerState.PAID && customer.getSatisfaction() == 100);
        // A paid customer keeps the bill amount but owes nothing.
        check(customer.getBillCents() == 900L && !customer.hasUnpaidBill());

        // A cancelled order is never billed.
        Customer walkout = new RegularCustomer(3, 1);
        walkout.markSeated();
        Order cancelled = new Order(new Menu().findByName("Pasta"));
        walkout.placeOrder(cancelled);
        check(walkout.getBillCents() == 1800L && walkout.hasUnpaidBill());
        cancelled.cancel();
        check(walkout.getBillCents() == 0 && !walkout.hasUnpaidBill());
    }

    private static void orderPreparationAndCancellation() {
        Order placed = new Order(new Menu().findByName("Pasta"));
        check(placed.getStatus() == OrderStatus.PLACED && placed.getPriceCents() == 1800L);
        placed.advanceOneUnit();
        check(placed.getPreparationUnitsDone() == 1 && placed.getStatus() == OrderStatus.PLACED);
        placed.advanceOneUnit();
        check(placed.getPreparationUnitsDone() == 2 && placed.getStatus() == OrderStatus.READY);
        expect(IllegalStateException.class, placed::advanceOneUnit);
        placed.cancel();
        check(placed.getStatus() == OrderStatus.CANCELLED);
        expect(IllegalStateException.class, placed::cancel);

        Order ready = new Order(new Menu().findByName("Salad"));
        ready.advanceOneUnit();
        expect(IllegalStateException.class, ready::markPaid);
        ready.cancel();
        expect(IllegalStateException.class, ready::cancel);

        Order served = new Order(new Menu().findByName("Salad"));
        served.advanceOneUnit();
        served.markServed();
        expect(IllegalStateException.class, served::cancel);
        served.markPaid();
        expect(IllegalStateException.class, served::cancel);
    }

    private static void check(boolean condition) {
        checks++;
        if (!condition) {
            throw new AssertionError("Check " + checks + " failed");
        }
    }

    private static void expect(Class<? extends RuntimeException> type, Runnable action) {
        checks++;
        try {
            action.run();
        } catch (RuntimeException exception) {
            if (type.isInstance(exception)) {
                return;
            }
            throw new AssertionError("Expected " + type.getSimpleName(), exception);
        }
        throw new AssertionError("Expected " + type.getSimpleName());
    }
}
