package restaurant.domain;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;

public final class Restaurant {
    private static final int REQUIRED_PAID_CUSTOMERS = 4;
    private static final long REQUIRED_REVENUE_CENTS = 4500L;
    private static final int REQUIRED_AVERAGE_SATISFACTION = 60;

    private static final Comparator<Customer> ARRIVAL_ORDER =
            Comparator.comparingInt(Customer::getArrivalTurn)
                    .thenComparingInt(Customer::getCustomerId);

    private final List<Table> tables;
    private final WaitingQueue waitingQueue;
    private final Menu menu;
    private final HappyHour happyHour = new HappyHour();
    private long revenueCents;
    private int servedCount;
    private int abandonedCount;
    private long lostBillCents;
    private final List<Customer> paidCustomers = new ArrayList<>();
    private final List<Integer> paidSatisfaction = new ArrayList<>();

    public Restaurant() {
        this.tables = List.of(new Table(1), new Table(2));
        this.waitingQueue = new WaitingQueue();
        this.menu = new Menu();
    }

    public List<Table> getTables() { return tables; }
    public WaitingQueue getWaitingQueue() { return waitingQueue; }
    public Menu getMenu() { return menu; }
    public HappyHour getHappyHour() { return happyHour; }
    public long getRevenueCents() { return revenueCents; }
    public int getServedCount() { return servedCount; }
    public int getAbandonedCount() { return abandonedCount; }
    public long getLostBillCents() { return lostBillCents; }

    public List<Customer> getPaidCustomers() { return List.copyOf(paidCustomers); }

    public long getOutstandingBillCents() {
        long outstanding = 0;
        for (Table table : tables) {
            Customer occupant = table.getOccupant();
            if (occupant != null && occupant.hasUnpaidBill()) {
                outstanding += occupant.getBillCents();
            }
        }
        return outstanding;
    }

    public List<Customer> getPresentCustomers() {
        List<Customer> present = new ArrayList<>(waitingQueue.getCustomers());
        for (Table table : tables) {
            if (!table.isFree()) {
                present.add(table.getOccupant());
            }
        }
        return List.copyOf(present);
    }

    public Customer findSeatedCustomer(int customerId) {
        for (Table table : tables) {
            Customer occupant = table.getOccupant();
            if (occupant != null && occupant.getCustomerId() == customerId) {
                return occupant;
            }
        }
        throw new IllegalArgumentException("No seated customer with id " + customerId);
    }

    public Table findTable(int tableId) {
        for (Table table : tables) {
            if (table.getTableId() == tableId) {
                return table;
            }
        }
        throw new IllegalArgumentException("No table with table id " + tableId);
    }

    public boolean activateHappyHour() {
        return happyHour.activate();
    }

    public void recoverWaitingSatisfaction() {
        if (!happyHour.isActive()) {
            return;
        }
        for (Customer c : getPresentCustomers()) {
            if (isStillWaitingForService(c)) {
                happyHour.recover(c);
            }
        }
    }

    public void tickHappyHour() {
        happyHour.tick();
    }

    public void advanceEating(int turn) {
        for (Table table : tables) {
            Customer occupant = table.getOccupant();
            if (occupant != null && occupant.getState() == CustomerState.EATING) {
                occupant.advanceEating(turn);
            }
        }
    }

    public Optional<Customer> nextReadyToPay(int turn) {
        Customer oldest = null;
        for (Table table : tables) {
            Customer c = table.getOccupant();
            if (c != null && isEligibleToPay(c, turn)
                    && (oldest == null || ARRIVAL_ORDER.compare(c, oldest) < 0)) {
                oldest = c;
            }
        }
        return Optional.ofNullable(oldest);
    }

    public void recordPayment(Customer customer, int turn) {
        if (customer == null) {
            throw new IllegalArgumentException("Cannot record payment for a null customer");
        }
        if (paidCustomers.contains(customer)) {
            throw new IllegalStateException("Customer " + customer.getCustomerId() + " has already paid");
        }
        Table table = tableOf(customer);
        if (table == null) {
            throw new IllegalArgumentException("Customer " + customer.getCustomerId() + " is not seated here");
        }
        if (!isEligibleToPay(customer, turn)) {
            throw new IllegalStateException(
                    "Customer " + customer.getCustomerId() + " cannot pay on turn " + turn);
        }

        Order order = customer.getOrder();
        order.markPaid();
        customer.markPaid();

        revenueCents += order.getPriceCents();
        paidCustomers.add(customer);
        paidSatisfaction.add(customer.getSatisfaction());
        servedCount++;
        table.clear();
    }

    public List<Customer> applyWaitingPenalties() {
        List<Customer> departed = new ArrayList<>();
        for (Customer c : getPresentCustomers()) {
            if (!isStillWaitingForService(c)) {
                continue;
            }
            c.loseSatisfaction(c.waitingPenalty());
            if (c.getSatisfaction() > 0) {
                continue;
            }

            Order order = c.getOrder();
            if (order != null
                    && (order.getStatus() == OrderStatus.PLACED || order.getStatus() == OrderStatus.READY)) {
                lostBillCents += order.getPriceCents();
                order.cancel();
            }

            boolean wasInQueue = c.getState() == CustomerState.WAITING;
            c.leave();
            if (wasInQueue) {
                waitingQueue.remove(c);
            } else {
                tableOf(c).clear();
            }
            abandonedCount++;
            departed.add(c);
        }
        return List.copyOf(departed);
    }

    public OptionalDouble getAverageSatisfaction() {
        return paidSatisfaction.stream()
                .mapToInt(Integer::intValue)
                .average();
    }

    public GameResult createResult(int turnsUsed) {
        OptionalDouble average = getAverageSatisfaction();
        boolean victory = servedCount >= REQUIRED_PAID_CUSTOMERS
                && revenueCents >= REQUIRED_REVENUE_CENTS
                && average.isPresent()
                && average.getAsDouble() >= REQUIRED_AVERAGE_SATISFACTION;
        return new GameResult(victory, servedCount, abandonedCount,
                revenueCents, average, turnsUsed);
    }

    private static boolean isEligibleToPay(Customer c, int turn) {
        return c.getState() == CustomerState.READY_TO_PAY && c.getReadyToPayTurn() < turn;
    }

    private static boolean isStillWaitingForService(Customer c) {
        if (c.getState() == CustomerState.WAITING) {
            return true;
        }
        if (c.getState() != CustomerState.SEATED) {
            return false;
        }
        Order order = c.getOrder();
        return order == null
                || order.getStatus() == OrderStatus.PLACED
                || order.getStatus() == OrderStatus.READY;
    }

    private Table tableOf(Customer customer) {
        for (Table table : tables) {
            if (table.getOccupant() == customer) {
                return table;
            }
        }
        return null;
    }
}
