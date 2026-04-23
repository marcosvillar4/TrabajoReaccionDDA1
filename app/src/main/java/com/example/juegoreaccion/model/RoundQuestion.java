package com.example.juegoreaccion.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RoundQuestion {

    private final String ruleLabel;
    private final String promptText;
    private final int promptTextColor;
    private final List<String> options;
    private final int correctOptionIndex;

    public RoundQuestion(String ruleLabel, String promptText, int promptTextColor, List<String> options, int correctOptionIndex) {
        this.ruleLabel = ruleLabel;
        this.promptText = promptText;
        this.promptTextColor = promptTextColor;
        this.options = Collections.unmodifiableList(new ArrayList<>(options));
        this.correctOptionIndex = correctOptionIndex;
    }

    public String getRuleLabel() {
        return ruleLabel;
    }

    public String getPromptText() {
        return promptText;
    }

    public int getPromptTextColor() {
        return promptTextColor;
    }

    public List<String> getOptions() {
        return options;
    }

    public int getCorrectOptionIndex() {
        return correctOptionIndex;
    }

    public String getCorrectOption() {
        return options.get(correctOptionIndex);
    }
}

