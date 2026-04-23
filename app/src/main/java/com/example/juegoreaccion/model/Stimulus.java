package com.example.juegoreaccion.model;

import android.graphics.Color;

import java.io.Serializable;

public class Stimulus implements Serializable {

    private final StimulusType type;
    private final String value;
    private final int color;

    public Stimulus(StimulusType type, String value, int color) {
        this.type = type;
        this.value = value;
        this.color = color;
    }

    public StimulusType getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public int getColor() {
        return color;
    }

    public boolean shouldReact(boolean inverseMode) {
        if (!inverseMode) {
            return true;
        }

        if (type == StimulusType.COLOR) {
            return !"ROJO".equalsIgnoreCase(value);
        }

        if (type == StimulusType.NUMBER) {
            try {
                int number = Integer.parseInt(value);
                return !isPrime(number);
            } catch (NumberFormatException ignored) {
                return true;
            }
        }

        return true;
    }

    public static Stimulus neutral() {
        return new Stimulus(StimulusType.WORD, "...", Color.WHITE);
    }

    public static boolean isPrime(int number) {
        if (number < 2) {
            return false;
        }
        if (number == 2) {
            return true;
        }
        if (number % 2 == 0) {
            return false;
        }
        for (int i = 3; i * i <= number; i += 2) {
            if (number % i == 0) {
                return false;
            }
        }
        return true;
    }
}

