package restaurant.domain;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;

/**
 * Owns the state of one game and enforces the rules for seating, ordering,
 * preparation, serving, eating, payment and satisfaction.
 */
public final class Restaurant {

    private final GameConfig config;
    private final Menu menu;
    private final List<Table> tables;
    private final WaitingQueue waitingQueue;
    private final List<Customer> customers;
    private final GameStats stats;
    private final Random random;

    private int nextCustomerId;
    private int nextOrderId;

    public Restaurant(GameConfig config, Menu menu, Random random) {
        this.config = Objects.requireNonNull(config, "config");
        this.menu = Objects.requireNonNull(menu, "menu");
        this.random = Objects.requireNonNull(random, "random");

        this.tables = new ArrayList<>();
        for (int tableId = 1; tableId <= config.getTableCount(); tableId++) {
            tables.add(new Table(tableId));
        }
        this.waitingQueue = new WaitingQueue();
        this.customers = new ArrayList<>();
        this.stats = new GameStats();
        this.nextCustomerId = 1;
        this.nextOrderId = 1;
    }

    /** Creates the customers scheduled for this turn and adds them to the queue. */
    public List<Customer> arrive(int turn) {
        if (!config.isArrivalTurn(turn)) {
            return List.of();
        }
        int customerId = nextCustomerId++;
        Customer customer = new Customer(customerId, "Customer " + customerId, turn);
        customer.decideOrder(menu, random);
        customers.add(customer);
        waitingQueue.enqueue(customer);
        return List.of(customer);
    }

    /** Seats a queued customer at a free table. */
    public void seat(Customer customer, Table table) {
        requirePresent(customer);
        if (!waitingQueue.contains(customer)) {
            throw new IllegalActionException(customer.getName() + " is not waiting for a table.");
        }
        if (!tables.contains(table)) {
            throw new IllegalActionException("That table does not belong to this restaurant.");
        }
        if (!table.isFree()) {
            throw new IllegalActionException("Table " + table.getTableId() + " is already occupied.");
        }
        table.seat(customer);
        waitingQueue.remove(customer);
    }

    /** Takes the customer's order and locks the price on it. */
    public Order takeOrder(Customer customer, int turn) {
        requirePresent(customer);
        if (!isSeated(customer)) {
            throw new IllegalActionException(customer.getName() + " is not seated.");
        }
        if (customer.hasOrder()) {
            throw new IllegalActionException(customer.getName() + " has already ordered.");
        }
        MenuItem item = customer.getChosenItem();
        if (item == null) {
            throw new IllegalStateException(customer.getName() + " has not chosen a dish.");
        }
        Order order = new Order(
                nextOrderId++,
                customer,
                item,
                customer.billCents(item),
                turn);
        customer.placeOrder(order);
        return order;
    }

    /** Advances one order by one preparation unit. */
    public void advanceKitchen(Order order, int turn) {
        requireActiveOrder(order);
        order.advancePrepUnit(turn);
    }

    /** Serves an order that was already READY when the Waiter acted. */
    public void serve(Order order, int turn) {
        requireActiveOrder(order);
        if (!order.isReady()) {
            throw new IllegalActionException(
                    "Order #" + order.getOrderId() + " is not READY yet.");
        }
        if (order.getTurnReady() >= turn) {
            throw new IllegalActionException(
                    "Order #" + order.getOrderId() + " became READY this turn and can be served next turn.");
        }
        order.markServed(turn);
        stats.recordServed();
    }

    /** Completes one full turn of eating for customers served before this turn. */
    public List<Customer> advanceEating(int turn) {
        List<Customer> readyToPay = new ArrayList<>();
        for (Customer customer : customers) {
            Order order = customer.getOrder();
            if (order != null
                    && order.getStatus() == OrderStatus.SERVED
                    && order.getTurnServed() < turn
                    && !customer.isReadyToPay()) {
                customer.markReadyToPay(turn);
                readyToPay.add(customer);
            }
        }
        return readyToPay;
    }

