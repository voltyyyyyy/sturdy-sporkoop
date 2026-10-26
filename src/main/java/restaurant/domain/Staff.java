package restaurant.domain;




public abstract class Staff {
    private final String name;
    private boolean actedThisTurn;

    protected Staff(String name) {
        this.name = name;
        this.actedThisTurn = false;
    }

    public String getName() { return name; }
    public boolean hasActedThisTurn() { return actedThisTurn; }

    public final void resetForTurn() {
        throw new UnsupportedOperationException("TODO: Yuma - reset the one-task flag");
    }

    protected final void requireAvailable() {
        throw new UnsupportedOperationException("TODO: Yuma - reject a second task");
    }

    protected final void markActed() {
        throw new UnsupportedOperationException("TODO: Yuma - record a successful task");
    }
}
