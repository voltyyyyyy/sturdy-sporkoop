package restaurant.control;

import java.util.Objects;
import java.util.Optional;

import restaurant.domain.Customer;
import restaurant.domain.Restaurant;

public final class Cashier {

    private final Restaurant restaurant;

    public Cashier(Restaurant restaurant) {
        this.restaurant = Objects.requireNonNull(restaurant, "restaurant");
    }

    public Optional<Customer> collectNext(int turn) {
        return restaurant.payOldestReady(turn);
    }
}
