package restaurant.app;

import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Scanner;

import restaurant.control.TurnEngine;
import restaurant.domain.GameConfig;
import restaurant.domain.Menu;
import restaurant.domain.Restaurant;
import restaurant.ui.ConsoleUI;

public final class Main {

    private static final int DEFAULT_SEED = 42;

    private Main() {
    }

    public static void main(String[] args) {
        int seed;
        try {
            seed = parseSeed(args);
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            return;
        }

        GameConfig config = GameConfig.base();
        Menu menu = Menu.baseMenu();
        Restaurant restaurant = new Restaurant(config, menu, new Random(seed));
        ConsoleUI ui = new ConsoleUI(new Scanner(System.in), System.out);

        try {
            ui.showWelcome(seed, restaurant);
            new TurnEngine(restaurant, ui).run();
        } catch (NoSuchElementException e) {
            System.out.println();
            System.out.println("Input ended before the game finished.");
        } finally {
            ui.close();
        }
    }

    private static int parseSeed(String[] args) {
        if (args.length == 0) {
            return DEFAULT_SEED;
        }
        if (args.length == 2 && args[0].equals("--seed")) {
            try {
                return Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("--seed needs a whole number.");
            }
        }
        throw new IllegalArgumentException("Usage: java restaurant.app.Main [--seed N]");
    }
}
