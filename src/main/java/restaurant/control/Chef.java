package restaurant.control;

import java.util.Objects;

import restaurant.domain.IllegalActionException;
import restaurant.domain.Order;
import restaurant.domain.Restaurant;

public final class Chef {

    private final Restaurant restaurant;
    private boolean actedThisTurn;

    public Chef(Restaurant restaurant) {
        this.restaurant = Objects.requireNonNull(restaurant, "restaurant");
    }

    public boolean available() {
        return !actedThisTurn;
    }

    public void prepare(Order order, int turn) {
        ensureAvailable();
        restaurant.advanceKitchen(order, turn);
        actedThisTurn = true;
    }

    public void resetForTurn() {
        actedThisTurn = false;
    }

    private void ensureAvailable() {
        if (actedThisTurn) {
            throw new IllegalActionException("The Chef has already acted this turn.");
        }
    }
}