    /** Pays the oldest customer who was already ready to pay before this turn. */
    public Optional<Customer> payOldestReady(int turn) {
        Optional<Customer> payer = customers.stream()
                .filter(Customer::isReadyToPay)
                .filter(customer -> customer.getReadyToPayTurn() < turn)
                .min(Comparator.comparingInt(Customer::getReadyToPayTurn)
                        .thenComparingInt(Customer::getArrivalTurn)
                        .thenComparingInt(Customer::getCustomerId));

        payer.ifPresent(this::completePayment);
        return payer;
    }

    /** Applies one turn of satisfaction loss to every present, unserved customer. */
    public List<Customer> applySatisfactionLoss() {
        List<Customer> leavers = new ArrayList<>();
        for (Customer customer : List.copyOf(customers)) {
            if (customer.isServed()) {
                continue;
            }
            customer.loseSatisfaction(customer.satisfactionLossPerTurn());
            if (customer.getSatisfaction() == 0) {
                abandon(customer);
                leavers.add(customer);
            }
        }
        return leavers;
    }

    /** True when all three victory targets are met. */
    public boolean isVictory() {
        return stats.getPaidCount() >= config.getTargetPaidCustomers()
                && stats.getRevenueCents() >= config.getTargetRevenueCents()
                && stats.hasPaidCustomers()
                && stats.averageSatisfaction() >= config.getTargetAverageSatisfaction();
    }

    public List<Customer> waitingCustomers() {
        return waitingQueue.snapshot();
    }

    public List<Customer> seatedCustomers() {
        List<Customer> seated = new ArrayList<>();
        for (Table table : tables) {
            if (!table.isFree()) {
                seated.add(table.getOccupant());
            }
        }
        return List.copyOf(seated);
    }

    public List<Customer> customersAwaitingOrder() {
        return seatedCustomers().stream()
                .filter(customer -> !customer.hasOrder())
                .toList();
    }

    public List<Order> servableOrders() {
        return customers.stream()
                .map(Customer::getOrder)
                .filter(Objects::nonNull)
                .filter(Order::isReady)
                .toList();
    }

    public List<Order> preparableOrders() {
        return customers.stream()
                .map(Customer::getOrder)
                .filter(Objects::nonNull)
                .filter(order -> order.getStatus() == OrderStatus.PLACED)
                .toList();
    }

    public List<Table> freeTables() {
        return tables.stream()
                .filter(Table::isFree)
                .toList();
    }

    public GameConfig getConfig() {
        return config;
    }

    public Menu getMenu() {
        return menu;
    }

    public List<Table> getTables() {
        return List.copyOf(tables);
    }

    public GameStats getStats() {
        return stats;
    }

    private void completePayment(Customer customer) {
        Order order = customer.getOrder();
        if (order == null) {
            throw new IllegalStateException("A paying customer must have an order.");
        }
        Table table = tableOf(customer)
                .orElseThrow(() -> new IllegalStateException(
                        customer.getName() + " is ready to pay but is not seated."));

        order.markPaid();
        stats.recordPayment(customer);
        customers.remove(customer);
        table.vacate();
    }

    private void abandon(Customer customer) {
        Order order = customer.getOrder();
        if (order != null && order.isActive()) {
            order.cancel();
        }
        waitingQueue.remove(customer);
        tableOf(customer).ifPresent(Table::vacate);
        customers.remove(customer);
        stats.recordDeparture();
    }

    private Optional<Table> tableOf(Customer customer) {
        return tables.stream()
                .filter(table -> customer == table.getOccupant())
                .findFirst();
    }

    private boolean isSeated(Customer customer) {
        return tableOf(customer).isPresent();
    }

    private void requirePresent(Customer customer) {
        Objects.requireNonNull(customer, "customer");
        if (!customers.contains(customer)) {
            throw new IllegalActionException("That customer is no longer in the restaurant.");
        }
    }

    private void requireActiveOrder(Order order) {
        Objects.requireNonNull(order, "order");
        boolean belongsToPresentCustomer = customers.stream()
                .anyMatch(customer -> customer.getOrder() == order);
        if (!belongsToPresentCustomer) {
            throw new IllegalActionException(
                    "Order #" + order.getOrderId() + " is no longer active.");
        }
    }
}
