package restaurant.domain;

/** Thrown when a game rule is violated. */
public final class IllegalActionException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public IllegalActionException(String message) {
        super(message);
    }
}
