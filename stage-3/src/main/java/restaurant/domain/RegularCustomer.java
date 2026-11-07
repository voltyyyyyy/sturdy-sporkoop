package restaurant.domain;

public final class RegularCustomer extends Customer {
    public RegularCustomer(int customerId, int arrivalTurn) {
        super(customerId, arrivalTurn);
    }

    @Override
    public int waitingPenalty() {
        return 8;
    }

    @Override
    public String typeLabel() {
        return "Regular";
    }

    @Override
    public long discountedPrice(long priceCents) {
        if (priceCents < 0) {
            throw new IllegalArgumentException("Price must not be negative");
        }
        return priceCents;
    }
}
