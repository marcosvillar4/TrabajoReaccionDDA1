package com.example.juegoreaccion.logic.rules;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

final class RuleSupport {

    static final String[] COLORS = {"ROJO", "VERDE", "AZUL", "AMARILLO"};
    static final int[] COLOR_VALUES = {0xFFE53935, 0xFF43A047, 0xFF1E88E5, 0xFFFDD835};
    static final String[] WORDS = {"CASA", "SOL", "LUNA", "PERRO", "NUBE", "RIO", "FLOR", "FUEGO"};
    static final String[] DIRECTIONS = {"ARRIBA", "DERECHA", "ABAJO", "IZQUIERDA"};
    static final String[] ARROWS = {"↑", "→", "↓", "←"};

    private RuleSupport() {
    }

    static String randomFrom(String[] pool, Random random) {
        return pool[random.nextInt(pool.length)];
    }

    static int colorValue(String colorName) {
        for (int i = 0; i < COLORS.length; i++) {
            if (COLORS[i].equalsIgnoreCase(colorName)) {
                return COLOR_VALUES[i];
            }
        }
        return 0xFF000000;
    }

    static List<String> buildOptions(String correctValue, String[] pool, Random random) {
        Set<String> unique = new LinkedHashSet<>();
        unique.add(correctValue);
        while (unique.size() < 4) {
            unique.add(randomFrom(pool, random));
        }

        List<String> options = new ArrayList<>(unique);
        Collections.shuffle(options, random);
        return options;
    }

    static List<String> buildNumberOptions(int correctValue, Random random) {
        Set<Integer> unique = new LinkedHashSet<>();
        unique.add(correctValue);
        while (unique.size() < 4) {
            unique.add(1 + random.nextInt(99));
        }

        List<String> options = new ArrayList<>();
        for (Integer value : unique) {
            options.add(String.valueOf(value));
        }
        Collections.shuffle(options, random);
        return options;
    }
}

