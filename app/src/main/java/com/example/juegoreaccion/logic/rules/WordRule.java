package com.example.juegoreaccion.logic.rules;

import android.graphics.Color;

import com.example.juegoreaccion.model.RoundQuestion;

import java.util.List;
import java.util.Random;

public class WordRule implements GameRule {

    @Override
    public String getLabel() {
        return "Elegi la palabra mostrada";
    }

    @Override
    public RoundQuestion createQuestion(Random random) {
        String word = RuleSupport.randomFrom(RuleSupport.WORDS, random);
        List<String> options = RuleSupport.buildOptions(word, RuleSupport.WORDS, random);
        int correctIndex = options.indexOf(word);

        return new RoundQuestion(
                getLabel(),
                word,
                Color.BLACK,
                options,
                correctIndex
        );
    }
}

