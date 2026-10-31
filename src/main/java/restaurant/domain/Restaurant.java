package restaurant.domain;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;

/**
 * Stage 1 restaurant aggregate. Owner: Zhang Qiyun.
 */
public final class Restaurant {
    private static final int REQUIRED_PAID_CUSTOMERS = 4;
    private static final long REQUIRED_REVENUE_CENTS = 4500L;
    private static final int REQUIRED_AVERAGE_SATISFACTION = 60;

    // Oldest first: earlier arrival turn, then lower customer id as a tie-breaker.
    private static final Comparator<Customer> ARRIVAL_ORDER =
            Comparator.comparingInt(Customer::getArrivalTurn)
                    .thenComparingInt(Customer::getCustomerId);

    private final List<Table> tables;
    private final WaitingQueue waitingQueue;
    private final Menu menu;
    private long revenueCents;
    // Completed, paid customers: the report's served-count scoring metric.
    private int servedCount;
    private int abandonedCount;
    // Bills cancelled because the customer left before being served.
    private long lostBillCents;
    private final List<Customer> paidCustomers = new ArrayList<>();
    private final List<Integer> paidSatisfaction = new ArrayList<>();

    public Restaurant() {
        // C1: create exactly two tables here; never accept externally supplied tables.
        this.tables = List.of(new Table(1), new Table(2));
        this.waitingQueue = new WaitingQueue();
        this.menu = new Menu();
    }

    public List<Table> getTables() { return tables; }
    public WaitingQueue getWaitingQueue() { return waitingQueue; }
    public Menu getMenu() { return menu; }
    public long getRevenueCents() { return revenueCents; }
    public int getServedCount() { return servedCount; }
    public int getAbandonedCount() { return abandonedCount; }
    public long getLostBillCents() { return lostBillCents; }

    /** Bill history: paid customers in payment order; each keeps its paid bill. */
    public List<Customer> getPaidCustomers() { return List.copyOf(paidCustomers); }

    /** Total still owed by seated customers who have ordered but not paid yet. */
    public long getOutstandingBillCents() {
        long outstanding = 0;
        // Queued customers have no order, and paying customers leave their table.
        for (Table table : tables) {
            Customer occupant = table.getOccupant();
            if (occupant != null && occupant.hasUnpaidBill()) {
                outstanding += occupant.getBillCents();
            }
        }
        return outstanding;
    }

    /** Snapshot used for iteration; never expose mutable restaurant collections. */
    public List<Customer> getPresentCustomers() {
        // Queued customers first (in arrival order), then table occupants.
        List<Customer> present = new ArrayList<>(waitingQueue.getCustomers());
        for (Table table : tables) {
            if (!table.isFree()) {
                present.add(table.getOccupant());
            }
        }
        return List.copyOf(present);
    }

    /** Find a seated customer by id for a manager choice; reject an unknown id. */
    public Customer findSeatedCustomer(int customerId) {
        for (Table table : tables) {
            Customer occupant = table.getOccupant();
            if (occupant != null && occupant.getCustomerId() == customerId) {
                return occupant;
            }
        }
        throw new IllegalArgumentException("No seated customer with id " + customerId);
    }

    /** Return an owned table; reject a table id outside the two-table restaurant. */
    public Table findTable(int tableId) {
        for (Table table : tables) {
            if (table.getTableId() == tableId) {
                return table;
            }
        }
        throw new IllegalArgumentException("No table with table id " + tableId);
    }

    /** Domain orchestration: call Customer.advanceEating(turn) for table occupants. */
    public void advanceEating(int turn) {
        for (Table table : tables) {
            Customer occupant = table.getOccupant();
            if (occupant != null && occupant.getState() == CustomerState.EATING) {
                occupant.advanceEating(turn);
            }
        }
    }

    /** Oldest eligible customer; READY_TO_PAY must have been reached before this turn. */
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

    /**
     * Validate membership and payment eligibility; reject repeat payment.
     * Mark Order PAID before Customer PAID, record the locked bill and satisfaction,
     * increment completed servedCount exactly once, and clear the owned table.
     * Cashier delegates here; no staff object owns score arithmetic.
     */
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

        // All checks passed: change state now, Order first because Customer.markPaid() requires it.
        Order order = customer.getOrder();
        order.markPaid();
        customer.markPaid();

        // Record the locked bill and the satisfaction snapshot at payment time.
        revenueCents += order.getPriceCents();
        paidCustomers.add(customer);
        paidSatisfaction.add(customer.getSatisfaction());
        servedCount++;
        table.clear();
    }

    /**
     * For each waiting/unserved seated customer, call
     * customer.loseSatisfaction(customer.waitingPenalty()).
     * At zero: cancel any unserved order, call leave(), remove from queue or clear
     * table, and increment abandonedCount once. A cancelled order's price is added
     * to lostBillCents. Return the departing customers.
     * Never test for RegularCustomer (P1).
     */
    public List<Customer> applyWaitingPenalties() {
        List<Customer> departed = new ArrayList<>();
        // Iterate a snapshot so customers can be removed inside the loop.
        for (Customer c : getPresentCustomers()) {
            if (!isStillWaitingForService(c)) {
                continue;
            }
            // P1: polymorphic call through Customer, no subclass test.
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

            // leave() must come before Table.clear(), which requires a LEFT occupant.
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

    /** Average the satisfaction snapshots recorded at payment; empty if none paid. */
    public OptionalDouble getAverageSatisfaction() {
        return paidSatisfaction.stream()
                .mapToInt(Integer::intValue)
                .average();
    }

    /**
     * Called after turn 18. Victory requires all three thresholds above.
     * No paid customers means no average and therefore no victory.
     */
    public GameResult createResult(int turnsUsed) {
        OptionalDouble average = getAverageSatisfaction();
        boolean victory = servedCount >= REQUIRED_PAID_CUSTOMERS
                && revenueCents >= REQUIRED_REVENUE_CENTS
                && average.isPresent()
                && average.getAsDouble() >= REQUIRED_AVERAGE_SATISFACTION;
        return new GameResult(victory, servedCount, abandonedCount,
                revenueCents, average, turnsUsed);
    }

    /** READY_TO_PAY must have been reached on an earlier turn, not this one. */
    private static boolean isEligibleToPay(Customer c, int turn) {
        return c.getState() == CustomerState.READY_TO_PAY && c.getReadyToPayTurn() < turn;
    }

    /** Queued, or seated whose food has not been served yet. */
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

    /** The table this customer occupies, or null if not seated here. */
    private Table tableOf(Customer customer) {
        for (Table table : tables) {
            if (table.getOccupant() == customer) {
                return table;
            }
        }
        return null;
    }
}
