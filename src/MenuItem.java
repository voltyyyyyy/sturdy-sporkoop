public class MenuItem {
    private final String name;
    private final long priceCents;
    private final int prepUnits;

    public MenuItem(String name, long priceCents, int prepUnits) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Dish name must not be empty");
        }
        if (priceCents <= 0) {
            throw new IllegalArgumentException("Price must be positive: " + priceCents);
        }
        if (prepUnits < 1) {
            throw new IllegalArgumentException("Preparation units must be at least 1: " + prepUnits);
        }
        this.name = name;
        this.priceCents = priceCents;
        this.prepUnits = prepUnits;
    }

    public String name() {
        return name;
    }

    public long priceCents() {
        return priceCents;
    }

    public int prepUnits() {
        return prepUnits;
    }
}
