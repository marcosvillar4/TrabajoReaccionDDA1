package com.example.juegoreaccion.logic;

import android.graphics.Color;

import com.example.juegoreaccion.model.Stimulus;
import com.example.juegoreaccion.model.StimulusType;

import java.util.Random;

public class StimulusFactory {

    private static final String[] WORDS = {"CASA", "SOL", "LUNA", "PERRO", "NUBE", "RIO"};
    private static final String[] COLOR_NAMES = {"ROJO", "VERDE", "AZUL", "AMARILLO"};
    private static final int[] COLOR_VALUES = {Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW};

    private final Random random = new Random();

    public Stimulus randomStimulus() {
        int selector = random.nextInt(3);
        if (selector == 0) {
            return buildWord();
        }
        if (selector == 1) {
            return buildNumber();
        }
        return buildColor();
    }

    private Stimulus buildWord() {
        String word = WORDS[random.nextInt(WORDS.length)];
        return new Stimulus(StimulusType.WORD, word, Color.BLACK);
    }

    private Stimulus buildNumber() {
        int value = 2 + random.nextInt(198);
        return new Stimulus(StimulusType.NUMBER, String.valueOf(value), Color.BLACK);
    }

    private Stimulus buildColor() {
        int idx = random.nextInt(COLOR_NAMES.length);
        return new Stimulus(StimulusType.COLOR, COLOR_NAMES[idx], COLOR_VALUES[idx]);
    }
}


