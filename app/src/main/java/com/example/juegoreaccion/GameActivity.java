package com.example.juegoreaccion;

import android.content.Intent;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.juegoreaccion.logic.GameAnswerEvaluator;
import com.example.juegoreaccion.logic.rules.GameRule;
import com.example.juegoreaccion.logic.rules.RuleCatalog;
import com.example.juegoreaccion.model.GameConfig;
import com.example.juegoreaccion.model.GameMode;
import com.example.juegoreaccion.model.GameStats;
import com.example.juegoreaccion.model.RoundQuestion;

import java.util.Random;

public class GameActivity extends AppCompatActivity {

    public static final String EXTRA_GAME_CONFIG = "extra_game_config";
    public static final String EXTRA_GAME_STATS = "extra_game_stats";
    public static final String EXTRA_VICTORY = "extra_victory";
    public static final String EXTRA_RULE_LABEL = "extra_rule_label";

    private static final int PRE_ROUND_COUNTDOWN_SECONDS = 3;
    private static final int NEXT_ROUND_DELAY_MS = 900;
    private static final int TIMER_TICK_MS = 50;
    private static final int ONE_SECOND_MS = 1000;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Random random = new Random();
    private ToneGenerator toneGenerator;

    private TextView statusText;
    private TextView promptText;
    private TextView ruleText;
    private TextView scoreText;
    private TextView roundText;
    private TextView timerText;
    private final Button[] optionButtons = new Button[4];

    private GameConfig config;
    private GameStats stats;
    private GameRule selectedRule;
    private RoundQuestion currentQuestion;

    private int currentRound;
    private int currentErrors;
    private int streak;
    private boolean waitingResponse;
    private long questionShownMs;
    private long roundStartMs;
    private long currentRoundLimitMs;
    private Runnable timeoutRunnable;
    private Runnable roundTimerRunnable;
    private Runnable countdownRunnable;
    private boolean roundTimerActive;
    private boolean countdownRunning;
    private int countdownSecondsLeft;
    private int defaultTimerColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        config = (GameConfig) getIntent().getSerializableExtra(EXTRA_GAME_CONFIG);
        if (config == null) {
            finish();
            return;
        }

        stats = new GameStats();
        selectedRule = RuleCatalog.randomRule(random);

        statusText = findViewById(R.id.textStatus);
        promptText = findViewById(R.id.textPrompt);
        ruleText = findViewById(R.id.textRuleTitle);
        scoreText = findViewById(R.id.textScore);
        roundText = findViewById(R.id.textRound);
        timerText = findViewById(R.id.textTimer);
        optionButtons[0] = findViewById(R.id.buttonOption1);
        optionButtons[1] = findViewById(R.id.buttonOption2);
        optionButtons[2] = findViewById(R.id.buttonOption3);
        optionButtons[3] = findViewById(R.id.buttonOption4);
        defaultTimerColor = timerText.getCurrentTextColor();
        toneGenerator = createToneGenerator();

        for (int i = 0; i < optionButtons.length; i++) {
            final int index = i;
            optionButtons[i].setOnClickListener(v -> onOptionSelected(index));
        }

