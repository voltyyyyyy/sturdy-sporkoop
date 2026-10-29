package restaurant.domain;

import java.util.OptionalDouble;

/**
 * Stage 1 result value object. Owner: Zhang Qiyun.
 */
public final class GameResult {
    private final boolean victory;
    private final int servedCount;
    private final int abandonedCount;
    private final long revenueCents;
    // Empty when nobody has paid. UI displays N/A.
    private final OptionalDouble averageSatisfaction;
    private final int turnsUsed;

    /** Restaurant computes the values; this value object never recomputes scoring rules. */
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
