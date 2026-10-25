package restaurant.domain;

import java.util.Objects;

/** One customer's order and its preparation lifecycle. */
public final class Order {

    private final int orderId;
    private final Customer customer;
    private final MenuItem item;
    private final int priceCents;
    private final int turnPlaced;

    private OrderStatus status;
    private int unitsDone;
    private int turnReady;
    private int turnServed;

    public Order(int orderId, Customer customer, MenuItem item, int priceCents, int turnPlaced) {
        if (orderId <= 0) {
            throw new IllegalArgumentException("Order id must be positive.");
        }
        if (priceCents < 0) {
            throw new IllegalArgumentException("Price cannot be negative.");
        }
        if (turnPlaced <= 0) {
            throw new IllegalArgumentException("Turn placed must be positive.");
        }
        this.orderId = orderId;
        this.customer = Objects.requireNonNull(customer, "customer");
        this.item = Objects.requireNonNull(item, "item");
        this.priceCents = priceCents;
        this.turnPlaced = turnPlaced;
        this.status = OrderStatus.PLACED;
        this.turnReady = -1;
        this.turnServed = -1;
    }

    /** Advances preparation by one unit and marks the order READY on the last unit. */
    public void advancePrepUnit(int turn) {
        requireStatus(OrderStatus.PLACED, "advance preparation");
        unitsDone++;
        if (unitsDone == item.getPrepUnits()) {
            markReady(turn);
        }
    }

    private void markReady(int turn) {
        status = OrderStatus.READY;
        turnReady = turn;
    }

    public void markServed(int turn) {
        requireStatus(OrderStatus.READY, "be served");
        status = OrderStatus.SERVED;
        turnServed = turn;
    }

    public void markPaid() {
        requireStatus(OrderStatus.SERVED, "be paid");
        status = OrderStatus.PAID;
    }

    public void cancel() {
        if (status != OrderStatus.PLACED && status != OrderStatus.READY) {
            throw new IllegalActionException(
                    "Order #" + orderId + " cannot be cancelled while " + status + ".");
        }
        status = OrderStatus.CANCELLED;
    }

    public boolean isReady() {
        return status == OrderStatus.READY;
    }

    /** True once the order has been served; remains true after payment. */
    public boolean isServed() {
        return status == OrderStatus.SERVED || status == OrderStatus.PAID;
    }

    public boolean isActive() {
        return status == OrderStatus.PLACED || status == OrderStatus.READY;
    }

    public String progressText() {
        return unitsDone + "/" + item.getPrepUnits();
    }

    private void requireStatus(OrderStatus expected, String action) {
        if (status != expected) {
            throw new IllegalActionException(
                    "Order #" + orderId + " cannot " + action + " while " + status + ".");
        }
    }

    public int getOrderId() {
        return orderId;
    }

    public Customer getCustomer() {
        return customer;
    }

    public MenuItem getItem() {
        return item;
    }

    public int getPriceCents() {
        return priceCents;
    }

    public int getTurnPlaced() {
        return turnPlaced;
    }

    public int getTurnReady() {
        return turnReady;
    }

    public int getTurnServed() {
        return turnServed;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public int getUnitsDone() {
        return unitsDone;
    }
}
