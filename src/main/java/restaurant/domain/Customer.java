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
        if(customerId<=0){
            throw new IllegalArgumentException("CUstomer ID must be positive");
        }
        this.customerId = customerId;
        if(arrivalTurn<1 || arrivalTurn>18){
            throw new IllegalArgumentException("Arrival turn must be between 1 and 18");
        }
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
        if (menu==null) {
            throw new IllegalArgumentException("Menu missing");
        }
        return menu.getItems().get(0);
    }

    public void markSeated() {

        if(state!=CustomerState.WAITING){
            throw new IllegalStateException("Only waiting customers can be seated");
        }
        state=CustomerState.SEATED;
    }


    public void placeOrder(Order order) {
        if (order==null){
            throw new IllegalArgumentException("Order must not be null");
        }
        if(state!=CustomerState.SEATED){
            throw new IllegalStateException("Customer must be seated to order");
        }
        if(this.order!=null){
            throw new IllegalStateException("Customer already has an order");
        }
        this.order=order;
    }

    public void markServed(int turn) {
        if(turn<arrivalTurn){
            throw new IllegalArgumentException("serving turn cannot precede arrival");
        }
        if(state!=CustomerState.SEATED || order==null || order.getStatus()!=OrderStatus.SERVED){
            throw new IllegalStateException("Customer must be seated with a served order");
        }
        servedTurn=turn;
        state=CustomerState.EATING;
    }

    public void advanceEating(int turn) {
        if(turn<arrivalTurn){
            throw new IllegalArgumentException("Eating turn cannot precede arrival");
        }
        if(state==CustomerState.EATING && turn >servedTurn){
            readyToPayTurn=turn;
            state=CustomerState.READY_TO_PAY;
        }
    }

    public void markPaid() {
        if(state!=CustomerState.READY_TO_PAY || order==null ||order.getStatus()!= OrderStatus.PAID) {
            throw new IllegalStateException("CUstomer must be ready with a paid order");
        }
        state=CustomerState.PAID;
    }

    public void loseSatisfaction(int points) {
        if(points<0){
            throw new IllegalArgumentException("Satisfaction loss must not be negative");
        }
        if(state !=CustomerState.WAITING && state!=CustomerState.SEATED){
            return;
        }
        if (order != null) {
            if (order.getStatus() == OrderStatus.SERVED) {
                return;
            }

            if (order.getStatus() == OrderStatus.PAID) {
                return;
            }
        }
        satisfaction = Math.max(0, satisfaction - points);
    }

    public void leave() {
        if (state != CustomerState.WAITING && state != CustomerState.SEATED) {
            throw new IllegalStateException("Only waiting or seated customers can leave");
        }

        if (satisfaction != 0) {
            throw new IllegalStateException("Customer can leave only at zero satisfaction");
        }

        state = CustomerState.LEFT;
    }
}
