package restaurant.domain;


//customer represents 1 person visiting the restaurant
//keeps track of ID, turn, satisfaction, status, order, turn their food was served, turn they were ready to pay

public abstract class Customer {
    private final int customerId;
    private final int arrivalTurn;
    private int satisfaction;
    private CustomerState state;
    private Order order;
    private int servedTurn;
    private int readyToPayTurn;

    //customer stores the customer's data and behaviour
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

    //get methods
    public int getCustomerId() { return customerId; }
    public int getArrivalTurn() { return arrivalTurn; }
    public int getSatisfaction() { return satisfaction; }
    public CustomerState getState() { return state; }
    public Order getOrder() { return order; }
    public int getServedTurn() { return servedTurn; }
    public int getReadyToPayTurn() { return readyToPayTurn; }

    //bill is the price locked into the order, so it is read from the order instead of stored twice
    //0 before ordering, or when the order was cancelled because the customer left
    public long getBillCents() {
        if (order==null || order.getStatus()==OrderStatus.CANCELLED) {
            return 0;
        }
        return order.getPriceCents();
    }

    //bill is owed from placing the order until the order is paid
    public boolean hasUnpaidBill() {
        return order!=null
                && order.getStatus()!=OrderStatus.CANCELLED
                && order.getStatus()!=OrderStatus.PAID;
    }


    public abstract int waitingPenalty();

    public MenuItem chooseFood(Menu menu) {
        if (menu==null) {
            throw new IllegalArgumentException("Menu missing");
        }
        return menu.getItems().get(0); // choosing the first item deterministically
    }

    //if customer is not waiting, reject, otherwise mark seated
    public void markSeated() {

        if(state!=CustomerState.WAITING){
            throw new IllegalStateException("Only waiting customers can be seated");
        }
        state=CustomerState.SEATED;
    }


    public void placeOrder(Order order) {
        //check order, state and current order
        //passed in order
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

    //mark customer as served, transition  to eating state
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

    //progress an eating customer to ready to pay
    public void advanceEating(int turn) {
        if(turn<arrivalTurn){
            throw new IllegalArgumentException("Eating turn cannot precede arrival");
        }
        if (state != CustomerState.EATING) {
            throw new IllegalStateException("Only an eating customer can become ready to pay");
        }
        if (turn < servedTurn) {
            throw new IllegalArgumentException("Eating turn cannot precede serving turn");
        }
        // Serving does not count as the full eating turn.
        if (turn > servedTurn) {
            readyToPayTurn=turn;
            state=CustomerState.READY_TO_PAY;
        }
    }

    //mark customer as paid
    public void markPaid() {
        if(state!=CustomerState.READY_TO_PAY || order==null ||order.getStatus()!= OrderStatus.PAID) {
            throw new IllegalStateException("CUstomer must be ready with a paid order");
        }
        state=CustomerState.PAID;
    }

    //customer starts with 100, loses 8 points while waiting to be seated/ order/ waiting for food
    //deducting for every turn
    public void loseSatisfaction(int points) {
        //must be non negative, subtracting would increase  satisfaction
        if(points<0){
            throw new IllegalArgumentException("Satisfaction loss must not be negative");
        }
        if(state !=CustomerState.WAITING && state!=CustomerState.SEATED){
            return;
        }
        // Check the status only if the customer has an order.
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

    //leave at zero satisfaction
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
