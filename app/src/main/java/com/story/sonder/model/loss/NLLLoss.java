package com.story.sonder.model.loss;

import android.support.v4.util.Pair;

import com.story.sonder.model.Tensor;

public class NLLLoss implements ILoss<Integer> {
    @Override
    public Pair<Double, Object> forward(Tensor input, Integer target) {
        // TODO: Compute the loss and return with input to back-prop
        return null;
    }

    @Override
    public Tensor backward(double gradInput, Object backInput) {
        // TODO: Pass the gradient backwards
        return null;
    }

    @Override
    public String toString() {
        return "NLLLoss()";
    }
}
