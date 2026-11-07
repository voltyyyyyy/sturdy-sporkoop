package restaurant.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class HappyHour {
    private static final int DURATION_TURNS = 2;
    private static final int DISCOUNT_PERCENT = 20;
    private static final int RECOVERY_POINTS = 15;

    private int remainingTurns;
    private boolean used;

    public boolean activate() {
        if (used) {
            return false;
        }
        used = true;
        remainingTurns = DURATION_TURNS;
        return true;
    }

    public boolean isActive() {
        return remainingTurns > 0;
    }

    public long applyDiscount(long priceCents) {
        if (priceCents < 0) {
            throw new IllegalArgumentException("Price must not be negative");
        }
        return BigDecimal.valueOf(priceCents)
                .multiply(BigDecimal.valueOf(100 - DISCOUNT_PERCENT, 2))
                .setScale(0, RoundingMode.HALF_UP)
                .longValueExact();
    }

    public void recover(Customer customer) {
        if (customer == null) {
            throw new IllegalArgumentException("Customer must not be null");
        }
        customer.recoverSatisfaction(RECOVERY_POINTS);
    }

    public void tick() {
        if (remainingTurns > 0) {
            remainingTurns--;
        }
    }

    public int getRemainingTurns() {
        return remainingTurns;
    }
}
