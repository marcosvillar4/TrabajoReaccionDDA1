package com.example.juegoreaccion.logic;

public final class GameAnswerEvaluator {

    private GameAnswerEvaluator() {
    }

    public static boolean isCorrectSelection(boolean inverseMode, int selectedIndex, int correctOptionIndex) {
        return inverseMode ? selectedIndex != correctOptionIndex : selectedIndex == correctOptionIndex;
    }
}

