package restaurant.control;

import restaurant.domain.Cashier;
import restaurant.domain.Chef;
import restaurant.domain.Customer;
import restaurant.domain.GameResult;
import restaurant.domain.Order;
import restaurant.domain.Restaurant;
import restaurant.domain.Table;
import restaurant.domain.Waiter;
import restaurant.ui.ConsoleUI;

import java.util.Optional;

public final class GameMgr {
    private static final int TOTAL_TURNS = 18;

    private final ConsoleUI ui;
    private final Restaurant restaurant;
    private final Waiter waiter;
    private final Chef chef;
    private final Cashier cashier;
    private final CustomerFactory customerFactory;
    private int currentTurn;
    private int nextCustomerId;

    public GameMgr(ConsoleUI ui) {
        this.ui = ui;
        this.restaurant = new Restaurant();
        this.waiter = new Waiter("Waiter");
        this.chef = new Chef("Chef");
        this.cashier = new Cashier("Cashier");
        this.customerFactory = new CustomerFactory();
        this.currentTurn = 0;
        this.nextCustomerId = 1;
    }

    public static void main(String[] args) {
        new GameMgr(new ConsoleUI()).run();
    }

    public void run() {
        for (currentTurn = 1; currentTurn <= TOTAL_TURNS; currentTurn++) {
            runTurn(currentTurn);
        }
        GameResult result = restaurant.createResult(TOTAL_TURNS);
        ui.showResult(result);
        ui.showBillHistory(restaurant);
    }

    private void runTurn(int turn) {
        waiter.resetForTurn();
        chef.resetForTurn();
        cashier.resetForTurn();

        Optional<Customer> arrival = processArrivals(turn);
        ui.showTurnStart(turn, restaurant, waiter, chef, cashier);
        arrival.ifPresent(customer -> ui.showMessage("Customer #" + customer.getCustomerId() + " arrived."));

        handleWaiterChoice(turn);
        handleChefChoice();

        processEatingAndPayment(turn);
        for (Customer customer : restaurant.applyWaitingPenalties()) {
            ui.showMessage("Customer #" + customer.getCustomerId()
                    + " left after losing all satisfaction.");
        }
        ui.showTurnEnd(turn, restaurant);
    }

    private Optional<Customer> processArrivals(int turn) {
        Optional<Customer> arrival = Optional.ofNullable(
                customerFactory.create(nextCustomerId, turn));

        arrival.ifPresent(customer -> {
            restaurant.getWaitingQueue().add(customer);
            nextCustomerId++;
        });

        return arrival;
    }

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

    private void processEatingAndPayment(int turn) {
        restaurant.advanceEating(turn);
        Optional<Customer> readyCustomer = restaurant.nextReadyToPay(turn);
        if (readyCustomer.isPresent()) {
            Customer customer = readyCustomer.get();
            cashier.collect(restaurant, customer, turn);
            ui.showPayment(customer);
        }
    }
}
