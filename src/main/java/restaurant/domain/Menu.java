package restaurant.domain;

import java.util.List;




public final class Menu {
    private final List<MenuItem> items = List.of(
            new MenuItem("Salad", 900L, 1),
            new MenuItem("Burger", 1500L, 1),
            new MenuItem("Pasta", 1800L, 2));

    public Menu() {
    }

    public List<MenuItem> getItems() { return items; }

    public MenuItem findByName(String name) {
        throw new UnsupportedOperationException("TODO: Karthik - menu lookup");
    }
}
