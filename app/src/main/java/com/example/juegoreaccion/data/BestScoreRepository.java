package com.example.juegoreaccion.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.juegoreaccion.model.GameMode;

public class BestScoreRepository {

    private static final String PREFS_NAME = "reaction_best_scores";
    private static final String SCORE_SUFFIX = "_score";
    private static final String AVG_SUFFIX = "_avg";

    private final SharedPreferences preferences;

    public BestScoreRepository(Context context) {
        preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public PlayerBestRecord getBestRecord(String player, GameMode mode, boolean inverseMode) {
        String keyBase = buildKeyBase(player, mode, inverseMode);
        int bestScore = preferences.getInt(keyBase + SCORE_SUFFIX, 0);
        long bestAvg = preferences.getLong(keyBase + AVG_SUFFIX, 0L);
        return new PlayerBestRecord(bestScore, bestAvg);
    }

    public void updateBestRecordIfNeeded(String player, GameMode mode, boolean inverseMode, int score, long averageMs) {
        String keyBase = buildKeyBase(player, mode, inverseMode);
        int storedScore = preferences.getInt(keyBase + SCORE_SUFFIX, 0);
        long storedAvg = preferences.getLong(keyBase + AVG_SUFFIX, 0L);

        boolean shouldUpdate = score > storedScore || (score == storedScore && averageMs > 0 && (storedAvg == 0 || averageMs < storedAvg));

        if (shouldUpdate) {
            preferences.edit()
                    .putInt(keyBase + SCORE_SUFFIX, score)
                    .putLong(keyBase + AVG_SUFFIX, averageMs)
                    .apply();
        }
    }

    private String buildKeyBase(String player, GameMode mode, boolean inverseMode) {
        String safePlayer = player.trim().toLowerCase().replace(" ", "_");
        return safePlayer + "_" + mode.name().toLowerCase() + "_" + (inverseMode ? "inv" : "std");
    }
}

