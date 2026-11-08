package restaurant.domain;

import java.util.Optional;

public final class Host extends Staff {
    public Host(String name) {
        super(name);
    }

    public boolean seatNext(Restaurant restaurant) {
        requireAvailable();
        if (restaurant == null) {
            throw new IllegalArgumentException("Restaurant must not be null");
        }
        Optional<Customer> next = restaurant.getWaitingQueue().peek();
        Optional<Table> freeTable = restaurant.getTables().stream()
                .filter(Table::isFree)
                .findFirst();
        if (next.isEmpty() || freeTable.isEmpty()) {
            return false;
        }
        Customer customer = next.get();
        Table table = freeTable.get();
        if (customer.getState() != CustomerState.WAITING) {
            throw new IllegalStateException("Only waiting customers can be seated");
        }
        table.seat(customer);
        customer.markSeated();
        restaurant.getWaitingQueue().remove(customer);
        markActed();
        return true;
    }
}
