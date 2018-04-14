package com.story.sonder.model.layer;

import android.support.v4.util.Pair;

import com.story.sonder.model.Tensor;

public class ReLU extends Layer implements ILayer {
    @Override
    public Pair<Tensor, Object> forward(Tensor input) {
        Tensor output = input.copy().updateEach((i, v) -> Math.max(0, v));
        return Pair.create(output, input);
    }

    @Override
    public Tensor backward(Tensor gradInput, Object backInput) {
        Tensor input = (Tensor) backInput;
        return gradInput.copy().updateEach((i, v) -> input.getValueAt(i) < 0 ? 0 : v);
    }

    @Override
    public String toString() {
        return "ReLU()";
    }
}
