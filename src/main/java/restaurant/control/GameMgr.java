package restaurant.control;

import restaurant.domain.Cashier;
import restaurant.domain.Chef;
import restaurant.domain.Restaurant;
import restaurant.domain.Waiter;
import restaurant.ui.ConsoleUI;




public final class GameMgr {
    private static final int TOTAL_TURNS = 18;
    private static final int[] ARRIVAL_TURNS = {1, 4, 7, 10, 13, 16};

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

    public static void main(String[] args) {
        new GameMgr(new ConsoleUI()).run();
    }

    public void run() {
        throw new UnsupportedOperationException("TODO: Pluem - 18-turn game loop");
    }








    private void runTurn(int turn) {
        throw new UnsupportedOperationException("TODO: Pluem - six-step turn sequencing");
    }

    private void processArrivals(int turn) {
        throw new UnsupportedOperationException("TODO: Pluem - scheduled arrivals");
    }

    private void handleWaiterChoice(int turn) {
        throw new UnsupportedOperationException("TODO: Pluem - Waiter task dispatch and retries");
    }

    private void handleChefChoice() {
        throw new UnsupportedOperationException("TODO: Pluem - Chef task dispatch and retries");
    }




    private void processEatingAndPayment(int turn) {
        throw new UnsupportedOperationException("TODO: Pluem - eating and automatic payment sequencing");
    }
}
