package com.example.juegoreaccion;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.example.juegoreaccion.model.Stimulus;
import com.example.juegoreaccion.model.StimulusType;

import org.junit.Test;

public class ReactionRulesTest {

    @Test
    public void inverseMode_redColorShouldNotReact() {
        Stimulus stimulus = new Stimulus(StimulusType.COLOR, "ROJO", 0);
        assertFalse(stimulus.shouldReact(true));
    }

    @Test
    public void inverseMode_primeNumberShouldNotReact() {
        Stimulus stimulus = new Stimulus(StimulusType.NUMBER, "127", 0);
        assertFalse(stimulus.shouldReact(true));
    }

    @Test
    public void normalMode_shouldAlwaysReact() {
        Stimulus stimulus = new Stimulus(StimulusType.COLOR, "ROJO", 0);
        assertTrue(stimulus.shouldReact(false));
    }

    @Test
    public void inverseMode_nonPrimeNumberShouldReact() {
        Stimulus stimulus = new Stimulus(StimulusType.NUMBER, "100", 0);
        assertTrue(stimulus.shouldReact(true));
    }
}


