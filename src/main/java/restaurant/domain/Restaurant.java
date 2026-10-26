package restaurant.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;




public final class Restaurant {
    private static final int REQUIRED_PAID_CUSTOMERS = 4;
    private static final long REQUIRED_REVENUE_CENTS = 4500L;
    private static final int REQUIRED_AVERAGE_SATISFACTION = 60;

    private final List<Table> tables;
    private final WaitingQueue waitingQueue;
    private final Menu menu;
    private long revenueCents;
    private int servedCount;
    private int abandonedCount;
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
    public long getRevenueCents() { return revenueCents; }
    public int getServedCount() { return servedCount; }
    public int getAbandonedCount() { return abandonedCount; }

    public List<Customer> getPresentCustomers() {
        throw new UnsupportedOperationException("TODO: Qiyun - queued and seated customer snapshot");
    }

    public Customer findSeatedCustomer(int customerId) {
        throw new UnsupportedOperationException("TODO: Qiyun - seated customer lookup");
    }

    public Table findTable(int tableId) {
        throw new UnsupportedOperationException("TODO: Qiyun - owned table lookup");
    }

    public void advanceEating(int turn) {
        throw new UnsupportedOperationException("TODO: Qiyun - delegate eating progression to customers");
    }

    public Optional<Customer> nextReadyToPay(int turn) {
        throw new UnsupportedOperationException("TODO: Qiyun - oldest eligible payment selection");
    }






    public void recordPayment(Customer customer, int turn) {
        throw new UnsupportedOperationException("TODO: Qiyun - payment transaction and scoring");
    }







    public List<Customer> applyWaitingPenalties() {
        throw new UnsupportedOperationException("TODO: Qiyun - penalties and abandonment orchestration");
    }

    public OptionalDouble getAverageSatisfaction() {
        throw new UnsupportedOperationException("TODO: Qiyun - paid-customer satisfaction average");
    }




    public GameResult createResult(int turnsUsed) {
        throw new UnsupportedOperationException("TODO: Qiyun - final victory calculation and result value");
    }
}
