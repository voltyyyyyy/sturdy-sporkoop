package restaurant.ui;

import java.util.Scanner;

import restaurant.domain.Cashier;
import restaurant.domain.Chef;
import restaurant.domain.GameResult;
import restaurant.domain.Restaurant;
import restaurant.domain.Waiter;

/**
 * Stage 1 prototype. Owner: Pluem.
 * See docs/stage-1-contracts.md before implementing TODO methods.
 */
public final class ConsoleUI {
    private final Scanner input;

    public ConsoleUI() {
        this.input = new Scanner(System.in);
    }

    /** Display turn, score, queue, tables, satisfaction, order states and staff availability. */
    public void showTurnStart(int turn, Restaurant restaurant,
                              Waiter waiter, Chef chef, Cashier cashier) {
        throw new UnsupportedOperationException("TODO: Pluem - start-of-turn display");
    }

    /**
     * Read a Waiter task code: 0=wait, 1=seat, 2=take order, 3=serve.
     * These codes are input choices, not extra domain types.
     * Validate numeric input here; leave game-action legality to domain methods.
     */
    public int askWaiterTask() {
        throw new UnsupportedOperationException("TODO: Pluem - Waiter task input");
    }

    /** Read a Chef task code: 0=wait, 1=prepare. */
    public int askChefTask() {
        throw new UnsupportedOperationException("TODO: Pluem - Chef task input");
    }

    /** Read a displayed customer id. GameMgr resolves it through the domain. */
    public int askCustomerId() {
        throw new UnsupportedOperationException("TODO: Pluem - customer selection input");
    }

    /** Read a displayed table id. GameMgr resolves it through Restaurant. */
    public int askTableId() {
        throw new UnsupportedOperationException("TODO: Pluem - table selection input");
    }

    public void showError(String message) {
        throw new UnsupportedOperationException("TODO: Pluem - invalid-choice explanation");
    }

    public void showMessage(String message) {
        throw new UnsupportedOperationException("TODO: Pluem - event message output");
    }

    /** Display getter values; do not calculate revenue, averages or victory. */
    public void showTurnEnd(int turn, Restaurant restaurant) {
        throw new UnsupportedOperationException("TODO: Pluem - end-of-turn display");
    }

    /** Read GameResult only; display its verdict without recomputing the rules. */
    public void showResult(GameResult result) {
        throw new UnsupportedOperationException("TODO: Pluem - final result display");
    }

    /** Presentation-only conversion: long cents to dollars with exactly two decimals. */
    private String formatMoney(long cents) {
        throw new UnsupportedOperationException("TODO: Pluem - boundary money formatting");
    }
}
