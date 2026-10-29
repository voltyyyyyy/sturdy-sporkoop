package restaurant.domain;

import java.util.List;

/**
 * Stage 1 implementation. Owner: Gangula Karthik.
 * See docs/stage-1-contracts.md for the module contract.
 */
public final class Menu {
    // Composition: Menu creates and owns its three dishes.
    // The assignment brief supplies the Burger values omitted from the pasted report.
    private final List<MenuItem> items = List.of(
            new MenuItem("Salad", 900L, 1),
            new MenuItem("Burger", 1500L, 1),
            new MenuItem("Pasta", 1800L, 2));

    public Menu() {
        // Only Stage 1 dishes; no combos or pricing policies.
    }

    public List<MenuItem> getItems() { return items; }

    /** Lookup a dish by name; reject an unknown name rather than inventing a dish. */
    public MenuItem findByName(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Dish name must not be null");
        }
        return items.stream()
                .filter(item -> item.getName().equals(name))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No dish named " + name));
    }
}
