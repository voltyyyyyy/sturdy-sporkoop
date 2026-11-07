package restaurant.domain;

import restaurant.control.CustomerFactory;
import restaurant.control.GameMgr;
import restaurant.ui.ConsoleUI;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

public final class Stage2BehaviorTest {
    private static int checks;

    private Stage2BehaviorTest() { }

    public static void main(String[] args) {
        vipPenaltyAndDiscount();
        criticPreference();
        factorySchedule();
        sharedServiceFlow();
        victoryAndDefeat();
        System.out.println("Passed " + checks + " stage 2 behavior checks.");
    }

    private static void vipPenaltyAndDiscount() {
        VIPCustomer vip = new VIPCustomer(1, 1);
        check(vip.waitingPenalty() == 5);
        check(vip.typeLabel().equals("VIP"));
        check(vip.discountedPrice(900L) == 810L);
        check(vip.discountedPrice(1800L) == 1620L);
        check(vip.discountedPrice(999L) == 899L);
        expect(IllegalArgumentException.class, () -> vip.discountedPrice(-1));

        Restaurant restaurant = new Restaurant();
        Waiter waiter = new Waiter("Waiter");
        Cashier cashier = new Cashier("Cashier");
        Table table = restaurant.getTables().get(0);
        restaurant.getWaitingQueue().add(vip);
        waiter.seat(restaurant, vip, table);
        waiter.resetForTurn();
        Order order = waiter.takeOrder(restaurant, vip);
        check(order.getPriceCents() == 810L);
        check(vip.getBillCents() == 810L && vip.hasUnpaidBill());
        check(restaurant.getOutstandingBillCents() == 810L);
        check(restaurant.getRevenueCents() == 0L);

        order.advanceOneUnit();
        order.markServed();
        vip.markServed(2);
        vip.advanceEating(3);
        cashier.collect(restaurant, vip, 4);
        check(restaurant.getRevenueCents() == 810L);
        check(restaurant.getPaidCustomers().equals(List.of(vip)));
        check(vip.getBillCents() == 810L && !vip.hasUnpaidBill());

        VIPCustomer walkout = new VIPCustomer(2, 1);
        restaurant.getWaitingQueue().add(walkout);
        waiter.resetForTurn();
        waiter.seat(restaurant, walkout, restaurant.getTables().get(1));
        waiter.resetForTurn();
        Order lost = waiter.takeOrder(restaurant, walkout);
        check(lost.getPriceCents() == 810L);
        walkout.loseSatisfaction(100);
        restaurant.applyWaitingPenalties();
        check(lost.getStatus() == OrderStatus.CANCELLED);
        check(restaurant.getLostBillCents() == 810L);
        check(restaurant.getRevenueCents() == 810L);
    }

    private static void criticPreference() {
        CriticCustomer critic = new CriticCustomer(1, 1);
        check(critic.waitingPenalty() == 12);
        check(critic.typeLabel().equals("Critic"));
        check(critic.discountedPrice(1800L) == 1800L);
        expect(IllegalArgumentException.class, () -> critic.discountedPrice(-5));

        Menu menu = new Menu();
        MenuItem criticChoice = critic.chooseFood(menu);
        check(criticChoice.getName().equals("Pasta + Burger"));
        check(criticChoice.getPriceCents() == 2970L);
        check(criticChoice.getPreparationUnits() == 3);
        MenuItem regularChoice = new RegularCustomer(1, 1).chooseFood(menu);
        check(regularChoice.getName().equals("Salad + Burger"));
        check(regularChoice.getPriceCents() == 2160L);
    }

    private static void factorySchedule() {
        CustomerFactory factory = new CustomerFactory();
        String[] expected = {
                "Regular", null, null, "VIP", null, null, "Regular", null, null, "Critic",
                null, null, "Regular", null, null, "Regular", null, null
        };
        for (int turn = 1; turn <= 18; turn++) {
            Customer arrival = factory.create(100 + turn, turn);
            if (expected[turn - 1] == null) {
                check(arrival == null);
            } else {
                check(arrival != null && arrival.typeLabel().equals(expected[turn - 1]));
            }
        }
        expect(IllegalArgumentException.class, () -> factory.create(0, 1));
        expect(IllegalArgumentException.class, () -> factory.create(1, 0));
        expect(IllegalArgumentException.class, () -> factory.create(1, 19));
    }

    private static void sharedServiceFlow() {
        runSharedScenario(new RegularCustomer(1, 1), 2160L, 8);
        runSharedScenario(new VIPCustomer(2, 1), 810L, 5);
        runSharedScenario(new CriticCustomer(3, 1), 2970L, 12);

        runAbandonmentScenario(new RegularCustomer(4, 1), 2160L);
        runAbandonmentScenario(new VIPCustomer(5, 1), 810L);
    }

