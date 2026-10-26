package restaurant.domain;

/**
 * Stage 1 prototype. Owner: Yuma.
 * See docs/stage-1-contracts.md before implementing TODO methods.
 */
public abstract class Staff {
    private final String name;
    private boolean actedThisTurn;

    protected Staff(String name) {
        // TODO Yuma: validate name.
        this.name = name;
        this.actedThisTurn = false;
    }

    public String getName() { return name; }
    public boolean hasActedThisTurn() { return actedThisTurn; }

    /** GameMgr resets every staff member, including Cashier, at turn start. */
    public final void resetForTurn() {
        throw new UnsupportedOperationException("TODO: Yuma - reset the one-task flag");
    }

    /** Shared guard. An invalid attempt must not consume the task. */
    protected final void requireAvailable() {
        throw new UnsupportedOperationException("TODO: Yuma - reject a second task");
    }

    /** Call only after a complete successful action. */
    protected final void markActed() {
        throw new UnsupportedOperationException("TODO: Yuma - record a successful task");
    }
}
