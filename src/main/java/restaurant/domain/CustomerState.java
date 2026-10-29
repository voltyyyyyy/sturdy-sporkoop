package restaurant.domain;

/**
 * Customer lifecycle states. Owner: Mingchen.
 */
public enum CustomerState {
    WAITING,
    SEATED,
    EATING,
    READY_TO_PAY,
    PAID,
    LEFT
}
