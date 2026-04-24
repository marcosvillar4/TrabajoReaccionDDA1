package com.example.juegoreaccion;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.juegoreaccion.data.BestScoreRepository;
import com.example.juegoreaccion.data.PlayerBestRecord;
import com.example.juegoreaccion.data.ScoreHistoryRepository;
import com.example.juegoreaccion.model.GameConfig;
import com.example.juegoreaccion.model.GameMode;
import com.example.juegoreaccion.model.GameStats;

public class ResultActivity extends AppCompatActivity {

    private GameConfig config;
    private GameStats stats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        config = (GameConfig) getIntent().getSerializableExtra(GameActivity.EXTRA_GAME_CONFIG);
        stats = (GameStats) getIntent().getSerializableExtra(GameActivity.EXTRA_GAME_STATS);
        boolean victory = getIntent().getBooleanExtra(GameActivity.EXTRA_VICTORY, false);
        String ruleLabel = getIntent().getStringExtra(GameActivity.EXTRA_RULE_LABEL);

        if (config == null || stats == null) {
            finish();
            return;
        }

        if (ruleLabel == null) {
            ruleLabel = "-";
        }

        TextView resultTitle = findViewById(R.id.textResultTitle);
        TextView detailText = findViewById(R.id.textResultDetail);
        TextView bestText = findViewById(R.id.textBestRecord);
        Button retryButton = findViewById(R.id.buttonRetry);
        Button menuButton = findViewById(R.id.buttonBackMenu);

        resultTitle.setText(victory ? R.string.result_victory : R.string.result_defeat);
        detailText.setText(buildDetailText(ruleLabel));

        if (savedInstanceState == null) {
            new ScoreHistoryRepository(this).saveRecord(
                    config.getPlayerName(),
                    stats.getScore(),
                    stats.getCorrectPercentage(),
                    stats.getAverageReactionMs(),
                    buildModeLabel()
            );
        }

        BestScoreRepository repository = new BestScoreRepository(this);
        if (config.getGameMode() != GameMode.ENTRENAMIENTO) {
            repository.updateBestRecordIfNeeded(
                    config.getPlayerName(),
                    config.getGameMode(),
                    config.isInverseMode(),
                    stats.getScore(),
                    stats.getAverageReactionMs()
            );
        }

        PlayerBestRecord bestRecord = repository.getBestRecord(
                config.getPlayerName(),
                config.getGameMode(),
                config.isInverseMode()
        );

        bestText.setText(getString(R.string.best_record_text, bestRecord.getBestScore(), bestRecord.getBestAverageMs()));

        retryButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, GameActivity.class);
            intent.putExtra(GameActivity.EXTRA_GAME_CONFIG, config);
            startActivity(intent);
            finish();
        });

        menuButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, Main.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }

    private String buildDetailText(String ruleLabel) {
        String trainingTag = config.getGameMode() == GameMode.ENTRENAMIENTO
                ? getString(R.string.training_no_score)
                : "";

        return getString(
                R.string.result_detail_text,
                config.getPlayerName(),
                ruleLabel,
                config.getGameMode().toString(),
                config.isInverseMode() ? getString(R.string.mode_inverse_enabled) : getString(R.string.mode_inverse_disabled),
                stats.getRoundsPlayed(),
                config.getIterations(),
                stats.getCorrectResponses(),
                stats.getWrongResponses(),
                stats.getTimeouts(),
                stats.getCorrectPercentage(),
                stats.getAverageReactionMs(),
                stats.getBestReactionMs(),
                stats.getScore(),
                trainingTag
        );
    }

    private String buildModeLabel() {
        String modeName = config.getGameMode().toString();
        if (config.isInverseMode()) {
            return modeName + " - " + getString(R.string.mode_inverse_enabled);
        }
        return modeName;
    }
}







