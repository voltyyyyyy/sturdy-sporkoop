package restaurant.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;

/**
 * Stage 1 prototype. Owner: Qiyun.
 * See docs/stage-1-contracts.md before implementing TODO methods.
 */
public final class Restaurant {
    private static final int REQUIRED_PAID_CUSTOMERS = 4;
    private static final long REQUIRED_REVENUE_CENTS = 4500L;
    private static final int REQUIRED_AVERAGE_SATISFACTION = 60;

    private final List<Table> tables;
    private final WaitingQueue waitingQueue;
    private final Menu menu;
    private long revenueCents;
    // Completed, paid customers: the report's served-count scoring metric.
    private int servedCount;
    private int abandonedCount;
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

    /** Snapshot used for iteration; never expose mutable restaurant collections. */
    public List<Customer> getPresentCustomers() {
        throw new UnsupportedOperationException("TODO: Qiyun - queued and seated customer snapshot");
    }

    /** Find a seated customer by id for a manager choice; reject an unknown id. */
    public Customer findSeatedCustomer(int customerId) {
        throw new UnsupportedOperationException("TODO: Qiyun - seated customer lookup");
    }

    /** Return an owned table; reject a table id outside the two-table restaurant. */
    public Table findTable(int tableId) {
        throw new UnsupportedOperationException("TODO: Qiyun - owned table lookup");
    }

    /** Domain orchestration: call Customer.advanceEating(turn) for table occupants. */
    public void advanceEating(int turn) {
        throw new UnsupportedOperationException("TODO: Qiyun - delegate eating progression to customers");
    }

    /** Oldest eligible customer; READY_TO_PAY must have been reached before this turn. */
    public Optional<Customer> nextReadyToPay(int turn) {
        throw new UnsupportedOperationException("TODO: Qiyun - oldest eligible payment selection");
    }

    /**
     * Validate membership and payment eligibility; reject repeat payment.
     * Mark Order PAID before Customer PAID, record the locked bill and satisfaction,
     * increment completed servedCount exactly once, and clear the owned table.
     * Cashier delegates here; no staff object owns score arithmetic.
     */
    public void recordPayment(Customer customer, int turn) {
        throw new UnsupportedOperationException("TODO: Qiyun - payment transaction and scoring");
    }

    /**
     * For each waiting/unserved seated customer, call
     * customer.loseSatisfaction(customer.waitingPenalty()).
     * At zero: cancel any unserved order, call leave(), remove from queue or clear
     * table, and increment abandonedCount once. Return the departing customers.
     * Never test for RegularCustomer (P1).
     */
    public List<Customer> applyWaitingPenalties() {
        throw new UnsupportedOperationException("TODO: Qiyun - penalties and abandonment orchestration");
    }

    /** Average the satisfaction snapshots recorded at payment; empty if none paid. */
    public OptionalDouble getAverageSatisfaction() {
        throw new UnsupportedOperationException("TODO: Qiyun - paid-customer satisfaction average");
    }

    /**
     * Called after turn 18. Victory requires all three thresholds above.
     * No paid customers means no average and therefore no victory.
     */
    public GameResult createResult(int turnsUsed) {
        throw new UnsupportedOperationException("TODO: Qiyun - final victory calculation and result value");
    }
}
