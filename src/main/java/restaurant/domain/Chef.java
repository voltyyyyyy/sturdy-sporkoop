package restaurant.domain;

/**
 * Prepares at most one order unit each turn. Owner: Yuma.
 */
public final class Chef extends Staff {
    public Chef(String name) {
        super(name);
    }

    /** Advance one eligible order by one unit and consume the Chef's single task. */
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
