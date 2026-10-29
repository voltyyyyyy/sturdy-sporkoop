package restaurant.domain;

/**
 * Stage 1 implementation. Owner: Karthik.
 * See docs/stage-1-contracts.md for the module contract.
 */
public final class Order {
    private final MenuItem item;
    // E1: captured once at creation, never recomputed from the menu.
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

    public MenuItem getItem() { return item; }
    public long getPriceCents() { return priceCents; }
    public int getPreparationUnitsDone() { return preparationUnitsDone; }
    public OrderStatus getStatus() { return status; }

    /** Advance exactly one unit while PLACED; the final unit changes status to READY. */
    public void advanceOneUnit() {
        if (status != OrderStatus.PLACED) {
            throw new IllegalStateException("Only placed orders can be prepared");
        }
        preparationUnitsDone++;
        if (preparationUnitsDone >= item.getPreparationUnits()) {
            status = OrderStatus.READY;
        }
    }

    /** Require READY, then change status to SERVED. */
    public void markServed() {
        if (status != OrderStatus.READY) {
            throw new IllegalStateException("Order is not ready to serve");
        }
        status = OrderStatus.SERVED;
    }

    /** E1: refuse payment unless status is already SERVED. */
    public void markPaid() {
        if (status != OrderStatus.SERVED) {
            throw new IllegalStateException("Only served orders can be paid");
        }
        status = OrderStatus.PAID;
    }

    /** Cancel a placed or ready order on abandonment; cancelled orders are terminal. */
    public void cancel() {
        if (status != OrderStatus.PLACED && status != OrderStatus.READY) {
            throw new IllegalStateException("Cannot cancel an order that is " + status);
        }
        status = OrderStatus.CANCELLED;
    }
}
