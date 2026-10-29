package restaurant.control;

import restaurant.domain.Cashier;
import restaurant.domain.Chef;
import restaurant.domain.Customer;
import restaurant.domain.GameResult;
import restaurant.domain.Order;
import restaurant.domain.RegularCustomer;
import restaurant.domain.Restaurant;
import restaurant.domain.Table;
import restaurant.domain.Waiter;
import restaurant.ui.ConsoleUI;

import java.util.Optional;

/**
 * Runs the Stage 1 turn sequence. Console interaction is delegated to ConsoleUI.
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

    /** Run every turn 1..18, then display the result computed by Restaurant. */
    public void run() {
        for (currentTurn = 1; currentTurn <= TOTAL_TURNS; currentTurn++) {
            runTurn(currentTurn);
        }
        GameResult result = restaurant.createResult(TOTAL_TURNS);
        ui.showResult(result);
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
        waiter.resetForTurn();
        chef.resetForTurn();
        cashier.resetForTurn();

        Optional<Customer> arrival = processArrivals(turn);
        ui.showTurnStart(turn, restaurant, waiter, chef, cashier);
        arrival.ifPresent(customer -> ui.showMessage("Customer #" + customer.getCustomerId() + " arrived."));

        // Prompt and execute in sequence so Chef can select an order taken this turn.
        handleWaiterChoice(turn);
        handleChefChoice();

        processEatingAndPayment(turn);
        for (Customer customer : restaurant.applyWaitingPenalties()) {
            ui.showMessage("Customer #" + customer.getCustomerId()
                    + " left after losing all satisfaction.");
        }
        ui.showTurnEnd(turn, restaurant);
    }

    /** Create RegularCustomer externally to the queue only on ARRIVAL_TURNS. */
    private Optional<Customer> processArrivals(int turn) {
        for (int arrivalTurn : ARRIVAL_TURNS) {
            if (arrivalTurn == turn) {
                Customer customer = new RegularCustomer(nextCustomerId++, turn);
                restaurant.getWaitingQueue().add(customer);
                return Optional.of(customer);
            }
        }
        return Optional.empty();
    }

    /** Dispatch the selected task to Waiter; retry rejected choices without spending its task. */
    private void handleWaiterChoice(int turn) {
        while (true) {
            int task = ui.askWaiterTask();
            if (task == 0) {
                ui.showMessage("Waiter waits this turn.");
                return;
            }

            try {
                if (task == 1) {
                    Customer customer = restaurant.getWaitingQueue().peek()
                            .orElseThrow(() -> new IllegalStateException("Nobody is waiting to be seated."));
                    int tableId = ui.askTableId();
                    Table table = restaurant.findTable(tableId);
                    waiter.seat(restaurant, customer, table);
                    ui.showMessage("Waiter seated customer #" + customer.getCustomerId()
                            + " at table " + table.getTableId() + ".");
                } else {
                    int customerId = ui.askCustomerId();
                    Customer customer = restaurant.findSeatedCustomer(customerId);
                    if (task == 2) {
                        Order order = waiter.takeOrder(restaurant, customer);
                        ui.showMessage("Customer #" + customerId + " ordered "
                                + order.getItem().getName() + " (" + order.getItem().getPreparationUnits()
                                + " preparation unit(s)).");
                    } else {
                        waiter.serve(restaurant, customer, turn);
                        ui.showMessage("Waiter served customer #" + customerId + ".");
                    }
                }
                return;
            } catch (IllegalArgumentException | IllegalStateException ex) {
                ui.showError(ex.getMessage());
            }
        }
    }

    /** Select a seated customer's order; dispatch to Chef or wait. Retry invalid choices. */
    private void handleChefChoice() {
        while (true) {
            int task = ui.askChefTask();
            if (task == 0) {
                ui.showMessage("Chef waits this turn.");
                return;
            }

            try {
                int customerId = ui.askCustomerId();
                Customer customer = restaurant.findSeatedCustomer(customerId);
                Order order = customer.getOrder();
                if (order == null) {
                    throw new IllegalStateException("That customer has no order to prepare.");
                }
                chef.prepare(order);
                ui.showMessage("Chef advanced " + order.getItem().getName() + " for customer #"
                        + customerId + " (" + order.getPreparationUnitsDone() + "/"
                        + order.getItem().getPreparationUnits() + " units; " + order.getStatus() + ").");
                return;
            } catch (IllegalArgumentException | IllegalStateException ex) {
                ui.showError(ex.getMessage());
            }
        }
    }

    /**
     * Delegate eating to Restaurant.advanceEating(turn), then ask nextReadyToPay(turn).
     * If present, call Cashier.collect(). Newly ready customers must wait until next turn.
     */
    private void processEatingAndPayment(int turn) {
        restaurant.advanceEating(turn);
        Optional<Customer> readyCustomer = restaurant.nextReadyToPay(turn);
        if (readyCustomer.isPresent()) {
            Customer customer = readyCustomer.get();
            cashier.collect(restaurant, customer, turn);
            ui.showMessage("Cashier collected payment from customer #" + customer.getCustomerId() + ".");
        }
    }
}
