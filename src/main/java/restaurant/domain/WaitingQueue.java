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
        if (customer == null){
            throw new IllegalArgumentException("Cannot queue a null customer");
        }
        if (customer.getState()!= CustomerState.WAITING){
            throw new IllegalStateException("Only WAITING customers can join the queue, but the customer is " +  customer.getState());
        }
        if (customers.contains(customer)){
            throw new IllegalStateException("Customer already in queue!");
        }
        customers.add(customer);
    }

    /** Return the oldest queued customer; empty means nobody is waiting. */
    public Optional<Customer> peek() {
        if (customers.isEmpty()){
            return Optional.empty();
        }
        return Optional.of(customers.get(0));
    }

    /** Remove after seating or abandonment; reject a customer not in this queue. */
    public void remove(Customer customer) {
        if (customer == null) {
            throw new IllegalArgumentException("Cannot remove a null customer");
        }
        if (!customers.remove(customer)) {
            throw new IllegalArgumentException("Customer is not in this queue");
        }
    }
}
