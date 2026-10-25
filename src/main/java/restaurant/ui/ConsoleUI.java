package restaurant.ui;

import java.io.PrintStream;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Scanner;

import restaurant.domain.Customer;
import restaurant.domain.GameConfig;
import restaurant.domain.GameStats;
import restaurant.domain.MenuItem;
import restaurant.domain.Money;
import restaurant.domain.Order;
import restaurant.domain.Restaurant;
import restaurant.domain.Table;

/** Command-line boundary: reads manager choices and displays game state. */
public final class ConsoleUI {

    private static final String RULE = "=".repeat(72);

    private final Scanner in;
    private final PrintStream out;

    public ConsoleUI(Scanner in, PrintStream out) {
        this.in = Objects.requireNonNull(in, "in");
        this.out = Objects.requireNonNull(out, "out");
    }

    public void showWelcome(int seed, Restaurant restaurant) {
        GameConfig config = restaurant.getConfig();
        out.println(RULE);
        out.println("RESTAURANT RUSH - Stage 1");
        out.printf("Seed: %d%n", seed);
        out.println();
        out.printf("Base setting: %d turns, %d tables, 1 waiter, 1 chef, 1 cashier.%n",
                config.getMaxTurns(), config.getTableCount());
        out.println("Menu:");
        for (MenuItem item : restaurant.getMenu().items()) {
            out.printf("  %-7s %s  %d preparation unit%s%n",
                    item.getName(),
                    Money.format(item.getPriceCents()),
                    item.getPrepUnits(),
                    item.getPrepUnits() == 1 ? "" : "s");
        }
        out.printf(Locale.ROOT,
                "Goal: at least %d paid customers, %s revenue, average paid satisfaction %.1f.%n",
                config.getTargetPaidCustomers(),
                Money.format(config.getTargetRevenueCents()),
                config.getTargetAverageSatisfaction());
        out.println(RULE);
    }

    public void showTurnStart(int turn, Restaurant restaurant) {
        GameStats stats = restaurant.getStats();
        out.println();
        out.println(RULE);
        out.printf("TURN %d / %d%n", turn, restaurant.getConfig().getMaxTurns());
        out.printf("Revenue: %s    Average satisfaction of paid customers: %s%n",
                Money.format(stats.getRevenueCents()), formatAverage(stats));
        printWaitingQueue(restaurant);
        printOccupiedTables(restaurant);
        printStaffTasks(restaurant);
        out.println(RULE);
    }

    public void showTurnEnd(int turn, Restaurant restaurant) {
        GameStats stats = restaurant.getStats();
        out.println();
        out.printf("End of turn %d%n", turn);
        out.printf("  Served: %d    Unhappy departures: %d%n",
                stats.getServedCount(), stats.getUnhappyDepartures());
        out.printf("  Revenue: %s    Average satisfaction of paid customers: %s%n",
                Money.format(stats.getRevenueCents()), formatAverage(stats));
        out.printf("  Turns used: %d%n", turn);
    }

    public void showResult(Restaurant restaurant) {
        GameStats stats = restaurant.getStats();
        GameConfig config = restaurant.getConfig();
        out.println();
        out.println(RULE);
        out.println(restaurant.isVictory() ? "RESULT: VICTORY" : "RESULT: DEFEAT");
        out.printf("  Paid customers: %d (target %d)%n",
                stats.getPaidCount(), config.getTargetPaidCustomers());
        out.printf("  Revenue: %s (target %s)%n",
                Money.format(stats.getRevenueCents()),
                Money.format(config.getTargetRevenueCents()));
        out.printf(Locale.ROOT, "  Average paid satisfaction: %s (target %.1f)%n",
                formatAverage(stats), config.getTargetAverageSatisfaction());
        out.printf("  Served: %d    Unhappy departures: %d    Turns used: %d%n",
                stats.getServedCount(), stats.getUnhappyDepartures(), config.getMaxTurns());
        out.println(RULE);
    }

    public void showMessage(String message) {
        out.println(message);
    }

    public void close() {
        in.close();
    }

    public Request askWaiterRequest(Restaurant restaurant) {
        while (true) {
            out.println();
            out.println("Waiter's task:");
            out.println("  [1] Seat a customer");
            out.println("  [2] Take an order");
            out.println("  [3] Serve a READY order");
            out.println("  [4] Wait");
            int choice = readInt("Choose 1-4: ", 1, 4);

            if (choice == 1) {
                List<Customer> waiting = restaurant.waitingCustomers();
                List<Table> freeTables = restaurant.freeTables();
                if (waiting.isEmpty()) {
                    out.println("No customer is waiting for a table.");
                    continue;
                }
                if (freeTables.isEmpty()) {
                    out.println("No table is free.");
                    continue;
                }
                int customerIndex = chooseIndex("Seat which customer?",
                        waiting.stream()
                                .map(customer -> customer.getName()
                                        + "  satisfaction " + customer.getSatisfaction())
                                .toList());
                List<String> freeTableIds = freeTables.stream()
                        .map(table -> String.valueOf(table.getTableId()))
                        .toList();
                out.println("Free tables: " + String.join(", ", freeTableIds));
                int tableId = readInt("Table id: ", 1, restaurant.getTables().size());
                return Request.seat(customerIndex, tableId);
            }

            if (choice == 2) {
                List<Customer> awaiting = restaurant.customersAwaitingOrder();
                if (awaiting.isEmpty()) {
                    out.println("No seated customer is waiting to order.");
                    continue;
                }
                int customerIndex = chooseIndex("Take the order of which customer?",
                        awaiting.stream()
                                .map(customer -> customer.getName()
                                        + "  satisfaction " + customer.getSatisfaction())
                                .toList());
                return Request.takeOrder(customerIndex);
            }

            if (choice == 3) {
                List<Order> readyOrders = restaurant.servableOrders();
                if (readyOrders.isEmpty()) {
                    out.println("No order is READY yet.");
                    continue;
                }
                int orderIndex = chooseIndex("Serve which order?",
                        readyOrders.stream()
                                .map(this::describeOrderForChoice)
                                .toList());
                return Request.serve(readyOrders.get(orderIndex).getOrderId());
            }

            return Request.idle();
        }
    }

