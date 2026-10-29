package restaurant.domain;

/**
 * Collects at most one eligible payment each turn. Owner: Ochi Yuma.
 */
public final class Cashier extends Staff {
    public Cashier(String name) {
        super(name);
    }

    /**
     * Validate this customer is Restaurant.nextReadyToPay(turn)'s oldest eligible one.
     * Delegate Restaurant.recordPayment(customer, turn); mark acted only on success.
     * GameMgr calls this automatically; the manager does not choose who pays.
     */
    public void collect(Restaurant restaurant, Customer customer, int turn) {
        requireAvailable();
        if (restaurant == null || customer == null) {
            throw new IllegalArgumentException("Restaurant and customer must not be null");
        }
        if (turn < customer.getArrivalTurn()) {
            throw new IllegalArgumentException("Payment turn cannot precede arrival");
        }
        Customer next = restaurant.nextReadyToPay(turn)
                .orElseThrow(() -> new IllegalStateException("No customer is eligible to pay"));
        if (next != customer) {
            throw new IllegalStateException("Collect from the oldest eligible customer first");
        }
        restaurant.recordPayment(customer, turn);
        markActed();
    }
}
