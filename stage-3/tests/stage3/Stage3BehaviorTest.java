package restaurant.domain;

import restaurant.control.GameMgr;
import restaurant.ui.ConsoleUI;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

public final class Stage3BehaviorTest {
    private static int checks;

    public static void main(String[] args) {
        comboMealPricing();
        comboChoiceRules();
        happyHourDiscountStacking();
        happyHourLifecycle();
        hostSeating();
        happyHourGame();
        System.out.println("Passed " + checks + " stage 3 behavior checks.");
    }

    private static void comboMealPricing() {
        Menu menu = new Menu();
        MenuItem salad = menu.findByName("Salad");
        MenuItem burger = menu.findByName("Burger");
        MenuItem pasta = menu.findByName("Pasta");

        ComboMeal saladPasta = new ComboMeal(salad, pasta);
        check(saladPasta.getName().equals("Salad + Pasta"));
        check(saladPasta.getPriceCents() == 2430L);
        check(saladPasta.getPreparationUnits() == 3);

        ComboMeal burgerPasta = new ComboMeal(burger, pasta);
        check(burgerPasta.getPriceCents() == 2970L);
        check(burgerPasta.getPreparationUnits() == 3);

        check(saladPasta instanceof MenuItem);
        Order comboOrder = new Order(saladPasta, saladPasta.getPriceCents());
        check(comboOrder.getItem().getName().equals("Salad + Pasta"));

        MenuItem odd = new MenuItem("Odd", 999L, 1);
        check(new ComboMeal(odd, new MenuItem("Odd2", 999L, 1)).getPriceCents() == 1798L);
        expect(IllegalArgumentException.class, () -> new ComboMeal(odd, odd));
        expect(IllegalArgumentException.class, () -> new ComboMeal(odd, new MenuItem("Odd", 999L, 1)));
        expect(IllegalArgumentException.class, () -> new ComboMeal(salad, null));
    }

    private static void comboChoiceRules() {
        Menu menu = new Menu();
        MenuItem regularChoice = new RegularCustomer(1, 1).chooseFood(menu);
        check(regularChoice.getName().equals("Salad + Burger"));
        check(regularChoice.getPriceCents() == 2160L);
        check(regularChoice.getPreparationUnits() == 2);

        MenuItem criticChoice = new CriticCustomer(2, 1).chooseFood(menu);
        check(criticChoice.getName().equals("Pasta + Burger"));
        check(criticChoice.getPriceCents() == 2970L);

        MenuItem vipChoice = new VIPCustomer(3, 1).chooseFood(menu);
        check(vipChoice.getName().equals("Salad"));
    }

    private static void happyHourDiscountStacking() {
        VIPCustomer vip = new VIPCustomer(1, 1);
        HappyHour happyHour = new HappyHour();
        happyHour.activate();
        long vipPrice = vip.discountedPrice(1500L);
        check(vipPrice == 1350L);
        check(happyHour.applyDiscount(vipPrice) == 1080L);

        Restaurant restaurant = new Restaurant();
        Waiter waiter = new Waiter("Waiter");
        Table table = restaurant.getTables().get(0);
        restaurant.getWaitingQueue().add(vip);
        waiter.seat(restaurant, vip, table);
        waiter.resetForTurn();
        check(restaurant.activateHappyHour());
        Order order = waiter.takeOrder(restaurant, vip);
        check(order.getPriceCents() == 648L);
        check(vip.getBillCents() == 648L);

        RegularCustomer regular = new RegularCustomer(2, 2);
        Restaurant other = new Restaurant();
        Waiter otherWaiter = new Waiter("Waiter");
        Table otherTable = other.getTables().get(1);
        other.getWaitingQueue().add(regular);
        otherWaiter.seat(other, regular, otherTable);
        otherWaiter.resetForTurn();
        Order before = otherWaiter.takeOrder(other, regular);
        check(before.getPriceCents() == 2160L);
        other.activateHappyHour();
        check(before.getPriceCents() == 2160L);

        other.tickHappyHour();
        other.tickHappyHour();
        check(!other.getHappyHour().isActive());
        RegularCustomer late = new RegularCustomer(3, 3);
        other.getWaitingQueue().add(late);
        otherWaiter.resetForTurn();
        otherWaiter.seat(other, late, other.getTables().get(0));
        otherWaiter.resetForTurn();
        check(otherWaiter.takeOrder(other, late).getPriceCents() == 2160L);
    }

