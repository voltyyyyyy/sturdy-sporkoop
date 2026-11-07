package restaurant.domain;

import java.util.OptionalDouble;

public final class GameResult {
    private final boolean victory;
    private final int servedCount;
    private final int abandonedCount;
    private final long revenueCents;
    private final OptionalDouble averageSatisfaction;
    private final int turnsUsed;

    public GameResult(boolean victory, int servedCount, int abandonedCount,
                      long revenueCents, OptionalDouble averageSatisfaction, int turnsUsed) {
        this.victory = victory;
        this.servedCount = servedCount;
        this.abandonedCount = abandonedCount;
        this.revenueCents = revenueCents;
        this.averageSatisfaction = averageSatisfaction;
        this.turnsUsed = turnsUsed;
    }

    public boolean isVictory() { return victory; }
    public int getServedCount() { return servedCount; }
    public int getAbandonedCount() { return abandonedCount; }
    public long getRevenueCents() { return revenueCents; }
    public OptionalDouble getAverageSatisfaction() { return averageSatisfaction; }
    public int getTurnsUsed() { return turnsUsed; }
}
