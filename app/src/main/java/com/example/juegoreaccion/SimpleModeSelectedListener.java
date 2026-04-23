package com.example.juegoreaccion;

import android.view.View;
import android.widget.AdapterView;

import com.example.juegoreaccion.model.GameMode;

import java.util.function.Consumer;

public class SimpleModeSelectedListener implements AdapterView.OnItemSelectedListener {

    private final Consumer<GameMode> callback;

    public SimpleModeSelectedListener(Consumer<GameMode> callback) {
        this.callback = callback;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Object selectedItem = parent.getItemAtPosition(position);
        if (selectedItem instanceof GameMode) {
            callback.accept((GameMode) selectedItem);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        callback.accept(GameMode.FACIL);
    }
}

