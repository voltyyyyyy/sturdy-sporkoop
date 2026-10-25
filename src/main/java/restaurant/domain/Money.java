package restaurant.domain;

import java.util.Locale;

public final class Money {

    private Money() {
    }






    public static String format(int cents) {
        if (cents < 0) {
            throw new IllegalArgumentException("Amount cannot be negative: " + cents);
        }
        return String.format(Locale.ROOT, "$%d.%02d", cents / 100, cents % 100);
    }
}
