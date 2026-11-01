package restaurant.domain;

public final class VIPCustomer extends Customer {
    public VIPCustomer(int customerId, int arrivalTurn) {
        super(customerId, arrivalTurn);
    }

    @Override
    public int waitingPenalty() {
        throw new UnsupportedOperationException("STAGE2 TODO: Yang Mingchen - VIPCustomer.waitingPenalty");
    }

    @Override
    public long discountedPrice(long priceCents) {
        throw new UnsupportedOperationException("STAGE2 TODO: Yang Mingchen - VIPCustomer.discountedPrice");
    }

    @Override
    public String typeLabel() {
        throw new UnsupportedOperationException("STAGE2 TODO: Yang Mingchen - VIPCustomer.typeLabel");
    }
}
