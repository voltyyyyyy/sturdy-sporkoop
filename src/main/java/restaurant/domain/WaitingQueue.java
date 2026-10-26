package restaurant.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;




public final class WaitingQueue {
    private final List<Customer> customers = new ArrayList<>();

    public WaitingQueue() { }

    public List<Customer> getCustomers() { return List.copyOf(customers); }
    public boolean isEmpty() { return customers.isEmpty(); }

    public void add(Customer customer) {
        throw new UnsupportedOperationException("TODO: Qiyun - arrival-ordered queue insertion");
    }

    public Optional<Customer> peek() {
        throw new UnsupportedOperationException("TODO: Qiyun - oldest queued customer");
    }

    public void remove(Customer customer) {
        throw new UnsupportedOperationException("TODO: Qiyun - queue removal");
    }
}
