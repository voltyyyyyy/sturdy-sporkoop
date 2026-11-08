package restaurant.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class ComboMeal extends MenuItem {
    private final MenuItem first;
    private final MenuItem second;

    public ComboMeal(MenuItem first, MenuItem second) {
        super(comboName(first, second), comboPriceCents(first, second),
                first.getPreparationUnits() + second.getPreparationUnits());
        this.first = first;
        this.second = second;
    }

    private static String comboName(MenuItem first, MenuItem second) {
        if (first == null || second == null) {
            throw new IllegalArgumentException("Combo components must not be null");
        }
        if (first.getName().equals(second.getName())) {
            throw new IllegalArgumentException("A combo needs two different dishes");
        }
        return first.getName() + " + " + second.getName();
    }

    private static long comboPriceCents(MenuItem first, MenuItem second) {
        long combined = first.getPriceCents() + second.getPriceCents();
        return BigDecimal.valueOf(combined)
                .multiply(new BigDecimal("0.90"))
                .setScale(0, RoundingMode.HALF_UP)
                .longValueExact();
    }
}
