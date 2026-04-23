package com.example.juegoreaccion.logic.rules;

import com.example.juegoreaccion.model.RoundQuestion;

import java.util.Random;

public interface GameRule {

    String getLabel();

    RoundQuestion createQuestion(Random random);
}