    public Request askChefRequest(Restaurant restaurant) {
        while (true) {
            out.println();
            out.println("Chef's task:");
            out.println("  [1] Prepare an order (one preparation unit)");
            out.println("  [2] Wait");
            int choice = readInt("Choose 1-2: ", 1, 2);

            if (choice == 2) {
                return Request.idle();
            }

            List<Order> kitchenOrders = restaurant.preparableOrders();
            if (kitchenOrders.isEmpty()) {
                out.println("No order is waiting in the kitchen.");
                continue;
            }
            int orderIndex = chooseIndex("Prepare which order?",
                    kitchenOrders.stream()
                            .map(this::describeOrderForChoice)
                            .toList());
            return Request.prepare(kitchenOrders.get(orderIndex).getOrderId());
        }
    }

    private void printWaitingQueue(Restaurant restaurant) {
        List<Customer> waiting = restaurant.waitingCustomers();
        out.println("Waiting queue:");
        if (waiting.isEmpty()) {
            out.println("  empty");
            return;
        }
        for (int i = 0; i < waiting.size(); i++) {
            Customer customer = waiting.get(i);
            out.printf("  %d. %s  satisfaction %d  arrived turn %d%n",
                    i + 1,
                    customer.getName(),
                    customer.getSatisfaction(),
                    customer.getArrivalTurn());
        }
    }

    private void printOccupiedTables(Restaurant restaurant) {
        out.println("Occupied tables:");
        boolean anyOccupied = false;
        for (Table table : restaurant.getTables()) {
            if (table.isFree()) {
                continue;
            }
            anyOccupied = true;
            Customer customer = table.getOccupant();
            out.printf("  Table %d: %s  satisfaction %d  %s%n",
                    table.getTableId(),
                    customer.getName(),
                    customer.getSatisfaction(),
                    describeOrder(customer));
        }
        if (!anyOccupied) {
            out.println("  none");
        }
    }

    private void printStaffTasks(Restaurant restaurant) {
        int waiting = restaurant.waitingCustomers().size();
        int freeTables = restaurant.freeTables().size();
        int awaitingOrder = restaurant.customersAwaitingOrder().size();
        int readyOrders = restaurant.servableOrders().size();
        int kitchenOrders = restaurant.preparableOrders().size();
        long readyToPay = restaurant.seatedCustomers().stream()
                .filter(Customer::isReadyToPay)
                .count();

        out.println("Staff tasks available:");
        out.println("  Waiter (1 task):");
        out.printf("    [1] Seat        %d waiting, %d free table%s%n",
                waiting, freeTables, plural(freeTables));
        out.printf("    [2] Take order  %d seated customer%s without an order%n",
                awaitingOrder, plural(awaitingOrder));
        out.printf("    [3] Serve       %d READY order%s%n",
                readyOrders, plural(readyOrders));
        out.println("    [4] Wait");
        out.println("  Chef (1 preparation unit):");
        out.printf("    [1] Prepare     %d order%s in the kitchen%n",
                kitchenOrders, plural(kitchenOrders));
        out.println("    [2] Wait");
        out.printf("  Cashier: automatic, %d ready to pay%n", readyToPay);
    }

    private String describeOrder(Customer customer) {
        if (!customer.hasOrder()) {
            return "no order yet";
        }
        Order order = customer.getOrder();
        String description = "order #" + order.getOrderId()
                + " " + order.getItem().getName()
                + " " + order.getStatus()
                + " (" + order.progressText() + ")";
        if (customer.isReadyToPay()) {
            description += ", ready to pay";
        }
        return description;
    }

    private String describeOrderForChoice(Order order) {
        return "#" + order.getOrderId()
                + " " + order.getItem().getName()
                + " for " + order.getCustomer().getName()
                + " (" + order.progressText() + ")";
    }

    private int chooseIndex(String header, List<String> options) {
        out.println(header);
        for (int i = 0; i < options.size(); i++) {
            out.printf("  %d. %s%n", i + 1, options.get(i));
        }
        return readInt("Choose 1-" + options.size() + ": ", 1, options.size()) - 1;
    }

    private int readInt(String prompt, int min, int max) {
        while (true) {
            out.print(prompt);
            String line = nextLine();
            try {
                int value = Integer.parseInt(line.trim());
                if (value >= min && value <= max) {
                    return value;
                }
            } catch (NumberFormatException ignored) {
                // Fall through to the range message.
            }
            out.printf("Enter a whole number from %d to %d.%n", min, max);
        }
    }

    private String nextLine() {
        if (!in.hasNextLine()) {
            throw new NoSuchElementException("Input ended.");
        }
        return in.nextLine();
    }

    private String formatAverage(GameStats stats) {
        if (!stats.hasPaidCustomers()) {
            return "N/A";
        }
        return String.format(Locale.ROOT, "%.1f", stats.averageSatisfaction());
    }

    private String plural(int count) {
        return count == 1 ? "" : "s";
    }
}
