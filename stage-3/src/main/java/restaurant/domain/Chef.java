package restaurant.domain;

public final class Chef extends Staff {
    public Chef(String name) {
        super(name);
    }

    public void prepare(Order order) {
        requireAvailable();
        if (order == null) {
            throw new IllegalArgumentException("Order must not be null");
        }
        if (order.getStatus() != OrderStatus.PLACED) {
            throw new IllegalStateException("Only placed orders can be prepared");
        }
        order.advanceOneUnit();
        markActed();
    }
}