        ruleText.setText(getString(R.string.rule_selected, selectedRule.getLabel()));
        setAnswerButtonsEnabled(false);
        updateHeader();
        startInitialCountdown();
    }

    private void startInitialCountdown() {
        countdownRunning = true;
        countdownSecondsLeft = PRE_ROUND_COUNTDOWN_SECONDS;
        statusText.setText(R.string.status_prepare);

        countdownRunnable = new Runnable() {
            @Override
            public void run() {
                if (!countdownRunning) {
                    return;
                }

                if (countdownSecondsLeft > 0) {
                    statusText.setText(getString(R.string.status_countdown, countdownSecondsLeft));
                    countdownSecondsLeft--;
                    handler.postDelayed(this, ONE_SECOND_MS);
                    return;
                }

                countdownRunning = false;
                statusText.setText(R.string.status_get_ready);
                startRound();
            }
        };

        handler.post(countdownRunnable);
    }

    private void startRound() {
        if (currentRound >= config.getIterations()) {
            finishGame(true);
            return;
        }

        if (currentErrors >= config.getGameMode().allowedErrors()) {
            finishGame(false);
            return;
        }

        currentRound++;
        waitingResponse = false;
        setAnswerButtonsEnabled(false);
        currentRoundLimitMs = calculateRoundLimitMs();
        timerText.setTextColor(defaultTimerColor);

        currentQuestion = selectedRule.createQuestion(random);
        ruleText.setText(getString(R.string.rule_selected, currentQuestion.getRuleLabel()));
        promptText.setText(currentQuestion.getPromptText());
        promptText.setTextColor(currentQuestion.getPromptTextColor());
        bindOptions(currentQuestion);

        statusText.setText(R.string.status_select_answer);
        updateTimeCounter(0, currentRoundLimitMs);
        roundText.setText(getString(R.string.round_counter, currentRound, config.getIterations()));

        roundStartMs = SystemClock.elapsedRealtime();
        questionShownMs = roundStartMs;
        waitingResponse = true;
        setAnswerButtonsEnabled(true);
        startRoundElapsedCounter();

        timeoutRunnable = this::onRoundTimeout;
        handler.postDelayed(timeoutRunnable, currentRoundLimitMs);
    }

    private void bindOptions(RoundQuestion question) {
        for (int i = 0; i < optionButtons.length; i++) {
            optionButtons[i].setText(question.getOptions().get(i));
        }
    }

    private void setAnswerButtonsEnabled(boolean enabled) {
        for (Button optionButton : optionButtons) {
            optionButton.setEnabled(enabled);
        }
    }

    private void startRoundElapsedCounter() {
        roundTimerActive = true;
        if (roundTimerRunnable != null) {
            handler.removeCallbacks(roundTimerRunnable);
        }

        roundTimerRunnable = new Runnable() {
            @Override
            public void run() {
                if (!roundTimerActive) {
                    return;
                }
                long elapsed = SystemClock.elapsedRealtime() - roundStartMs;
                updateTimeCounter(elapsed, currentRoundLimitMs);
                handler.postDelayed(this, TIMER_TICK_MS);
            }
        };

        handler.post(roundTimerRunnable);
    }

    private void stopRoundElapsedCounter() {
        roundTimerActive = false;
        if (roundTimerRunnable != null) {
            handler.removeCallbacks(roundTimerRunnable);
        }
    }

    private void onOptionSelected(int selectedIndex) {
        if (!waitingResponse || currentQuestion == null) {
            return;
        }

        long reactionMs = SystemClock.elapsedRealtime() - questionShownMs;
        boolean correct = GameAnswerEvaluator.isCorrectSelection(
                config.isInverseMode(),
                selectedIndex,
                currentQuestion.getCorrectOptionIndex()
        );

        closeRoundTimers();
        showFinalReactionTime(reactionMs, true);
        setAnswerButtonsEnabled(false);

        if (correct) {
            stats.registerCorrect(reactionMs, config.getGameMode() != GameMode.ENTRENAMIENTO);
            streak++;
            playCorrectSound();
            statusText.setText(config.isInverseMode()
                    ? getString(R.string.status_correct_inverse_ms, reactionMs)
                    : getString(R.string.status_correct_ms, reactionMs));
        } else {
            stats.registerWrong(false);
            currentErrors++;
            streak = 0;
            playIncorrectSound();
            if (config.isInverseMode()) {
                statusText.setText(R.string.status_wrong_inverse);
            } else {
                statusText.setText(getString(R.string.status_wrong_option, currentQuestion.getCorrectOption()));
            }
        }

        updateHeader();
        if (shouldFinishGame()) {
            finishGame(currentErrors < config.getGameMode().allowedErrors());
            return;
        }

        scheduleNextRound();
    }

    private void onRoundTimeout() {
        if (!waitingResponse) {
            return;
        }

        closeRoundTimers();
        setAnswerButtonsEnabled(false);
        showFinalReactionTime(0, false);

        stats.registerWrong(true);
        currentErrors++;
        streak = 0;
        playIncorrectSound();
        statusText.setText(R.string.status_timeout);

        updateHeader();
        if (shouldFinishGame()) {
            finishGame(false);
            return;
        }

        scheduleNextRound();
    }

    private long calculateRoundLimitMs() {
        long base = (long) config.getMaxReactionSeconds() * ONE_SECOND_MS;
        if (config.getGameMode() == GameMode.ENTRENAMIENTO) {
            return base;
        }

        long discount = (long) (streak / 5) * ONE_SECOND_MS;
        return Math.max(2000L, base - discount);
    }

    private void scheduleNextRound() {
        handler.postDelayed(this::startRound, NEXT_ROUND_DELAY_MS);
    }

    private void updateTimeCounter(long elapsedMs, long maxMs) {
        double elapsedSeconds = Math.max(0d, elapsedMs / (double) ONE_SECOND_MS);
        double maxSeconds = Math.max(0.1d, maxMs / (double) ONE_SECOND_MS);
        timerText.setText(getString(R.string.time_counter, elapsedSeconds, maxSeconds));
    }

    private void closeRoundTimers() {
        countdownRunning = false;
        waitingResponse = false;
        handler.removeCallbacks(timeoutRunnable);
        if (countdownRunnable != null) {
            handler.removeCallbacks(countdownRunnable);
        }
        stopRoundElapsedCounter();
    }

    private void showFinalReactionTime(long reactionMs, boolean hasReaction) {
        timerText.setTextColor(Color.RED);
        if (hasReaction) {
            timerText.setText(getString(R.string.final_reaction_time_ms, reactionMs));
            return;
        }
        timerText.setText(R.string.final_reaction_time_none);
    }

    private void updateHeader() {
        scoreText.setText(getString(R.string.score_and_errors, stats.getScore(), currentErrors));
    }

    private void finishGame(boolean victory) {
        closeRoundTimers();
        Intent intent = new Intent(this, ResultActivity.class);
        intent.putExtra(EXTRA_GAME_CONFIG, config);
        intent.putExtra(EXTRA_GAME_STATS, stats);
        intent.putExtra(EXTRA_VICTORY, victory);
        intent.putExtra(EXTRA_RULE_LABEL, selectedRule.getLabel());
        startActivity(intent);
        finish();
    }

    private boolean shouldFinishGame() {
        return currentErrors >= config.getGameMode().allowedErrors() || currentRound >= config.getIterations();
    }

    private ToneGenerator createToneGenerator() {
        try {
            return new ToneGenerator(AudioManager.STREAM_MUSIC, 90);
        } catch (RuntimeException ex) {
            return null;
        }
    }

    private void playCorrectSound() {
        if (toneGenerator != null) {
            toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP, 140);
        }
    }

    private void playIncorrectSound() {
        if (toneGenerator != null) {
            toneGenerator.startTone(ToneGenerator.TONE_PROP_NACK, 160);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (toneGenerator != null) {
            toneGenerator.release();
            toneGenerator = null;
        }
        handler.removeCallbacksAndMessages(null);
    }
}




