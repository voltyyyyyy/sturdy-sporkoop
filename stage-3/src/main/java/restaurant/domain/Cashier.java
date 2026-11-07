package restaurant.domain;

public final class Cashier extends Staff {
    public Cashier(String name) {
        super(name);
    }

    public void collect(Restaurant restaurant, Customer customer, int turn) {
        requireAvailable();
        if (restaurant == null || customer == null) {
            throw new IllegalArgumentException("Restaurant and customer must not be null");
        }
        if (turn < customer.getArrivalTurn()) {
            throw new IllegalArgumentException("Payment turn cannot precede arrival");
        }
        Customer next = restaurant.nextReadyToPay(turn)
                .orElseThrow(() -> new IllegalStateException("No customer is eligible to pay"));
        if (next != customer) {
            throw new IllegalStateException("Collect from the oldest eligible customer first");
        }
        restaurant.recordPayment(customer, turn);
        markActed();
    }
}