    private static void happyHourLifecycle() {
        HappyHour happyHour = new HappyHour();
        check(!happyHour.isActive());
        check(happyHour.getRemainingTurns() == 0);
        check(happyHour.activate());
        check(happyHour.isActive());
        check(happyHour.getRemainingTurns() == 2);
        happyHour.tick();
        check(happyHour.isActive() && happyHour.getRemainingTurns() == 1);
        happyHour.tick();
        check(!happyHour.isActive() && happyHour.getRemainingTurns() == 0);
        check(!happyHour.activate());
        expect(IllegalArgumentException.class, () -> happyHour.applyDiscount(-1));

        RegularCustomer waiting = new RegularCustomer(1, 1);
        waiting.loseSatisfaction(40);
        check(waiting.getSatisfaction() == 60);
        happyHour.recover(waiting);
        check(waiting.getSatisfaction() == 75);
        happyHour.recover(waiting);
        happyHour.recover(waiting);
        check(waiting.getSatisfaction() == 100);
        waiting.recoverSatisfaction(Integer.MAX_VALUE);
        check(waiting.getSatisfaction() == 100);
        expect(IllegalArgumentException.class, () -> happyHour.recover(null));
        expect(IllegalArgumentException.class, () -> waiting.recoverSatisfaction(-1));

        RegularCustomer gone = new RegularCustomer(2, 1);
        gone.loseSatisfaction(100);
        gone.leave();
        happyHour.recover(gone);
        check(gone.getSatisfaction() == 0);

        Restaurant restaurant = new Restaurant();
        RegularCustomer queued = new RegularCustomer(3, 1);
        restaurant.getWaitingQueue().add(queued);
        queued.loseSatisfaction(30);
        check(restaurant.activateHappyHour());
        restaurant.recoverWaitingSatisfaction();
        check(queued.getSatisfaction() == 85);
        restaurant.tickHappyHour();
        restaurant.tickHappyHour();
        queued.loseSatisfaction(10);
        restaurant.recoverWaitingSatisfaction();
        check(queued.getSatisfaction() == 75);
    }

    private static void hostSeating() {
        Restaurant restaurant = new Restaurant();
        Host host = new Host("Host");
        Waiter waiter = new Waiter("Waiter");
        RegularCustomer first = new RegularCustomer(1, 1);
        VIPCustomer second = new VIPCustomer(2, 1);
        restaurant.getWaitingQueue().add(first);
        restaurant.getWaitingQueue().add(second);

        check(host.seatNext(restaurant));
        check(first.getState() == CustomerState.SEATED);
        check(restaurant.getTables().get(0).getOccupant() == first);
        check(restaurant.getWaitingQueue().getCustomers().size() == 1);
        check(host.hasActedThisTurn());

        expect(IllegalStateException.class, () -> host.seatNext(restaurant));

        waiter.seat(restaurant, second, restaurant.getTables().get(1));
        check(second.getState() == CustomerState.SEATED);

        host.resetForTurn();
        check(!host.seatNext(restaurant));
        RegularCustomer third = new RegularCustomer(3, 2);
        RegularCustomer fourth = new RegularCustomer(4, 2);
        restaurant.getWaitingQueue().add(third);
        restaurant.getWaitingQueue().add(fourth);
        check(!host.seatNext(restaurant));
        check(!host.hasActedThisTurn());
        expect(IllegalArgumentException.class, () -> host.seatNext(null));

        Restaurant staleQueue = new Restaurant();
        Host otherHost = new Host("Host");
        RegularCustomer noLongerWaiting = new RegularCustomer(5, 1);
        staleQueue.getWaitingQueue().add(noLongerWaiting);
        noLongerWaiting.markSeated();
        expect(IllegalStateException.class, () -> otherHost.seatNext(staleQueue));
        check(staleQueue.getTables().get(0).isFree());
        check(staleQueue.getWaitingQueue().peek().orElseThrow() == noLongerWaiting);
        check(!otherHost.hasActedThisTurn());
    }

    private static void happyHourGame() {
        String input = String.join("\n",
                "1", "2", "1", "1", "1",
                "0", "0", "1", "1",
                "0", "3", "1", "0",
                "0", "2", "2", "1", "2",
                "0", "3", "2", "0",
                "0", "0", "0",
                "0", "0", "0",
                "0", "0", "0",
                "0", "0", "0",
                "0", "0", "0",
                "0", "0", "0",
                "0", "0", "0",
                "0", "0", "0",
                "0", "0", "0",
                "0", "0", "0",
                "0", "0", "0",
                "0", "0", "0",
                "0", "0", "0",
                "");

        String output = runScriptedGame(input);
        check(output.contains("Happy Hour activated! It lasts this turn and the next (2 turn(s) remaining)."));
        check(output.contains("Happy Hour active: 1 turn(s) remaining."));
        check(output.contains("Regular customer #1 ordered Salad + Burger (2 preparation units, $17.28)."));
        check(output.contains("VIP customer #2 ordered Salad (1 preparation unit, $8.10)."));
        check(output.contains("Host seated customer #1 at table 1."));
        check(output.contains("Host seated customer #2 at table 2."));
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
        }
        throw new AssertionError("Expected " + type.getSimpleName() + " at check " + checks);
    }
}
