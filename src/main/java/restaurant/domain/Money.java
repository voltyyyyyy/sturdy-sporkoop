package restaurant.domain;

import java.util.Locale;

/** Formats integer cent amounts for display. */
public final class Money {

    private Money() {
    }

    /**
     * Formats a non-negative amount of cents as dollars with two decimals.
     *
     * @param cents amount in cents
     * @return the amount, for example {@code $18.00}
     */
    public static String format(int cents) {
        if (cents < 0) {
            throw new IllegalArgumentException("Amount cannot be negative: " + cents);
        }
        return String.format(Locale.ROOT, "$%d.%02d", cents / 100, cents % 100);
    }
}
