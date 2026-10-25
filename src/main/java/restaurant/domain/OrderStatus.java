package restaurant.domain;

/** The lifecycle states of an order. */
public enum OrderStatus {
    PLACED,
    READY,
    SERVED,
    PAID,
    CANCELLED
}
