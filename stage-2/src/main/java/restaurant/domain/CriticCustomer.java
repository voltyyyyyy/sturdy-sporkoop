package restaurant.domain;

public final class CriticCustomer extends Customer {
    public CriticCustomer(int customerId, int arrivalTurn) {
        super(customerId, arrivalTurn);
    }

    @Override
    public int waitingPenalty() {
        throw new UnsupportedOperationException("STAGE2 TODO: Gangula Karthik - CriticCustomer.waitingPenalty");
    }

    @Override
    public MenuItem chooseFood(Menu menu) {
        throw new UnsupportedOperationException("STAGE2 TODO: Gangula Karthik - CriticCustomer.chooseFood");
    }

    @Override
    public String typeLabel() {
        throw new UnsupportedOperationException("STAGE2 TODO: Gangula Karthik - CriticCustomer.typeLabel");
    }

    @Override
    public long discountedPrice(long priceCents) {
        throw new UnsupportedOperationException("STAGE2 TODO: Gangula Karthik - CriticCustomer.discountedPrice");
    }
}
