package restaurant.domain;

/**
 * Stage 1 implementation. Owner: Gangula Karthik.
 * See docs/stage-1-contracts.md for the module contract.
 */
public final class MenuItem {
    private final String name;
    private final long priceCents;
    private final int preparationUnits;

    public MenuItem(String name, long priceCents, int preparationUnits) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Dish name must not be null or blank");
        }
        if (priceCents < 0) {
            throw new IllegalArgumentException("Price must not be negative");
        }
        if (preparationUnits <= 0) {
            throw new IllegalArgumentException("Preparation units must be positive");
        }
        this.name = name;
        this.priceCents = priceCents;
        this.preparationUnits = preparationUnits;
    }

    public String getName() { return name; }
    public long getPriceCents() { return priceCents; }
    public int getPreparationUnits() { return preparationUnits; }
}
