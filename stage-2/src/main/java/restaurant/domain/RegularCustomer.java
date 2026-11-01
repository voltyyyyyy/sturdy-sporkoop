package restaurant.domain;

public final class RegularCustomer extends Customer {
    public RegularCustomer(int customerId, int arrivalTurn) {
        super(customerId, arrivalTurn);
    }

    @Override
    public int waitingPenalty() {
        return 8;
    }
}
