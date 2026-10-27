package restaurant.domain;

import java.util.List;
import java.util.Optional;

public final class Restaurant {
    private final List<Table> tables = List.of(new Table(), new Table());
    private final WaitingQueue queue = new WaitingQueue();
    private final Menu menu = new Menu();
    Customer eligible;
    Customer collected;
    int paymentCalls;
    int paymentTurn;
    boolean rejectPayment;

    public List<Table> getTables() { return tables; }
    public WaitingQueue getWaitingQueue() { return queue; }
    public Menu getMenu() { return menu; }
    public Optional<Customer> nextReadyToPay(int turn) { return Optional.ofNullable(eligible); }
    public void recordPayment(Customer customer, int turn) {
        if (rejectPayment) { throw new IllegalStateException("Payment rejected"); }
        paymentCalls++;
        paymentTurn = turn;
        collected = customer;
    }
}
