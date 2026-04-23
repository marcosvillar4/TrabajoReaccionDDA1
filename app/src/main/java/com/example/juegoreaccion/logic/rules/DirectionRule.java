package com.example.juegoreaccion.logic.rules;

import android.graphics.Color;

import com.example.juegoreaccion.model.RoundQuestion;

import java.util.List;
import java.util.Random;

public class DirectionRule implements GameRule {

    @Override
    public String getLabel() {
        return "Elegi la direccion de la flecha";
    }

    @Override
    public RoundQuestion createQuestion(Random random) {
        int idx = random.nextInt(RuleSupport.ARROWS.length);
        String arrow = RuleSupport.ARROWS[idx];
        String correctDirection = RuleSupport.DIRECTIONS[idx];
        List<String> options = RuleSupport.buildOptions(correctDirection, RuleSupport.DIRECTIONS, random);
        int correctIndex = options.indexOf(correctDirection);

        return new RoundQuestion(
                getLabel(),
                arrow,
                Color.BLACK,
                options,
                correctIndex
        );
    }
}

