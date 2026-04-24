package com.example.juegoreaccion;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.juegoreaccion.data.ScoreHistoryRecord;
import com.example.juegoreaccion.data.ScoreHistoryRepository;

import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        TextView title = findViewById(R.id.textHistoryTitle);
        ListView listView = findViewById(R.id.listHistory);
        Button backButton = findViewById(R.id.buttonBackMainFromHistory);

        title.setText(R.string.history_title);

        ScoreHistoryRepository repository = new ScoreHistoryRepository(this);
        List<ScoreHistoryRecord> records = repository.getRecordsSortedByScoreThenReaction();

        List<String> rows = new ArrayList<>();
        for (int i = 0; i < records.size(); i++) {
            ScoreHistoryRecord record = records.get(i);
            String scoreText = getString(R.string.history_score, record.getScore());
            String accuracyText = getString(R.string.history_accuracy_percent, record.getCorrectPercentage());
            String averageText = record.getAverageReactionMs() > 0
                    ? getString(R.string.history_avg_ms, record.getAverageReactionMs())
                    : getString(R.string.history_avg_no_data);

            String line = getString(
                    R.string.history_row,
                    i + 1,
                    record.getPlayerName(),
                    record.getModeLabel(),
                    scoreText,
                    accuracyText,
                    averageText
            );
            rows.add(line);
        }

        if (rows.isEmpty()) {
            rows.add(getString(R.string.history_empty));
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                rows
        );
        listView.setAdapter(adapter);

        backButton.setOnClickListener(v -> finish());
    }
}
