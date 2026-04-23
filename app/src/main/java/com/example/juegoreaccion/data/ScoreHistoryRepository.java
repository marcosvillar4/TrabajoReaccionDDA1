package com.example.juegoreaccion.data;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ScoreHistoryRepository {

    private static final String PREFS_NAME = "reaction_history";
    private static final String KEY_RECORDS = "records_json";

    private static final String FIELD_PLAYER = "player";
    private static final String FIELD_AVG_MS = "avg_ms";
    private static final String FIELD_MODE = "mode";
    private static final String FIELD_TS = "ts";

    private final SharedPreferences preferences;

    public ScoreHistoryRepository(Context context) {
        preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void saveRecord(String playerName, long averageReactionMs, String modeLabel) {
        JSONArray current = readArray();

        JSONObject item = new JSONObject();
        try {
            item.put(FIELD_PLAYER, playerName);
            item.put(FIELD_AVG_MS, averageReactionMs);
            item.put(FIELD_MODE, modeLabel);
            item.put(FIELD_TS, System.currentTimeMillis());
            current.put(item);
            preferences.edit().putString(KEY_RECORDS, current.toString()).apply();
        } catch (JSONException ignored) {
        }
    }

    public List<ScoreHistoryRecord> getRecordsSortedByAverage() {
        JSONArray source = readArray();
        List<ScoreHistoryRecord> records = new ArrayList<>();

        for (int i = 0; i < source.length(); i++) {
            JSONObject item = source.optJSONObject(i);
            if (item == null) {
                continue;
            }

            String player = item.optString(FIELD_PLAYER, "-");
            long avg = item.optLong(FIELD_AVG_MS, 0L);
            String mode = item.optString(FIELD_MODE, "-");
            long timestamp = item.optLong(FIELD_TS, 0L);
            records.add(new ScoreHistoryRecord(player, avg, mode, timestamp));
        }

        Collections.sort(records, new Comparator<ScoreHistoryRecord>() {
            @Override
            public int compare(ScoreHistoryRecord left, ScoreHistoryRecord right) {
                long leftAvg = left.getAverageReactionMs() <= 0 ? Long.MAX_VALUE : left.getAverageReactionMs();
                long rightAvg = right.getAverageReactionMs() <= 0 ? Long.MAX_VALUE : right.getAverageReactionMs();

                if (leftAvg == rightAvg) {
                    return Long.compare(right.getTimestamp(), left.getTimestamp());
                }
                return Long.compare(leftAvg, rightAvg);
            }
        });

        return records;
    }

    private JSONArray readArray() {
        String raw = preferences.getString(KEY_RECORDS, "[]");
        try {
            return new JSONArray(raw);
        } catch (JSONException ex) {
            return new JSONArray();
        }
    }
}

