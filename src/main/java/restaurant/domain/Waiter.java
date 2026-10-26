package restaurant.domain;

/**
 * Stage 1 prototype. Owner: Yuma.
 * See docs/stage-1-contracts.md before implementing TODO methods.
 */
public final class Waiter extends Staff {
    public Waiter(String name) {
        super(name);
    }

    /**
     * Require an available Waiter, a queued customer and a free restaurant-owned table.
     * Delegate Table.seat(), Customer.markSeated() and WaitingQueue.remove().
     * Preserve the queue's arrival-order fairness; mark acted only after success.
     */
    public void seat(Restaurant restaurant, Customer customer, Table table) {
        throw new UnsupportedOperationException("TODO: Yuma - seating action");
    }

    /**
     * Require a seated customer with no order in this restaurant.
     * Ask Customer.chooseFood(restaurant.getMenu()), create Order and attach it.
     * The manager/UI never chooses the dish. Return the new order.
     */
    public Order takeOrder(Restaurant restaurant, Customer customer) {
        throw new UnsupportedOperationException("TODO: Yuma - order-taking action");
    }

    /**
     * Require a seated customer whose order is already READY when the Waiter acts.
     * Call Order.markServed(), then Customer.markServed(turn); mark acted on success.
     */
    public void serve(Restaurant restaurant, Customer customer, int turn) {
        throw new UnsupportedOperationException("TODO: Yuma - serving action");
    }
}
