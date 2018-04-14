package com.story.sonder.model.layer;

import android.support.v4.util.Pair;

import com.story.sonder.model.Tensor;

import java.util.List;

public class Sequence extends Layer implements ILayer {
    public Sequence() {
        // TODO: get all layers
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
    public List<List<Tensor>> getParameters() {
        // TODO: return list of parameters from all layers
        return super.getParameters();
    }

    @Override
    public String toString() {
        // TODO: fill in the parameters
        return "Sequence(layers...)";
    }
}
