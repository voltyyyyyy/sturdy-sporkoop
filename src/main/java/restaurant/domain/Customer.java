package restaurant.domain;

import java.util.Objects;
import java.util.Random;




public class Customer {

    private static final int STARTING_SATISFACTION = 100;
    private static final int NO_READY_TURN = -1;

    private final int customerId;
    private final String name;
    private final int arrivalTurn;

    private int satisfaction;
    private MenuItem chosenItem;
    private Order order;
    private int readyToPayTurn;

    public Customer(int customerId, String name, int arrivalTurn) {
        if (customerId <= 0) {
            throw new IllegalArgumentException("Customer id must be positive.");
        }
        if (arrivalTurn <= 0) {
            throw new IllegalArgumentException("Arrival turn must be positive.");
        }
        Objects.requireNonNull(name, "name");
        if (name.isBlank()) {
            throw new IllegalArgumentException("Customer name cannot be blank.");
        }
        this.customerId = customerId;
        this.name = name;
        this.arrivalTurn = arrivalTurn;
        this.satisfaction = STARTING_SATISFACTION;
        this.readyToPayTurn = NO_READY_TURN;
    }




    public void decideOrder(Menu menu, Random random) {
        Objects.requireNonNull(menu, "menu");
        Objects.requireNonNull(random, "random");
        if (chosenItem != null) {
            throw new IllegalStateException(name + " has already chosen a dish.");
        }
        chosenItem = chooseMenuItem(menu, random);
    }

    protected MenuItem chooseMenuItem(Menu menu, Random random) {
        return menu.itemAt(random.nextInt(menu.size()));
    }

    public int satisfactionLossPerTurn() {
        return 8;
    }

    public int billCents(MenuItem item) {
        return item.getPriceCents();
    }

    public void placeOrder(Order order) {
        if (this.order != null) {
            throw new IllegalStateException(name + " already has an order.");
        }
        this.order = Objects.requireNonNull(order, "order");
    }

    public void loseSatisfaction(int points) {
        if (points < 0) {
            throw new IllegalArgumentException("Satisfaction loss cannot be negative.");
        }
        satisfaction = Math.max(0, satisfaction - points);
    }

    public void markReadyToPay(int turn) {
        if (turn <= 0) {
            throw new IllegalArgumentException("Turn must be positive.");
        }
        if (readyToPayTurn == NO_READY_TURN) {
            readyToPayTurn = turn;
        }
    }

    public boolean isServed() {
        return order != null && order.isServed();
    }

    public boolean hasOrder() {
        return order != null;
    }

    public boolean isReadyToPay() {
        return readyToPayTurn != NO_READY_TURN;
    }

    public int getCustomerId() {
        return customerId;
    }

    public String getName() {
        return name;
    }

    public int getArrivalTurn() {
        return arrivalTurn;
    }

    public int getSatisfaction() {
        return satisfaction;
    }

    public MenuItem getChosenItem() {
        return chosenItem;
    }

    public Order getOrder() {
        return order;
    }

    public int getReadyToPayTurn() {
        return readyToPayTurn;
    }
}
