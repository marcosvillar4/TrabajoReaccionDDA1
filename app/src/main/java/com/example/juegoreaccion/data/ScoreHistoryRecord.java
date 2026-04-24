package com.example.juegoreaccion.data;

public class ScoreHistoryRecord {

    private final String playerName;
    private final int score;
    private final double correctPercentage;
    private final long averageReactionMs;
    private final String modeLabel;
    private final long timestamp;

    public ScoreHistoryRecord(String playerName, int score, double correctPercentage, long averageReactionMs, String modeLabel, long timestamp) {
        this.playerName = playerName;
        this.score = score;
        this.correctPercentage = correctPercentage;
        this.averageReactionMs = averageReactionMs;
        this.modeLabel = modeLabel;
        this.timestamp = timestamp;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getScore() {
        return score;
    }

    public double getCorrectPercentage() {
        return correctPercentage;
    }

    public long getAverageReactionMs() {
        return averageReactionMs;
    }

    public String getModeLabel() {
        return modeLabel;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
