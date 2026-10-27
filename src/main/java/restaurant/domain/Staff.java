package restaurant.domain;

/**
 * Shared one-action-per-turn behaviour for staff. Owner: Yuma.
 */
public abstract class Staff {
    private final String name;
    private boolean actedThisTurn;

    protected Staff(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Staff name must not be blank");
        }
        this.name = name;
        this.actedThisTurn = false;
    }

    public String getName() { return name; }
    public boolean hasActedThisTurn() { return actedThisTurn; }

    /** GameMgr resets every staff member, including Cashier, at turn start. */
    public final void resetForTurn() {
        actedThisTurn = false;
    }

    /** Shared guard. An invalid attempt must not consume the task. */
    protected final void requireAvailable() {
        if (actedThisTurn) {
            throw new IllegalStateException(name + " has already acted this turn");
        }
    }

    /** Call only after a complete successful action. */
    protected final void markActed() {
        requireAvailable();
        actedThisTurn = true;
    }
}
