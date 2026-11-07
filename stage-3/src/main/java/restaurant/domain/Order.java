package restaurant.domain;

public final class Order {
    private final MenuItem item;
    private final long priceCents;
    private int preparationUnitsDone;
    private OrderStatus status;

    public Order(MenuItem item) {
        if (item == null) {
            throw new IllegalArgumentException("Order item must not be null");
        }
        this.item = item;
        this.priceCents = item.getPriceCents();
        this.preparationUnitsDone = 0;
        this.status = OrderStatus.PLACED;
    }

    public Order(MenuItem item, long priceCents) {
        if (item == null) {
            throw new IllegalArgumentException("Order item must not be null");
        }
        if (priceCents < 0) {
            throw new IllegalArgumentException("Order price must not be negative");
        }
        this.item = item;
        this.priceCents = priceCents;
        this.preparationUnitsDone = 0;
        this.status = OrderStatus.PLACED;
    }

    public MenuItem getItem() { return item; }
    public long getPriceCents() { return priceCents; }
    public int getPreparationUnitsDone() { return preparationUnitsDone; }
    public OrderStatus getStatus() { return status; }

    public void advanceOneUnit() {
        if (status != OrderStatus.PLACED) {
            throw new IllegalStateException("Only placed orders can be prepared");
        }
        preparationUnitsDone++;
        if (preparationUnitsDone >= item.getPreparationUnits()) {
            status = OrderStatus.READY;
        }
    }

    public void markServed() {
        if (status != OrderStatus.READY) {
            throw new IllegalStateException("Order is not ready to serve");
        }
        status = OrderStatus.SERVED;
    }

    public void markPaid() {
        if (status != OrderStatus.SERVED) {
            throw new IllegalStateException("Only served orders can be paid");
        }
        status = OrderStatus.PAID;
    }

    public void cancel() {
        if (status != OrderStatus.PLACED && status != OrderStatus.READY) {
            throw new IllegalStateException("Cannot cancel an order that is " + status);
        }
        status = OrderStatus.CANCELLED;
    }
}
