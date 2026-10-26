package restaurant.domain;

/**
 * Stage 1 prototype. Owner: Mingchen.
 * See docs/stage-1-contracts.md before implementing TODO methods.
 */
public abstract class Customer {
    private final int customerId;
    private final int arrivalTurn;
    private int satisfaction;
    private CustomerState state;
    private Order order;
    private int servedTurn;
    private int readyToPayTurn;

    protected Customer(int customerId, int arrivalTurn) {
        // TODO Mingchen: validate a positive id and a valid arrival turn.
        this.customerId = customerId;
        this.arrivalTurn = arrivalTurn;
        this.satisfaction = 100;
        this.state = CustomerState.WAITING;
        this.servedTurn = -1;
        this.readyToPayTurn = -1;
    }

    public int getCustomerId() { return customerId; }
    public int getArrivalTurn() { return arrivalTurn; }
    public int getSatisfaction() { return satisfaction; }
    public CustomerState getState() { return state; }
    /** Null until the Waiter records this customer's order. */
    public Order getOrder() { return order; }
    public int getServedTurn() { return servedTurn; }
    public int getReadyToPayTurn() { return readyToPayTurn; }

    /** P1: return a non-negative penalty; callers must not inspect the subclass. */
    public abstract int waitingPenalty();

    /** The customer chooses; document a deterministic selection rule when implementing. */
    public MenuItem chooseFood(Menu menu) {
        throw new UnsupportedOperationException("TODO: Mingchen - customer-owned food selection");
    }

    /** Require WAITING, then move to SEATED. */
    public void markSeated() {
        throw new UnsupportedOperationException("TODO: Mingchen - WAITING to SEATED");
    }

    /** Require SEATED with no existing order; ordering does not change CustomerState. */
    public void placeOrder(Order order) {
        throw new UnsupportedOperationException("TODO: Mingchen - attach one order");
    }

    /** Require SEATED and an order already marked SERVED; record the serving turn. */
    public void markServed(int turn) {
        throw new UnsupportedOperationException("TODO: Mingchen - SEATED to EATING");
    }

    /** EATING becomes READY_TO_PAY only on a turn later than servedTurn. */
    public void advanceEating(int turn) {
        throw new UnsupportedOperationException("TODO: Mingchen - eating progression and readiness turn");
    }

    /** Require READY_TO_PAY and an order already marked PAID. */
    public void markPaid() {
        throw new UnsupportedOperationException("TODO: Mingchen - READY_TO_PAY to PAID");
    }

    /** Only waiting/unserved seated customers lose points; clamp at zero. */
    public void loseSatisfaction(int points) {
        throw new UnsupportedOperationException("TODO: Mingchen - private satisfaction update");
    }

    /** At zero satisfaction, a waiting or unserved seated customer may become LEFT. */
    public void leave() {
        throw new UnsupportedOperationException("TODO: Mingchen - abandonment state transition");
    }
}
