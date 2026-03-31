package dev.onlynelchilling.nlobbyblocks.model;

import java.util.UUID;

public class ActiveBlock {

    private final UUID placerUuid;
    private final int totalSeconds;
    private int remainingSeconds;

    public ActiveBlock(UUID placerUuid, int totalSeconds) {
        this.placerUuid = placerUuid;
        this.totalSeconds = totalSeconds;
        this.remainingSeconds = totalSeconds;
    }

    public void tick() {
        remainingSeconds--;
    }

    public boolean isExpired() {
        return remainingSeconds <= 0;
    }

    public UUID getPlacerUuid() {
        return placerUuid;
    }

    public int getRemainingSeconds() {
        return remainingSeconds;
    }

    public float getDamageProgress() {
        if (totalSeconds == 0) return 1.0f;
        return 1.0f - ((float) remainingSeconds / totalSeconds);
    }
}
