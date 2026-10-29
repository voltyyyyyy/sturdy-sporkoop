package restaurant.domain;

/**
 * Order lifecycle states. Owner: Karthik.
 */
public enum OrderStatus {
    PLACED,
    READY,
    SERVED,
    PAID,
    CANCELLED
}
