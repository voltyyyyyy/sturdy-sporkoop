package restaurant.domain;

/** Test double only; never included in the production build. */
public final class Order {
    private final MenuItem item;
    private final long priceCents;
    private OrderStatus status = OrderStatus.PLACED;
    int paidCalls;
    int cancelCalls;

    public Order(MenuItem item) {
        this.item = item;
        this.priceCents = item.getPriceCents();
    }

    public MenuItem getItem() { return item; }
    public long getPriceCents() { return priceCents; }
    public OrderStatus getStatus() { return status; }

    /** Test shortcut for the Chef's work: jump straight to READY. */
    void finishPreparation() {
        if (status != OrderStatus.PLACED) { throw new IllegalStateException("Not placed"); }
        status = OrderStatus.READY;
    }
    public void markServed() {
        if (status != OrderStatus.READY) { throw new IllegalStateException("Not ready"); }
        status = OrderStatus.SERVED;
    }
    public void markPaid() {
        if (status != OrderStatus.SERVED) { throw new IllegalStateException("Not served"); }
        paidCalls++;
        status = OrderStatus.PAID;
    }
    public void cancel() {
        if (status == OrderStatus.SERVED || status == OrderStatus.PAID) {
            throw new IllegalStateException("Cannot cancel a served or paid order");
        }
        cancelCalls++;
        status = OrderStatus.CANCELLED;
    }
}
