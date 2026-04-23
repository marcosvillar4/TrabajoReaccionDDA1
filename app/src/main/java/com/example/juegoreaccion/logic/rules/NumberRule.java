package com.example.juegoreaccion.logic.rules;

import android.graphics.Color;

import com.example.juegoreaccion.model.RoundQuestion;

import java.util.List;
import java.util.Random;

public class NumberRule implements GameRule {

    @Override
    public String getLabel() {
        return "Elegi el numero mostrado";
    }

    @Override
    public RoundQuestion createQuestion(Random random) {
        int value = 1 + random.nextInt(99);
        List<String> options = RuleSupport.buildNumberOptions(value, random);
        int correctIndex = options.indexOf(String.valueOf(value));

        return new RoundQuestion(
                getLabel(),
                String.valueOf(value),
                Color.BLACK,
                options,
                correctIndex
        );
    }
}

