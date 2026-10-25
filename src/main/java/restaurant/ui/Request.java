package restaurant.ui;








public final class Request {

    private final Action action;
    private final int firstTarget;
    private final int secondTarget;

    private Request(Action action, int firstTarget, int secondTarget) {
        this.action = action;
        this.firstTarget = firstTarget;
        this.secondTarget = secondTarget;
    }

    public static Request idle() {
        return new Request(Action.WAIT, -1, -1);
    }

    public static Request seat(int customerIndex, int tableId) {
        if (customerIndex < 0) {
            throw new IllegalArgumentException("Customer index cannot be negative.");
        }
        if (tableId <= 0) {
            throw new IllegalArgumentException("Table id must be positive.");
        }
        return new Request(Action.SEAT, customerIndex, tableId);
    }

    public static Request takeOrder(int customerIndex) {
        if (customerIndex < 0) {
            throw new IllegalArgumentException("Customer index cannot be negative.");
        }
        return new Request(Action.TAKE_ORDER, customerIndex, -1);
    }

    public static Request serve(int orderId) {
        if (orderId <= 0) {
            throw new IllegalArgumentException("Order id must be positive.");
        }
        return new Request(Action.SERVE, orderId, -1);
    }

    public static Request prepare(int orderId) {
        if (orderId <= 0) {
            throw new IllegalArgumentException("Order id must be positive.");
        }
        return new Request(Action.PREPARE, orderId, -1);
    }

    public Action getAction() {
        return action;
    }

    public int getFirstTarget() {
        return firstTarget;
    }

    public int getSecondTarget() {
        return secondTarget;
    }
}
