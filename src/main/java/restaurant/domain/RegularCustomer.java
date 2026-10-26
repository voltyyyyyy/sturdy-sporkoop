package restaurant.domain;

/**
 * Stage 1 prototype. Owner: Mingchen.
 * See docs/stage-1-contracts.md before implementing TODO methods.
 */
public final class RegularCustomer extends Customer {
    public RegularCustomer(int customerId, int arrivalTurn) {
        super(customerId, arrivalTurn);
    }

    /** Stage 1 regular customers lose exactly 8 satisfaction per waiting turn. */
    @Override
    public int waitingPenalty() {
        throw new UnsupportedOperationException("TODO: Mingchen - return the regular penalty of 8");
    }
}
