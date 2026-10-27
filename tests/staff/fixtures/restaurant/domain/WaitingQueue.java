package restaurant.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Test double only; never included in the production build. */
public final class WaitingQueue {
    final List<Customer> customers = new ArrayList<>();
    int removeCalls;
    public Optional<Customer> peek() { return customers.stream().findFirst(); }
    public void remove(Customer customer) {
        if (!customers.remove(customer)) { throw new IllegalStateException("Not queued"); }
        removeCalls++;
    }
}
