package restaurant.domain;

import java.util.List;
import java.util.Objects;

public final class GameConfig {

    private final int maxTurns;
    private final int tableCount;
    private final List<Integer> arrivalTurns;
    private final int targetPaidCustomers;
    private final int targetRevenueCents;
    private final double targetAverageSatisfaction;

    public GameConfig(int maxTurns,
                      int tableCount,
                      List<Integer> arrivalTurns,
                      int targetPaidCustomers,
                      int targetRevenueCents,
                      double targetAverageSatisfaction) {
        if (maxTurns <= 0) {
            throw new IllegalArgumentException("Max turns must be positive.");
        }
        if (tableCount <= 0) {
            throw new IllegalArgumentException("Table count must be positive.");
        }
        Objects.requireNonNull(arrivalTurns, "arrivalTurns");
        if (arrivalTurns.isEmpty()) {
            throw new IllegalArgumentException("At least one arrival turn is required.");
        }
        for (Integer turn : arrivalTurns) {
            if (turn == null || turn < 1 || turn > maxTurns) {
                throw new IllegalArgumentException(
                        "Arrival turns must be between 1 and " + maxTurns + ".");
            }
        }
        if (targetPaidCustomers < 0) {
            throw new IllegalArgumentException("Target paid customers cannot be negative.");
        }
        if (targetRevenueCents < 0) {
            throw new IllegalArgumentException("Target revenue cannot be negative.");
        }
        if (targetAverageSatisfaction < 0 || targetAverageSatisfaction > 100) {
            throw new IllegalArgumentException("Target average satisfaction must be between 0 and 100.");
        }

        this.maxTurns = maxTurns;
        this.tableCount = tableCount;
        this.arrivalTurns = List.copyOf(arrivalTurns);
        this.targetPaidCustomers = targetPaidCustomers;
        this.targetRevenueCents = targetRevenueCents;
        this.targetAverageSatisfaction = targetAverageSatisfaction;
    }

    public static GameConfig base() {
        return new GameConfig(
                18,
                2,
                List.of(1, 4, 7, 10, 13, 16),
                4,
                4500,
                60.0);
    }

    public boolean isArrivalTurn(int turn) {
        return arrivalTurns.contains(turn);
    }

    public int getMaxTurns() {
        return maxTurns;
    }

    public int getTableCount() {
        return tableCount;
    }

    public List<Integer> getArrivalTurns() {
        return arrivalTurns;
    }

    public int getTargetPaidCustomers() {
        return targetPaidCustomers;
    }

    public int getTargetRevenueCents() {
        return targetRevenueCents;
    }

    public double getTargetAverageSatisfaction() {
        return targetAverageSatisfaction;
    }
}
