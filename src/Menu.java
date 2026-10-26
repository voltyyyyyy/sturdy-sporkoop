import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Menu {
    private final List<MenuItem> items = new ArrayList<>();

    public Menu() {
        items.add(new MenuItem("Salad", 900, 1));
        items.add(new MenuItem("Burger", 1500, 1));
        items.add(new MenuItem("Pasta", 1800, 2));
    }

    public MenuItem find(String name) {
        if (name == null) {
            return null;
        }
        for (MenuItem item : items) {
            if (item.name().equalsIgnoreCase(name)) {
                return item;
            }
        }
        return null;
    }

    public List<MenuItem> items() {
        return Collections.unmodifiableList(items);
    }
}
