package restaurant.domain;

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

    public final void resetForTurn() {
        actedThisTurn = false;
    }

    protected final void requireAvailable() {
        if (actedThisTurn) {
            throw new IllegalStateException(name + " has already acted this turn");
        }
    }

    protected final void markActed() {
        requireAvailable();
        actedThisTurn = true;
    }
}
