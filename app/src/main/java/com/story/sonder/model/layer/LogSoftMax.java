package com.story.sonder.model.layer;

import android.support.v4.util.Pair;

import com.story.sonder.model.Tensor;

public class LogSoftMax extends Layer implements ILayer {
    @Override
    public Pair<Tensor, Object> forward(Tensor input) {
        // TODO: Forward pass. Return the output and input to back-prop
        return null;
    }

    @Override
    public Tensor backward(Tensor gradInput, Object backInput) {
        // TODO: Backward pass. Compute gradients for local parameters. Return the gradient for back-prop
        return null;
    }

    @Override
    public String toString() {
        return "LogSoftMax()";
    }
}
