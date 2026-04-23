package com.example.juegoreaccion.logic.rules;

import android.graphics.Color;

import com.example.juegoreaccion.model.RoundQuestion;

import java.util.List;
import java.util.Random;

public class ColorWordRule implements GameRule {

    @Override
    public String getLabel() {
        return "Elegi el color del texto";
    }

    @Override
    public RoundQuestion createQuestion(Random random) {
        String word = RuleSupport.randomFrom(RuleSupport.COLORS, random);
        String displayColorName = RuleSupport.randomFrom(RuleSupport.COLORS, random);
        while (word.equalsIgnoreCase(displayColorName)) {
            displayColorName = RuleSupport.randomFrom(RuleSupport.COLORS, random);
        }

        List<String> options = RuleSupport.buildOptions(displayColorName, RuleSupport.COLORS, random);
        int correctIndex = options.indexOf(displayColorName);

        return new RoundQuestion(
                getLabel(),
                word,
                RuleSupport.colorValue(displayColorName),
                options,
                correctIndex
        );
    }
}


