package restaurant.control;

import java.util.List;
import java.util.Objects;

import restaurant.domain.Customer;
import restaurant.domain.IllegalActionException;
import restaurant.domain.Money;
import restaurant.domain.Order;
import restaurant.domain.Restaurant;
import restaurant.domain.Table;
import restaurant.ui.ConsoleUI;
import restaurant.ui.Request;




public final class TurnEngine {

    private final Restaurant restaurant;
    private final ConsoleUI ui;
    private final Waiter waiter;
    private final Chef chef;
    private final Cashier cashier;
    private int currentTurn;

    public TurnEngine(Restaurant restaurant, ConsoleUI ui) {
        this.restaurant = Objects.requireNonNull(restaurant, "restaurant");
        this.ui = Objects.requireNonNull(ui, "ui");
        this.waiter = new Waiter(restaurant);
        this.chef = new Chef(restaurant);
        this.cashier = new Cashier(restaurant);
    }

    public void run() {
        int maxTurns = restaurant.getConfig().getMaxTurns();
        for (currentTurn = 1; currentTurn <= maxTurns; currentTurn++) {
            runTurn(currentTurn);
        }
        ui.showResult(restaurant);
    }

    private void runTurn(int turn) {
        waiter.resetForTurn();
        chef.resetForTurn();

        ui.showTurnStart(turn, restaurant);

        for (Customer customer : restaurant.arrive(turn)) {
            ui.showMessage(customer.getName() + " arrives with satisfaction "
                    + customer.getSatisfaction() + ".");
        }

        askWaiter(turn);
        askChef(turn);

        for (Customer customer : restaurant.advanceEating(turn)) {
            ui.showMessage(customer.getName() + " has finished eating and is ready to pay.");
        }

        cashier.collectNext(turn).ifPresent(customer ->
                ui.showMessage(customer.getName() + " pays "
                        + Money.format(customer.getOrder().getPriceCents()) + "."));

        for (Customer customer : restaurant.applySatisfactionLoss()) {
            ui.showMessage(customer.getName() + " leaves without paying.");
        }

        ui.showTurnEnd(turn, restaurant);
    }

    private void askWaiter(int turn) {
        while (true) {
            Request request = ui.askWaiterRequest(restaurant);
            try {
                applyWaiterRequest(request, turn);
                return;
            } catch (IllegalActionException e) {
                ui.showMessage(e.getMessage());
            }
        }
    }

    private void askChef(int turn) {
        while (true) {
            Request request = ui.askChefRequest(restaurant);
            try {
                applyChefRequest(request, turn);
                return;
            } catch (IllegalActionException e) {
                ui.showMessage(e.getMessage());
            }
        }
    }

    private void applyWaiterRequest(Request request, int turn) {
        switch (request.getAction()) {
            case SEAT -> {
                Customer customer = customerAt(request.getFirstTarget(), restaurant.waitingCustomers());
                Table table = tableById(request.getSecondTarget());
                waiter.seat(customer, table);
                ui.showMessage(customer.getName() + " is seated at table " + table.getTableId() + ".");
            }
            case TAKE_ORDER -> {
                Customer customer = customerAt(request.getFirstTarget(), restaurant.customersAwaitingOrder());
                Order order = waiter.takeOrder(customer, turn);
                ui.showMessage(customer.getName() + " orders " + order.getItem().getName()
                        + " for " + Money.format(order.getPriceCents()) + ".");
            }
            case SERVE -> {
                Order order = orderById(request.getFirstTarget(), restaurant.servableOrders());
                Customer customer = order.getCustomer();
                waiter.serve(order, turn);
                ui.showMessage(customer.getName() + " is served " + order.getItem().getName() + ".");
            }
            case WAIT -> {
            }
            default -> throw new IllegalActionException(
                    "The Waiter cannot " + request.getAction() + ".");
        }
    }

    private void applyChefRequest(Request request, int turn) {
        switch (request.getAction()) {
            case PREPARE -> {
                Order order = orderById(request.getFirstTarget(), restaurant.preparableOrders());
                chef.prepare(order, turn);
                if (order.isReady()) {
                    ui.showMessage("Order #" + order.getOrderId() + " ("
                            + order.getItem().getName() + ") is READY.");
                }
            }
            case WAIT -> {
            }
            default -> throw new IllegalActionException(
                    "The Chef cannot " + request.getAction() + ".");
        }
    }

    private Customer customerAt(int index, List<Customer> customers) {
        if (index < 0 || index >= customers.size()) {
            throw new IllegalActionException("That customer is no longer available. Choose again.");
        }
        return customers.get(index);
    }

    private Table tableById(int tableId) {
        for (Table table : restaurant.getTables()) {
            if (table.getTableId() == tableId) {
                return table;
            }
        }
        throw new IllegalActionException("There is no table " + tableId + ". Choose again.");
    }

    private Order orderById(int orderId, List<Order> orders) {
        for (Order order : orders) {
            if (order.getOrderId() == orderId) {
                return order;
            }
        }
        throw new IllegalActionException("Order #" + orderId + " is not available. Choose again.");
    }
}