    private static void runSharedScenario(Customer customer, long expectedLockedPrice, int penalty) {
        Restaurant restaurant = new Restaurant();
        Waiter waiter = new Waiter("Waiter");
        Chef chef = new Chef("Chef");
        Cashier cashier = new Cashier("Cashier");
        Table table = restaurant.getTables().get(0);
        restaurant.getWaitingQueue().add(customer);

        waiter.seat(restaurant, customer, table);
        check(customer.getState() == CustomerState.SEATED);
        check(table.getOccupant() == customer);
        waiter.resetForTurn();

        Order order = waiter.takeOrder(restaurant, customer);
        check(order.getPriceCents() == expectedLockedPrice);

        while (order.getStatus() != OrderStatus.READY) {
            chef.prepare(order);
            chef.resetForTurn();
        }
        waiter.resetForTurn();
        waiter.serve(restaurant, customer, 2);
        check(customer.getState() == CustomerState.EATING);
        customer.loseSatisfaction(penalty);
        check(customer.getSatisfaction() == 100);

        customer.advanceEating(3);
        check(customer.getState() == CustomerState.READY_TO_PAY);
        cashier.collect(restaurant, customer, 4);
        check(customer.getState() == CustomerState.PAID);
        check(restaurant.getRevenueCents() == expectedLockedPrice);
        check(table.isFree());
        check(!customer.hasUnpaidBill());
    }

    private static void runAbandonmentScenario(Customer customer, long expectedLockedPrice) {
        Restaurant restaurant = new Restaurant();
        Waiter waiter = new Waiter("Waiter");
        Table table = restaurant.getTables().get(0);
        restaurant.getWaitingQueue().add(customer);
        waiter.seat(restaurant, customer, table);
        waiter.resetForTurn();
        waiter.takeOrder(restaurant, customer);
        customer.loseSatisfaction(100);
        restaurant.applyWaitingPenalties();
        check(customer.getState() == CustomerState.LEFT);
        check(customer.getOrder().getStatus() == OrderStatus.CANCELLED);
        check(customer.getBillCents() == 0L && !customer.hasUnpaidBill());
        check(restaurant.getLostBillCents() == expectedLockedPrice);
        check(table.isFree());
    }

    private static void victoryAndDefeat() {
        String victoryOutput = runScriptedGame(VICTORY_INPUT);
        check(victoryOutput.contains("=== VICTORY ==="));
        check(victoryOutput.contains("Paid customers: 5"));
        check(victoryOutput.contains("Unhappy departures: 0"));
        check(victoryOutput.contains("Revenue: $102.60"));
        check(victoryOutput.contains("Average paid-customer satisfaction: 80.6"));
        check(victoryOutput.contains("Turns used: 18"));
        check(victoryOutput.contains("Unpaid bills: $0.00 | Lost to walkouts: $0.00"));

        String defeatOutput = runScriptedGame(DEFEAT_INPUT);
        check(defeatOutput.contains("=== DEFEAT ==="));
        check(defeatOutput.contains("Paid customers: 0"));
        check(defeatOutput.contains("Revenue: $0.00"));
        check(defeatOutput.contains("Average paid-customer satisfaction: N/A"));
    }

    private static String runScriptedGame(String input) {
        PrintStream originalOut = System.out;
        java.io.InputStream originalIn = System.in;
        ByteArrayOutputStream captured = new ByteArrayOutputStream();
        try {
            System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
            System.setOut(new PrintStream(captured, true, StandardCharsets.UTF_8));
            new GameMgr(new ConsoleUI()).run();
        } finally {
            System.setOut(originalOut);
            System.setIn(originalIn);
        }
        return captured.toString(StandardCharsets.UTF_8);
    }

    private static final String VICTORY_INPUT = String.join("\n",
            "0", "2", "1", "1", "1",
            "0", "0", "1", "1",
            "0", "3", "1", "0",
            "0", "2", "2", "1", "2",
            "0", "3", "2", "0",
            "0", "0", "0",
            "0", "2", "3", "1", "3",
            "0", "0", "1", "3",
            "0", "3", "3", "0",
            "0", "2", "4", "1", "4",
            "0", "0", "1", "4",
            "0", "0", "1", "4",
            "0", "3", "4", "0",
            "0", "2", "5", "1", "5",
            "0", "0", "1", "5",
            "0", "3", "5", "0",
            "0", "0", "0",
            "0", "0", "0",
            "");

    private static final String DEFEAT_INPUT = ("0\n0\n0\n").repeat(18);

    private static void check(boolean condition) {
        checks++;
        if (!condition) {
            throw new AssertionError("Check " + checks + " failed");
        }
    }

    private static void expect(Class<? extends RuntimeException> type, Runnable action) {
        checks++;
        try {
            action.run();
        } catch (RuntimeException exception) {
            if (type.isInstance(exception)) {
                return;
            }
            throw new AssertionError("Expected " + type.getSimpleName(), exception);
        }
        throw new AssertionError("Expected " + type.getSimpleName());
    }
}
