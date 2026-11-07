package restaurant.domain;


public final class Waiter extends Staff {
    public Waiter(String name) {
        super(name);
    }


    public void seat(Restaurant restaurant, Customer customer, Table table) {
        requireAvailable();
        if (restaurant == null || customer == null || table == null) {
            throw new IllegalArgumentException("Restaurant, customer and table must not be null");
        }
        if (!restaurant.getTables().contains(table)) {
            throw new IllegalArgumentException("Table does not belong to this restaurant");
        }
        if (!table.isFree()) {
            throw new IllegalStateException("Table is occupied");
        }
        if (customer.getState() != CustomerState.WAITING) {
            throw new IllegalStateException("Only waiting customers can be seated");
        }
        Customer oldest = restaurant.getWaitingQueue().peek()
                .orElseThrow(() -> new IllegalStateException("Nobody is waiting"));
        if (oldest != customer) {
            throw new IllegalStateException("Seat the oldest queued customer first");
        }
        table.seat(customer);
        customer.markSeated();
        restaurant.getWaitingQueue().remove(customer);
        markActed();
    }

    public Order takeOrder(Restaurant restaurant, Customer customer) {
        requireAvailable();
        requireSeatedCustomer(restaurant, customer);
        if (customer.getOrder() != null) {
            throw new IllegalStateException("Customer already has an order");
        }
        MenuItem item = customer.chooseFood(restaurant.getMenu());
        if (item == null) {
            throw new IllegalArgumentException("Customer must choose a menu item");
        }
        long priceCents = customer.discountedPrice(item.getPriceCents());
        if (restaurant.getHappyHour().isActive()) {
            priceCents = restaurant.getHappyHour().applyDiscount(priceCents);
        }
        Order order = new Order(item, priceCents);
        customer.placeOrder(order);
        markActed();
        return order;
    }

    public void serve(Restaurant restaurant, Customer customer, int turn) {
        requireAvailable();
        requireSeatedCustomer(restaurant, customer);
        if (turn < customer.getArrivalTurn()) {
            throw new IllegalArgumentException("Serving turn cannot precede arrival");
        }
        Order order = customer.getOrder();
        if (order == null || order.getStatus() != OrderStatus.READY) {
            throw new IllegalStateException("Customer must have a ready order");
        }
        order.markServed();
        customer.markServed(turn);
        markActed();
    }

    private void requireSeatedCustomer(Restaurant restaurant, Customer customer) {
        if (restaurant == null || customer == null) {
            throw new IllegalArgumentException("Restaurant and customer must not be null");
        }
        if (customer.getState() != CustomerState.SEATED) {
            throw new IllegalStateException("Customer must be seated");
        }
        boolean present = restaurant.getTables().stream()
                .anyMatch(table -> table.getOccupant() == customer);
        if (!present) {
            throw new IllegalArgumentException("Customer is not seated in this restaurant");
        }
    }
}
