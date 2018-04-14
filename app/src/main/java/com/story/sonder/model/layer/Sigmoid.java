package com.story.sonder.model.layer;

import android.support.v4.util.Pair;

import com.story.sonder.model.Tensor;

public class Sigmoid extends Layer implements ILayer {
    @Override
    public Pair<Tensor, Object> forward(Tensor input) {
        Tensor output = input.copy().updateEach((i, v) -> 1 / (1 + Math.exp(-v)));
        return Pair.create(output, output);
    }

    @Override
    public Tensor backward(Tensor gradInput, Object backInput) {
        Tensor output = (Tensor) backInput;
        return output.updateEach((i, v) -> v * (1 - v) * gradInput.getValueAt(i));
    }

    @Override
    public String toString() {
        return "Sigmoid()";
    }
}
