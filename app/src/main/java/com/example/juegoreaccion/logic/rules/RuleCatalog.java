package com.example.juegoreaccion.logic.rules;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public final class RuleCatalog {

    private static final List<GameRule> RULES = Collections.unmodifiableList(Arrays.asList(
            new ColorWordRule(),
            new WordRule(),
            new NumberRule(),
            new DirectionRule()
    ));

    private RuleCatalog() {
    }

    public static GameRule randomRule(Random random) {
        return RULES.get(random.nextInt(RULES.size()));
    }

    public static List<GameRule> getRules() {
        return RULES;
    }
}

