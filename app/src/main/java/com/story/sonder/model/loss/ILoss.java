package com.story.sonder.model.loss;

import android.support.v4.util.Pair;

import com.story.sonder.model.Tensor;

public interface ILoss {
    Pair<Double, Object> forward(Tensor input, Tensor target);

    Tensor backward(double gradInput, Object backInput);
}
