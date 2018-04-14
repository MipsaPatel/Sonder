package com.story.sonder.model.layer;

import android.support.v4.util.Pair;

import com.story.sonder.model.Tensor;

public class LogSoftMax extends Layer implements ILayer {
    @Override
    public Pair<Tensor, Object> forward(Tensor input) {
        Tensor output = input.copy();
        final double max = output.reduce(Math::max, null);
        output.updateEach((i, v) -> v - max);
        final double sum = Math.log(output.reduce((x, y) -> x + Math.exp(y), 0.));
        output.updateEach((i, v) -> v - sum);
        return Pair.create(output, output);
    }

    @Override
    public Tensor backward(Tensor gradInput, Object backInput) {
        Tensor output = (Tensor) backInput;
        return output.copy().updateEach((i, v) -> gradInput.getValueAt(i) * (1 - Math.exp(v)));
    }

    @Override
    public String toString() {
        return "LogSoftMax()";
    }
}
