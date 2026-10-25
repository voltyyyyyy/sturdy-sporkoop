package restaurant.domain;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Objects;

/** FIFO queue of customers waiting to be seated. */
public final class WaitingQueue {

    private final Deque<Customer> customers = new ArrayDeque<>();

    public void enqueue(Customer customer) {
        customers.addLast(Objects.requireNonNull(customer, "customer"));
    }

    public boolean remove(Customer customer) {
        return customers.remove(customer);
    }

    public Customer oldest() {
        Customer customer = customers.peekFirst();
        if (customer == null) {
            throw new IllegalActionException("The waiting queue is empty.");
        }
        return customer;
    }

    public boolean contains(Customer customer) {
        return customers.contains(customer);
    }

    public boolean isEmpty() {
        return customers.isEmpty();
    }

    public int size() {
        return customers.size();
    }

    public List<Customer> snapshot() {
        return List.copyOf(customers);
    }
}
