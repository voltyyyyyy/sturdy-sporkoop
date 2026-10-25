package restaurant.domain;

import java.util.Objects;

public final class GameStats {

    private int servedCount;
    private int paidCount;
    private int revenueCents;
    private int totalPaidSatisfaction;
    private int unhappyDepartures;

    public void recordServed() {
        servedCount++;
    }

    public void recordPayment(Customer customer) {
        Objects.requireNonNull(customer, "customer");
        Order order = customer.getOrder();
        if (order == null) {
            throw new IllegalStateException("A paying customer must have an order.");
        }
        paidCount++;
        revenueCents += order.getPriceCents();
        totalPaidSatisfaction += customer.getSatisfaction();
    }

    public void recordDeparture() {
        unhappyDepartures++;
    }

    public double averageSatisfaction() {
        return paidCount == 0 ? Double.NaN : (double) totalPaidSatisfaction / paidCount;
    }

    public boolean hasPaidCustomers() {
        return paidCount > 0;
    }

    public int getServedCount() {
        return servedCount;
    }

    public int getPaidCount() {
        return paidCount;
    }

    public int getRevenueCents() {
        return revenueCents;
    }

    public int getUnhappyDepartures() {
        return unhappyDepartures;
    }
}
