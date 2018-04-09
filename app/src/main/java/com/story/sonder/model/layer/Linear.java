package com.story.sonder.model.layer;

import android.util.Pair;

import com.story.sonder.model.Tensor;

import java.util.List;

public class Linear extends Layer implements ILayer {
    public Linear() {
        // TODO: get in_channels and out_channels
    }

    @Override
    public List<List<Tensor>> getParameters() {
        // TODO: return list of parameters
        return super.getParameters();
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
        return "Linear(in=, out=)";
    }
}
