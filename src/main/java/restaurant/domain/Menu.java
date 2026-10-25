package restaurant.domain;

import java.util.List;
import java.util.Objects;

/** The menu for one game. */
public final class Menu {

    private final List<MenuItem> items;

    public Menu(List<MenuItem> items) {
        Objects.requireNonNull(items, "items");
        if (items.isEmpty()) {
            throw new IllegalArgumentException("A menu needs at least one item.");
        }
        this.items = List.copyOf(items);
    }

    /** The three items required by the Stage 1 base setting. */
    public static Menu baseMenu() {
        return new Menu(List.of(
                new MenuItem("Salad", 900, 1),
                new MenuItem("Burger", 1500, 1),
                new MenuItem("Pasta", 1800, 2)));
    }

    public int size() {
        return items.size();
    }

    public MenuItem itemAt(int index) {
        return items.get(index);
    }

    public List<MenuItem> items() {
        return items;
    }

    public MenuItem findByName(String name) {
        Objects.requireNonNull(name, "name");
        for (MenuItem item : items) {
            if (item.getName().equalsIgnoreCase(name)) {
                return item;
            }
        }
        throw new IllegalArgumentException("No menu item named " + name + ".");
    }
}
