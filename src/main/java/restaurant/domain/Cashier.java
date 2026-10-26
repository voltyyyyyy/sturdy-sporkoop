package restaurant.domain;

/**
 * Stage 1 prototype. Owner: Yuma.
 * See docs/stage-1-contracts.md before implementing TODO methods.
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
        throw new UnsupportedOperationException("TODO: Yuma - collect one eligible payment");
    }
}
