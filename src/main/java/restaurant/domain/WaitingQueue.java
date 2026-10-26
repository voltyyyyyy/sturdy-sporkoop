package restaurant.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Stage 1 prototype. Owner: Qiyun.
 * See docs/stage-1-contracts.md before implementing TODO methods.
 */
public final class WaitingQueue {
    // Aggregation: arrivals are created outside this queue and remain valid after removal.
    private final List<Customer> customers = new ArrayList<>();

    public WaitingQueue() { }

    /** Read-only snapshot; no caller can reorder the queue through this view. */
    public List<Customer> getCustomers() { return List.copyOf(customers); }
    public boolean isEmpty() { return customers.isEmpty(); }

    /** Add a WAITING customer once, preserving arrival order. */
    public void add(Customer customer) {
        throw new UnsupportedOperationException("TODO: Qiyun - arrival-ordered queue insertion");
    }

    /** Return the oldest queued customer; empty means nobody is waiting. */
    public Optional<Customer> peek() {
        throw new UnsupportedOperationException("TODO: Qiyun - oldest queued customer");
    }

    /** Remove after seating or abandonment; reject a customer not in this queue. */
    public void remove(Customer customer) {
        throw new UnsupportedOperationException("TODO: Qiyun - queue removal");
    }
}
