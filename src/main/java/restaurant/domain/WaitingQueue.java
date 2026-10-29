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

    public Optional<Customer> peek() {
        if (customers.isEmpty()){
            return Optional.empty();
        }
        return Optional.of(customers.get(0));
    }

    public void remove(Customer customer) {
        if (customer == null) {
            throw new IllegalArgumentException("Cannot remove a null customer");
        }
        if (!customers.remove(customer)) {
            throw new IllegalArgumentException("Customer is not in this queue");
        }
    }
}
