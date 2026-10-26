package restaurant.ui;

import java.util.Scanner;

import restaurant.domain.Cashier;
import restaurant.domain.Chef;
import restaurant.domain.GameResult;
import restaurant.domain.Restaurant;
import restaurant.domain.Waiter;




public final class ConsoleUI {
    private final Scanner input;

    public ConsoleUI() {
        this.input = new Scanner(System.in);
    }

    public void showTurnStart(int turn, Restaurant restaurant,
                              Waiter waiter, Chef chef, Cashier cashier) {
        throw new UnsupportedOperationException("TODO: Pluem - start-of-turn display");
    }





    public int askWaiterTask() {
        throw new UnsupportedOperationException("TODO: Pluem - Waiter task input");
    }

    public int askChefTask() {
        throw new UnsupportedOperationException("TODO: Pluem - Chef task input");
    }

    public int askCustomerId() {
        throw new UnsupportedOperationException("TODO: Pluem - customer selection input");
    }

    public int askTableId() {
        throw new UnsupportedOperationException("TODO: Pluem - table selection input");
    }

    public void showError(String message) {
        throw new UnsupportedOperationException("TODO: Pluem - invalid-choice explanation");
    }

    public void showMessage(String message) {
        throw new UnsupportedOperationException("TODO: Pluem - event message output");
    }

    public void showTurnEnd(int turn, Restaurant restaurant) {
        throw new UnsupportedOperationException("TODO: Pluem - end-of-turn display");
    }

    public void showResult(GameResult result) {
        throw new UnsupportedOperationException("TODO: Pluem - final result display");
    }

    private String formatMoney(long cents) {
        throw new UnsupportedOperationException("TODO: Pluem - boundary money formatting");
    }
}
