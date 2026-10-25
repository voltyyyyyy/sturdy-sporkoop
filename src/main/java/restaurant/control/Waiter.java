package restaurant.control;

import java.util.Objects;

import restaurant.domain.Customer;
import restaurant.domain.IllegalActionException;
import restaurant.domain.Order;
import restaurant.domain.Restaurant;
import restaurant.domain.Table;

public final class Waiter {

    private final Restaurant restaurant;
    private boolean actedThisTurn;

    public Waiter(Restaurant restaurant) {
        this.restaurant = Objects.requireNonNull(restaurant, "restaurant");
    }

    public boolean available() {
        return !actedThisTurn;
    }

    public void seat(Customer customer, Table table) {
        ensureAvailable();
        restaurant.seat(customer, table);
        actedThisTurn = true;
    }

    public Order takeOrder(Customer customer, int turn) {
        ensureAvailable();
        Order order = restaurant.takeOrder(customer, turn);
        actedThisTurn = true;
        return order;
    }

    public void serve(Order order, int turn) {
        ensureAvailable();
        restaurant.serve(order, turn);
        actedThisTurn = true;
    }

    public void resetForTurn() {
        actedThisTurn = false;
    }

    private void ensureAvailable() {
        if (actedThisTurn) {
            throw new IllegalActionException("The Waiter has already acted this turn.");
        }
    }
}
