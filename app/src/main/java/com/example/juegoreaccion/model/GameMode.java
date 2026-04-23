package com.example.juegoreaccion.model;

public enum GameMode {
    ENTRENAMIENTO,
    FACIL,
    MEDIO,
    DIFICIL;

    public int defaultMaxTimeSeconds() {
        switch (this) {
            case MEDIO:
                return 15;
            case DIFICIL:
                return 10;
            case ENTRENAMIENTO:
            case FACIL:
            default:
                return 20;
        }
    }

    public int allowedErrors() {
        switch (this) {
            case DIFICIL:
                return 2;
            case MEDIO:
                return 3;
            case FACIL:
                return 4;
            case ENTRENAMIENTO:
            default:
                return Integer.MAX_VALUE;
        }
    }

    @Override
    public String toString() {
        switch (this) {
            case ENTRENAMIENTO:
                return "Entrenamiento";
            case FACIL:
                return "Facil";
            case MEDIO:
                return "Medio";
            case DIFICIL:
                return "Dificil";
            default:
                return name();
        }
    }
}

