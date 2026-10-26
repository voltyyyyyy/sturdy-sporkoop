package restaurant.domain;

/**
 * Stage 1 prototype. Owner: Yuma.
 * See docs/stage-1-contracts.md before implementing TODO methods.
 */
public final class Chef extends Staff {
    public Chef(String name) {
        super(name);
    }

    /** Advance one eligible order by one unit and consume the Chef's single task. */
    public void prepare(Order order) {
        throw new UnsupportedOperationException("TODO: Yuma - delegate one preparation unit to Order");
    }
}
