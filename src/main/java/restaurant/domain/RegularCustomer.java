package restaurant.domain;

//concrete class that implements the abstract class
public final class RegularCustomer extends Customer {
    public RegularCustomer(int customerId, int arrivalTurn) {
        super(customerId, arrivalTurn);
    }

    @Override
    public int waitingPenalty() {
        return 8;
    }
}
