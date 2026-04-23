package com.example.juegoreaccion.model;

import java.io.Serializable;

public class GameConfig implements Serializable {

    public static final int MAX_ALLOWED_SECONDS = 30;
    public static final int DEFAULT_ITERATIONS = 20;

    private final String playerName;
    private final GameMode gameMode;
    private final int iterations;
    private final int maxReactionSeconds;
    private final boolean inverseMode;

    public GameConfig(String playerName, GameMode gameMode, int iterations, int maxReactionSeconds, boolean inverseMode) {
        this.playerName = playerName;
        this.gameMode = gameMode;
        this.iterations = iterations;
        this.maxReactionSeconds = Math.min(maxReactionSeconds, MAX_ALLOWED_SECONDS);
        this.inverseMode = inverseMode;
    }

    public String getPlayerName() {
        return playerName;
    }

    public GameMode getGameMode() {
        return gameMode;
    }

    public int getIterations() {
        return iterations;
    }

    public int getMaxReactionSeconds() {
        return maxReactionSeconds;
    }

    public boolean isInverseMode() {
        return inverseMode;
    }
}

