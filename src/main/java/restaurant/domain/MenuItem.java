package restaurant.domain;

/**
 * Stage 1 prototype. Owner: Karthik.
 * See docs/stage-1-contracts.md before implementing TODO methods.
 */
public final class MenuItem {
    private final String name;
    private final long priceCents;
    private final int preparationUnits;

    public MenuItem(String name, long priceCents, int preparationUnits) {
        // TODO Karthik: validate name, non-negative cents and positive preparation units.
        this.name = name;
        this.priceCents = priceCents;
        this.preparationUnits = preparationUnits;
    }

    public String getName() { return name; }
    public long getPriceCents() { return priceCents; }
    public int getPreparationUnits() { return preparationUnits; }
}
