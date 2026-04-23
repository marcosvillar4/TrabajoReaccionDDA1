package com.example.juegoreaccion.data;

public class ScoreHistoryRecord {

    private final String playerName;
    private final long averageReactionMs;
    private final String modeLabel;
    private final long timestamp;

    public ScoreHistoryRecord(String playerName, long averageReactionMs, String modeLabel, long timestamp) {
        this.playerName = playerName;
        this.averageReactionMs = averageReactionMs;
        this.modeLabel = modeLabel;
        this.timestamp = timestamp;
    }

    public String getPlayerName() {
        return playerName;
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

