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
        throw new UnsupportedOperationException("STAGE2 TODO: Gangula Karthik - RegularCustomer.typeLabel");
    }

    @Override
    public long discountedPrice(long priceCents) {
        throw new UnsupportedOperationException("STAGE2 TODO: Gangula Karthik - RegularCustomer.discountedPrice");
    }
}
