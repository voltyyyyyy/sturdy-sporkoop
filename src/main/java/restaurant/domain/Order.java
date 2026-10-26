package restaurant.domain;

/**
 * Stage 1 prototype. Owner: Karthik.
 * See docs/stage-1-contracts.md before implementing TODO methods.
 */
public final class Order {
    private final MenuItem item;
    // E1: captured once at creation, never recomputed from the menu.
    private final long priceCents;
    private int preparationUnitsDone;
    private OrderStatus status;

    public Order(MenuItem item) {
        // TODO Karthik: validate item before using it.
        this.item = item;
        this.priceCents = item.getPriceCents();
        this.preparationUnitsDone = 0;
        this.status = OrderStatus.PLACED;
    }

    public MenuItem getItem() { return item; }
    public long getPriceCents() { return priceCents; }
    public int getPreparationUnitsDone() { return preparationUnitsDone; }
    public OrderStatus getStatus() { return status; }

    /** Advance exactly one unit while PLACED; the final unit changes status to READY. */
    public void advanceOneUnit() {
        throw new UnsupportedOperationException("TODO: Karthik - guarded preparation progress");
    }

    /** Require READY, then change status to SERVED. */
    public void markServed() {
        throw new UnsupportedOperationException("TODO: Karthik - READY to SERVED");
    }

    /** E1: refuse payment unless status is already SERVED. */
    public void markPaid() {
        throw new UnsupportedOperationException("TODO: Karthik - SERVED to PAID");
    }

    /** Cancel an unserved order on abandonment; reject a served or paid order. */
    public void cancel() {
        throw new UnsupportedOperationException("TODO: Karthik - cancellation guards");
    }
}
