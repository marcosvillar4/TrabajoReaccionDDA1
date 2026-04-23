package com.example.juegoreaccion.model;

import java.io.Serializable;

public class GameStats implements Serializable {

    private int roundsPlayed;
    private int correctResponses;
    private int wrongResponses;
    private int timeouts;
    private int score;
    private long totalReactionMs;
    private long bestReactionMs = Long.MAX_VALUE;

    public void registerCorrect(long reactionMs, boolean scoreEnabled) {
        roundsPlayed++;
        correctResponses++;

        if (reactionMs >= 0) {
            totalReactionMs += reactionMs;
            bestReactionMs = Math.min(bestReactionMs, reactionMs);
        }

        if (scoreEnabled) {
            int bonus = Math.max(1, (int) ((30000 - Math.max(0, reactionMs)) / 1000));
            score += 10 + bonus;
        }
    }

    public void registerWrong(boolean timeout) {
        roundsPlayed++;
        wrongResponses++;
        if (timeout) {
            timeouts++;
        }
    }

    public int getRoundsPlayed() {
        return roundsPlayed;
    }

    public int getCorrectResponses() {
        return correctResponses;
    }

    public int getWrongResponses() {
        return wrongResponses;
    }

    public int getTimeouts() {
        return timeouts;
    }

    public int getScore() {
        return score;
    }

    public long getAverageReactionMs() {
        return correctResponses == 0 ? 0 : totalReactionMs / correctResponses;
    }

    public long getBestReactionMs() {
        return bestReactionMs == Long.MAX_VALUE ? 0 : bestReactionMs;
    }
}

