package restaurant.control;

import restaurant.domain.Cashier;
import restaurant.domain.Chef;
import restaurant.domain.Restaurant;
import restaurant.domain.Waiter;
import restaurant.ui.ConsoleUI;

/**
 * Stage 1 prototype. Owner: Pluem.
 * See docs/stage-1-contracts.md before implementing TODO methods.
 */
public final class GameMgr {
    private static final int TOTAL_TURNS = 18;
    private static final int[] ARRIVAL_TURNS = {1, 4, 7, 10, 13, 16};

    // Deliberately concrete ConsoleUI: DIP is partial, as stated in the report.
    private final ConsoleUI ui;
    private final Restaurant restaurant;
    private final Waiter waiter;
    private final Chef chef;
    private final Cashier cashier;
    private int currentTurn;
    private int nextCustomerId;

    public GameMgr(ConsoleUI ui) {
        this.ui = ui;
        this.restaurant = new Restaurant();
        this.waiter = new Waiter("Waiter");
        this.chef = new Chef("Chef");
        this.cashier = new Cashier("Cashier");
        this.currentTurn = 0;
        this.nextCustomerId = 1;
    }

    /** Entry point lives here so Stage 1 has exactly the report's 17 types, no Main class. */
    public static void main(String[] args) {
        new GameMgr(new ConsoleUI()).run();
    }

    /** Run every turn 1..18, then obtain Restaurant.createResult(18) and display it. */
    public void run() {
        throw new UnsupportedOperationException("TODO: Pluem - 18-turn game loop");
    }

    /**
     * Reset all staff once, display the start state, then sequence the six steps:
     * 1 arrivals; 2 manager choices; 3 Waiter before Chef;
     * 4 eating and automatic payment; 5 waiting/abandonment; 6 result.
     * Input prompting may be interleaved with execution so the Chef can select an
     * order just taken by the Waiter. A new READY dish cannot be served this turn.
     * Keep arithmetic and action validation in the domain; no console I/O here.
     */
    private void runTurn(int turn) {
        throw new UnsupportedOperationException("TODO: Pluem - six-step turn sequencing");
    }

    /** Create RegularCustomer externally to the queue only on ARRIVAL_TURNS. */
    private void processArrivals(int turn) {
        throw new UnsupportedOperationException("TODO: Pluem - scheduled arrivals");
    }

    /** Dispatch the selected task to Waiter; retry rejected choices without spending its task. */
    private void handleWaiterChoice(int turn) {
        throw new UnsupportedOperationException("TODO: Pluem - Waiter task dispatch and retries");
    }

    /** Select a seated customer's order; dispatch to Chef or wait. Retry invalid choices. */
    private void handleChefChoice() {
        throw new UnsupportedOperationException("TODO: Pluem - Chef task dispatch and retries");
    }

    /**
     * Delegate eating to Restaurant.advanceEating(turn), then ask nextReadyToPay(turn).
     * If present, call Cashier.collect(). Newly ready customers must wait until next turn.
     */
    private void processEatingAndPayment(int turn) {
        throw new UnsupportedOperationException("TODO: Pluem - eating and automatic payment sequencing");
    }
}
