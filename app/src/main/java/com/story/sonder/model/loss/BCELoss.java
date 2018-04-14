package com.story.sonder.model.loss;

import android.support.v4.util.Pair;

import com.story.sonder.model.Tensor;

public class BCELoss implements ILoss<Integer> {
    @Override
    public Pair<Double, Object> forward(Tensor input, Integer target) {
        double in = input.getValueAt(0);
        return Pair.create(-target * Math.log(in) - (1 - target) * Math.log(1 - in),
                Pair.create(in, target));
    }

    @Override
    public Tensor backward(double gradInput, Object backInput) {
        Pair input = (Pair) backInput;
        double in = (double) input.first;
        int t = (int) input.second;
        return new Tensor(new double[]{(in - t) / (in * (1 - in) + 1e-8)});
    }

    @Override
    public String toString() {
        return "BCELoss()";
    }
}
