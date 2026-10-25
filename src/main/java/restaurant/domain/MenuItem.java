package restaurant.domain;

import java.util.Objects;

/** One item on the menu. Prices are stored in cents. */
public final class MenuItem {

    private final String name;
    private final int priceCents;
    private final int prepUnits;

    public MenuItem(String name, int priceCents, int prepUnits) {
        Objects.requireNonNull(name, "name");
        if (name.isBlank()) {
            throw new IllegalArgumentException("Menu item name cannot be blank.");
        }
        if (priceCents < 0) {
            throw new IllegalArgumentException("Price cannot be negative.");
        }
        if (prepUnits <= 0) {
            throw new IllegalArgumentException("Preparation units must be positive.");
        }
        this.name = name;
        this.priceCents = priceCents;
        this.prepUnits = prepUnits;
    }

    public String getName() {
        return name;
    }

    public int getPriceCents() {
        return priceCents;
    }

    public int getPrepUnits() {
        return prepUnits;
    }
}
