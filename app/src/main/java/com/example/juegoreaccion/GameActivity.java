package com.example.juegoreaccion;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.juegoreaccion.logic.StimulusFactory;
import com.example.juegoreaccion.model.GameConfig;
import com.example.juegoreaccion.model.GameMode;
import com.example.juegoreaccion.model.GameStats;
import com.example.juegoreaccion.model.Stimulus;

import java.util.Random;

public class GameActivity extends AppCompatActivity {

    public static final String EXTRA_GAME_CONFIG = "extra_game_config";
    public static final String EXTRA_GAME_STATS = "extra_game_stats";
    public static final String EXTRA_VICTORY = "extra_victory";

    private static final int PRE_ROUND_COUNTDOWN_SECONDS = 3;
    private static final int MIN_RANDOM_STIMULUS_DELAY_MS = 400;
    private static final int NEXT_ROUND_DELAY_MS = 900;
    private static final int TIMER_TICK_MS = 50;
    private static final int ONE_SECOND_MS = 1000;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final StimulusFactory stimulusFactory = new StimulusFactory();
    private final Random random = new Random();

    private TextView statusText;
    private TextView stimulusText;
    private TextView instructionText;
    private TextView scoreText;
    private TextView roundText;
    private TextView timerText;

    private GameConfig config;
    private GameStats stats;

    private int currentRound;
    private int currentErrors;
    private int streak;
    private boolean waitingResponse;
    private long stimulusStartMs;
    private long roundStartMs;
    private long currentRoundLimitMs;
    private Stimulus currentStimulus = Stimulus.neutral();
    private Runnable timeoutRunnable;
    private Runnable roundTimerRunnable;
    private Runnable countdownRunnable;
    private Runnable showStimulusRunnable;
    private boolean roundTimerActive;
    private boolean awaitingRoundConfirmation;
    private boolean countdownRunning;
    private boolean waitingStimulusAppearance;
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

        statusText = findViewById(R.id.textStatus);
        stimulusText = findViewById(R.id.textStimulus);
        instructionText = findViewById(R.id.textInstruction);
        scoreText = findViewById(R.id.textScore);
        roundText = findViewById(R.id.textRound);
        timerText = findViewById(R.id.textTimer);
        defaultTimerColor = timerText.getCurrentTextColor();
        Button respondButton = findViewById(R.id.buttonRespond);

        respondButton.setOnClickListener(v -> onUserResponse());

