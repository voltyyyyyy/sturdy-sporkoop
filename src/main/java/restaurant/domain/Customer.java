package restaurant.domain;




public abstract class Customer {
    private final int customerId;
    private final int arrivalTurn;
    private int satisfaction;
    private CustomerState state;
    private Order order;
    private int servedTurn;
    private int readyToPayTurn;

    protected Customer(int customerId, int arrivalTurn) {
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
    public Order getOrder() { return order; }
    public int getServedTurn() { return servedTurn; }
    public int getReadyToPayTurn() { return readyToPayTurn; }

    public abstract int waitingPenalty();

    public MenuItem chooseFood(Menu menu) {
        throw new UnsupportedOperationException("TODO: Mingchen - customer-owned food selection");
    }

    public void markSeated() {
        throw new UnsupportedOperationException("TODO: Mingchen - WAITING to SEATED");
    }

    public void placeOrder(Order order) {
        throw new UnsupportedOperationException("TODO: Mingchen - attach one order");
    }

    public void markServed(int turn) {
        throw new UnsupportedOperationException("TODO: Mingchen - SEATED to EATING");
    }

    public void advanceEating(int turn) {
        throw new UnsupportedOperationException("TODO: Mingchen - eating progression and readiness turn");
    }

    public void markPaid() {
        throw new UnsupportedOperationException("TODO: Mingchen - READY_TO_PAY to PAID");
    }

    public void loseSatisfaction(int points) {
        throw new UnsupportedOperationException("TODO: Mingchen - private satisfaction update");
    }

    public void leave() {
        throw new UnsupportedOperationException("TODO: Mingchen - abandonment state transition");
    }
}
