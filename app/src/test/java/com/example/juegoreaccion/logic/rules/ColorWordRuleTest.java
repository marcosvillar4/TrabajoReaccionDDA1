package com.example.juegoreaccion.logic.rules;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import com.example.juegoreaccion.model.RoundQuestion;

import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class ColorWordRuleTest {

    @Test
    public void shouldGenerateColorWordWithDifferentDisplayedColor() {
        ColorWordRule rule = new ColorWordRule();
        Random random = new Random(42L);

        RoundQuestion question = rule.createQuestion(random);

        Set<String> validColors = new HashSet<>(Arrays.asList(RuleSupport.COLORS));
        assertTrue(validColors.contains(question.getPromptText()));
        assertEquals(4, question.getOptions().size());

        String correctColorName = question.getCorrectOption();
        assertTrue(validColors.contains(correctColorName));
        assertNotEquals(question.getPromptText(), correctColorName);
    }
}