        updateHeader();
        nextRound();
    }

    private void nextRound() {
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
        awaitingRoundConfirmation = true;
        countdownRunning = false;
        waitingStimulusAppearance = false;
        currentRoundLimitMs = calculateRoundLimitMs();
        timerText.setTextColor(defaultTimerColor);
        currentStimulus = Stimulus.neutral();
        stimulusText.setText(currentStimulus.getValue());
        stimulusText.setTextColor(Color.WHITE);
        instructionText.setText(buildInstructionText());
        statusText.setText(R.string.status_confirm_start_round);
        updateTimeCounter(0, currentRoundLimitMs);
        roundText.setText(getString(R.string.round_counter, currentRound, config.getIterations()));
    }

    private void startPreRoundCountdown() {
        countdownRunning = true;
        countdownSecondsLeft = PRE_ROUND_COUNTDOWN_SECONDS;

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
                roundStartMs = SystemClock.elapsedRealtime();
                startRoundElapsedCounter();
                waitingStimulusAppearance = true;
                long randomDelayMs = nextStimulusDelayMs();
                showStimulusRunnable = GameActivity.this::showStimulus;
                handler.postDelayed(showStimulusRunnable, randomDelayMs);
            }
        };

        handler.post(countdownRunnable);
    }

    private void showStimulus() {
        waitingStimulusAppearance = false;
        currentStimulus = stimulusFactory.randomStimulus();
        waitingResponse = true;
        stimulusStartMs = SystemClock.elapsedRealtime();

        stimulusText.setText(currentStimulus.getValue());
        stimulusText.setTextColor(currentStimulus.getColor());
        instructionText.setText(buildInstructionText());
        statusText.setText(getString(R.string.status_react_in_time, currentRoundLimitMs / ONE_SECOND_MS));

        timeoutRunnable = this::onRoundTimeout;
        handler.postDelayed(timeoutRunnable, currentRoundLimitMs);
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

    private void onUserResponse() {
        if (awaitingRoundConfirmation) {
            awaitingRoundConfirmation = false;
            startPreRoundCountdown();
            return;
        }

        if (countdownRunning || waitingStimulusAppearance) {
            registerEarlyPressPenalty();
            return;
        }

        if (!waitingResponse) {
            return;
        }

        closeRoundTimers();

        boolean shouldReact = currentStimulus.shouldReact(config.isInverseMode());
        long reactionMs = SystemClock.elapsedRealtime() - stimulusStartMs;
        showFinalReactionTime(reactionMs, true);

        if (shouldReact) {
            stats.registerCorrect(reactionMs, config.getGameMode() != GameMode.ENTRENAMIENTO);
            streak++;
            statusText.setText(getString(R.string.status_correct_ms, reactionMs));
        } else {
            stats.registerWrong(false);
            currentErrors++;
            streak = 0;
            statusText.setText(R.string.status_wrong_no_reaction);
        }

        updateHeader();
        scheduleNextRound();
    }

    private void registerEarlyPressPenalty() {
        closeRoundTimers();
        stats.registerWrong(false);
        currentErrors++;
        streak = 0;
        statusText.setText(R.string.status_early_press_penalty);
        showFinalReactionTime(0, false);
        updateHeader();
        scheduleNextRound();
    }

    private void onRoundTimeout() {
        if (!waitingResponse) {
            return;
        }

        closeRoundTimers();

        boolean shouldReact = currentStimulus.shouldReact(config.isInverseMode());
        showFinalReactionTime(0, false);
        if (shouldReact) {
            stats.registerWrong(true);
            currentErrors++;
            streak = 0;
            statusText.setText(R.string.status_timeout);
        } else {
            stats.registerCorrect(-1, config.getGameMode() != GameMode.ENTRENAMIENTO);
            streak++;
            statusText.setText(R.string.status_correct_hold);
        }

        updateHeader();
        scheduleNextRound();
    }

    private long calculateRoundLimitMs() {
        long base = config.getMaxReactionSeconds() * ONE_SECOND_MS;
        if (config.getGameMode() == GameMode.ENTRENAMIENTO) {
            return base;
        }

        // Dificultad dinamica opcional: cada 5 aciertos seguidos baja 1 segundo.
        long discount = (streak / 5) * ONE_SECOND_MS;
        return Math.max(2000L, base - discount);
    }

    private String buildInstructionText() {
        if (!config.isInverseMode()) {
            return getString(R.string.instruction_normal_mode);
        }
        return getString(R.string.instruction_inverse_mode);
    }

    private void scheduleNextRound() {
        handler.postDelayed(this::nextRound, NEXT_ROUND_DELAY_MS);
    }

    private long nextStimulusDelayMs() {
        long maxWindowMs = Math.max(MIN_RANDOM_STIMULUS_DELAY_MS, config.getMaxReactionSeconds() * ONE_SECOND_MS);
        if (maxWindowMs == MIN_RANDOM_STIMULUS_DELAY_MS) {
            return MIN_RANDOM_STIMULUS_DELAY_MS;
        }
        int randomPart = random.nextInt((int) (maxWindowMs - MIN_RANDOM_STIMULUS_DELAY_MS + 1));
        return MIN_RANDOM_STIMULUS_DELAY_MS + randomPart;
    }

    private void updateTimeCounter(long elapsedMs, long maxMs) {
        double elapsedSeconds = Math.max(0d, elapsedMs / (double) ONE_SECOND_MS);
        double maxSeconds = Math.max(0.1d, maxMs / (double) ONE_SECOND_MS);
        timerText.setText(getString(R.string.time_counter, elapsedSeconds, maxSeconds));
    }

    private void closeRoundTimers() {
        awaitingRoundConfirmation = false;
        countdownRunning = false;
        waitingStimulusAppearance = false;
        waitingResponse = false;
        handler.removeCallbacks(timeoutRunnable);
        if (countdownRunnable != null) {
            handler.removeCallbacks(countdownRunnable);
        }
        if (showStimulusRunnable != null) {
            handler.removeCallbacks(showStimulusRunnable);
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
        Intent intent = new Intent(this, ResultActivity.class);
        intent.putExtra(EXTRA_GAME_CONFIG, config);
        intent.putExtra(EXTRA_GAME_STATS, stats);
        intent.putExtra(EXTRA_VICTORY, victory);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}









