package com.story.sonder.model.layer;

import android.support.v4.util.Pair;

import com.story.sonder.model.Tensor;

public class Reshape extends Layer implements ILayer {
    public Reshape() {
        // TODO: get output size. Identify the dimension that needs to be inferred
    }

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
        // TODO: fill in the parameters
        return "Reshape(shape...)";
    }
}
