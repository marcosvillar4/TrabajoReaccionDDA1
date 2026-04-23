package com.example.juegoreaccion.data;

public class PlayerBestRecord {

    private final int bestScore;
    private final long bestAverageMs;

    public PlayerBestRecord(int bestScore, long bestAverageMs) {
        this.bestScore = bestScore;
        this.bestAverageMs = bestAverageMs;
    }

    public int getBestScore() {
        return bestScore;
    }

    public long getBestAverageMs() {
        return bestAverageMs;
    }
}

