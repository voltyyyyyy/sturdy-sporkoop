package restaurant.domain;




public final class MenuItem {
    private final String name;
    private final long priceCents;
    private final int preparationUnits;

    public MenuItem(String name, long priceCents, int preparationUnits) {
        this.name = name;
        this.priceCents = priceCents;
        this.preparationUnits = preparationUnits;
    }

    public String getName() { return name; }
    public long getPriceCents() { return priceCents; }
    public int getPreparationUnits() { return preparationUnits; }
}
