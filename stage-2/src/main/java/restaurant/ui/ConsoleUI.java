package restaurant.ui;

import java.util.Scanner;
import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.OptionalDouble;

import restaurant.domain.Cashier;
import restaurant.domain.Chef;
import restaurant.domain.Customer;
import restaurant.domain.GameResult;
import restaurant.domain.Order;
import restaurant.domain.Restaurant;
import restaurant.domain.Table;
import restaurant.domain.Waiter;

public final class ConsoleUI {
    private final Scanner input;

    public ConsoleUI() {
        this.input = new Scanner(System.in);
    }

    public void showTurnStart(int turn, Restaurant restaurant,
                              Waiter waiter, Chef chef, Cashier cashier) {
        System.out.println();
        System.out.println("=== Turn " + turn + " / 18 ===");
        System.out.println("Revenue: " + formatMoney(restaurant.getRevenueCents())
                + " | Average paid satisfaction: " + formatAverage(restaurant.getAverageSatisfaction()));

        List<Customer> waiting = restaurant.getWaitingQueue().getCustomers();
        System.out.println("Waiting queue:");
        if (waiting.isEmpty()) {
            System.out.println("  (empty)");
        } else {
            waiting.forEach(customer -> showCustomer(customer, "  "));
        }

        System.out.println("Tables:");
        for (Table table : restaurant.getTables()) {
            if (table.isFree()) {
                System.out.println("  Table " + table.getTableId() + ": free");
            } else {
                System.out.println("  Table " + table.getTableId() + ":");
                showCustomer(table.getOccupant(), "    ");
            }
        }

        System.out.println("Staff tasks: " + taskStatus(waiter) + "; " + taskStatus(chef)
                + "; " + taskStatus(cashier));
    }

    public int askWaiterTask() {
        while (true) {
            int choice = readInt("Waiter: 0=wait, 1=seat, 2=take order, 3=serve: ");
            if (choice >= 0 && choice <= 3) {
                return choice;
            }
            showError("Choose 0, 1, 2 or 3.");
        }
    }

    public int askChefTask() {
        while (true) {
            int choice = readInt("Chef: 0=wait, 1=prepare an order: ");
            if (choice == 0 || choice == 1) {
                return choice;
            }
            showError("Choose 0 or 1.");
        }
    }

    public int askCustomerId() {
        return readPositiveInt("Customer ID: ");
    }

    public int askTableId() {
        return readPositiveInt("Table ID: ");
    }

    public void showError(String message) {
        System.out.println("Invalid choice: " + message);
    }

    public void showMessage(String message) {
        System.out.println(message);
    }

    public void showTurnEnd(int turn, Restaurant restaurant) {
        System.out.println("End of turn " + turn + " | Paid customers: " + restaurant.getServedCount()
                + " | Abandoned: " + restaurant.getAbandonedCount()
                + " | Revenue: " + formatMoney(restaurant.getRevenueCents())
                + " | Average paid satisfaction: " + formatAverage(restaurant.getAverageSatisfaction()));
        System.out.println("Bills: unpaid " + formatMoney(restaurant.getOutstandingBillCents())
                + " | lost to walkouts " + formatMoney(restaurant.getLostBillCents()));
    }

    public void showPayment(Customer customer) {
        System.out.println("Cashier collected payment from customer #" + customer.getCustomerId()
                + " (" + formatMoney(customer.getBillCents()) + ").");
    }

    public void showBillHistory(Restaurant restaurant) {
        System.out.println("Bills paid:");
        List<Customer> paid = restaurant.getPaidCustomers();
        if (paid.isEmpty()) {
            System.out.println("  (none)");
        }
        for (Customer customer : paid) {
            System.out.println("  Customer #" + customer.getCustomerId() + ": "
                    + customer.getOrder().getItem().getName() + " " + formatMoney(customer.getBillCents()));
        }
        System.out.println("Unpaid bills: " + formatMoney(restaurant.getOutstandingBillCents())
                + " | Lost to walkouts: " + formatMoney(restaurant.getLostBillCents()));
    }

    public void showResult(GameResult result) {
        System.out.println();
        System.out.println("=== " + (result.isVictory() ? "VICTORY" : "DEFEAT") + " ===");
        System.out.println("Paid customers: " + result.getServedCount());
        System.out.println("Unhappy departures: " + result.getAbandonedCount());
        System.out.println("Revenue: " + formatMoney(result.getRevenueCents()));
        System.out.println("Average paid-customer satisfaction: "
                + formatAverage(result.getAverageSatisfaction()));
        System.out.println("Turns used: " + result.getTurnsUsed());
    }

    private String formatMoney(long cents) {
        return "$" + BigDecimal.valueOf(cents, 2).setScale(2).toPlainString();
    }

    private int readPositiveInt(String prompt) {
        while (true) {
            int value = readInt(prompt);
            if (value > 0) {
                return value;
            }
            showError("Enter a positive number.");
        }
    }

    private int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            if (!input.hasNextLine()) {
                throw new IllegalStateException("Console input ended before the game was complete.");
            }
            String line = input.nextLine().trim();
            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException ex) {
                showError("Enter a whole number.");
            }
        }
    }

    private static String taskStatus(restaurant.domain.Staff staff) {
        return staff.getName() + (staff.hasActedThisTurn() ? " (task used)" : " (task available)");
    }

    private void showCustomer(Customer customer, String indent) {
        System.out.println(indent + "Customer #" + customer.getCustomerId()
                + " | " + customer.getState()
                + " | satisfaction " + customer.getSatisfaction()
                + " | " + orderDescription(customer.getOrder()));
    }

    private String orderDescription(Order order) {
        if (order == null) {
            return "no order";
        }
        return order.getItem().getName() + " " + order.getStatus()
                + " (" + order.getPreparationUnitsDone() + "/"
                + order.getItem().getPreparationUnits() + " units, "
                + formatMoney(order.getPriceCents()) + ")";
    }

    public void showOrderPlaced(Customer customer) {
        throw new UnsupportedOperationException("STAGE2 TODO: Ochi Yuma - ConsoleUI.showOrderPlaced");
    }

    private static String formatAverage(OptionalDouble average) {
        return average.isPresent()
                ? String.format(Locale.ROOT, "%.1f", average.getAsDouble())
                : "N/A";
    }
}
