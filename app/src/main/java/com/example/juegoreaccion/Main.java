package com.example.juegoreaccion;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.example.juegoreaccion.model.GameConfig;
import com.example.juegoreaccion.model.GameMode;

public class Main extends AppCompatActivity {

	private EditText playerNameInput;
	private EditText iterationsInput;
	private EditText maxTimeInput;
	private Spinner modeSpinner;
	private SwitchCompat inverseModeSwitch;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		playerNameInput = findViewById(R.id.inputPlayerName);
		iterationsInput = findViewById(R.id.inputIterations);
		maxTimeInput = findViewById(R.id.inputMaxTime);
		modeSpinner = findViewById(R.id.spinnerMode);
		inverseModeSwitch = findViewById(R.id.switchInverseMode);
		Button startButton = findViewById(R.id.buttonStartGame);
		Button historyButton = findViewById(R.id.buttonViewHistory);

		ArrayAdapter<GameMode> adapter = new ArrayAdapter<>(
				this,
				android.R.layout.simple_spinner_item,
				GameMode.values()
		);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		modeSpinner.setAdapter(adapter);

		setDefaultValues();
		modeSpinner.setOnItemSelectedListener(new SimpleModeSelectedListener(this::updateTimeForMode));

		startButton.setOnClickListener(v -> startGame());
		historyButton.setOnClickListener(v -> startActivity(new Intent(this, HistoryActivity.class)));
	}

	private void setDefaultValues() {
		iterationsInput.setText(String.valueOf(GameConfig.DEFAULT_ITERATIONS));
		GameMode selected = (GameMode) modeSpinner.getSelectedItem();
		updateTimeForMode(selected == null ? GameMode.FACIL : selected);
	}

	private void updateTimeForMode(GameMode mode) {
		maxTimeInput.setText(String.valueOf(mode.defaultMaxTimeSeconds()));
	}

	private void startGame() {
		String playerName = playerNameInput.getText().toString().trim();
		if (playerName.isEmpty()) {
			Toast.makeText(this, R.string.error_player_required, Toast.LENGTH_SHORT).show();
			return;
		}

		int iterations;
		int maxSeconds;
		try {
			iterations = Integer.parseInt(iterationsInput.getText().toString().trim());
			maxSeconds = Integer.parseInt(maxTimeInput.getText().toString().trim());
		} catch (NumberFormatException ex) {
			Toast.makeText(this, R.string.error_numeric_values, Toast.LENGTH_SHORT).show();
			return;
		}

		if (iterations <= 0) {
			Toast.makeText(this, R.string.error_iterations, Toast.LENGTH_SHORT).show();
			return;
		}

		if (maxSeconds <= 0 || maxSeconds > GameConfig.MAX_ALLOWED_SECONDS) {
			Toast.makeText(this, R.string.error_max_time, Toast.LENGTH_SHORT).show();
			return;
		}

		GameMode mode = (GameMode) modeSpinner.getSelectedItem();
		if (mode == null) {
			mode = GameMode.FACIL;
		}

		GameConfig config = new GameConfig(
				playerName,
				mode,
				iterations,
				maxSeconds,
				inverseModeSwitch.isChecked()
		);

		Intent intent = new Intent(this, GameActivity.class);
		intent.putExtra(GameActivity.EXTRA_GAME_CONFIG, config);
		startActivity(intent);
	}
}
