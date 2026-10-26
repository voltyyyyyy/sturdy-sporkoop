package restaurant.domain;




public final class Order {
    private final MenuItem item;
    private final long priceCents;
    private int preparationUnitsDone;
    private OrderStatus status;

    public Order(MenuItem item) {
        this.item = item;
        this.priceCents = item.getPriceCents();
        this.preparationUnitsDone = 0;
        this.status = OrderStatus.PLACED;
    }

    public MenuItem getItem() { return item; }
    public long getPriceCents() { return priceCents; }
    public int getPreparationUnitsDone() { return preparationUnitsDone; }
    public OrderStatus getStatus() { return status; }

    public void advanceOneUnit() {
        throw new UnsupportedOperationException("TODO: Karthik - guarded preparation progress");
    }

    public void markServed() {
        throw new UnsupportedOperationException("TODO: Karthik - READY to SERVED");
    }

    public void markPaid() {
        throw new UnsupportedOperationException("TODO: Karthik - SERVED to PAID");
    }

    public void cancel() {
        throw new UnsupportedOperationException("TODO: Karthik - cancellation guards");
    }
}
