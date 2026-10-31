package restaurant.domain;

public final class Order {
    private final MenuItem item;
    private OrderStatus status = OrderStatus.PLACED;
    int units;
    int serveCalls;
    boolean rejectPreparation;

    public Order(MenuItem item) { this.item = item; }
    public OrderStatus getStatus() { return status; }
    public long getPriceCents() { return item.getPriceCents(); }
    public void advanceOneUnit() {
        if (rejectPreparation || status != OrderStatus.PLACED) {
            throw new IllegalStateException("Preparation rejected");
        }
        if (++units == item.getPreparationUnits()) { status = OrderStatus.READY; }
    }
    public void markServed() {
        if (status != OrderStatus.READY) { throw new IllegalStateException("Not ready"); }
        serveCalls++;
        status = OrderStatus.SERVED;
    }
}
