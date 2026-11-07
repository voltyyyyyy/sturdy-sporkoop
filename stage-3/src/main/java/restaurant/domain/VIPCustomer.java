package restaurant.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class VIPCustomer extends Customer {
    public VIPCustomer(int customerId, int arrivalTurn) {
        super(customerId, arrivalTurn);
    }

    @Override
    public int waitingPenalty() {
        return 5;
    }

    @Override
    public long discountedPrice(long priceCents) {
        if (priceCents < 0) {
            throw new IllegalArgumentException("Price must not be negative");
        }
        return BigDecimal.valueOf(priceCents)
                .multiply(new BigDecimal("0.90"))
                .setScale(0, RoundingMode.HALF_UP)
                .longValueExact();
    }

    @Override
    public String typeLabel() {
        return "VIP";
    }
}
