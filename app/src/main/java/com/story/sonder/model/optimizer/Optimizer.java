package com.story.sonder.model.optimizer;

import com.story.sonder.model.Tensor;
import com.story.sonder.model.layer.ILayer;

import java.util.List;

abstract class Optimizer implements IOptimizer {
    protected final List<List<Tensor>> parameters;

    Optimizer(ILayer model) {
        parameters = model.getParameters();
    }

    @Override
    public void reset_gradients() {
        for (List<Tensor> parameter : parameters)
            parameter.get(1).updateEach((i, v) -> 0);
    }

    @Override
    public void update() {
        for (List<Tensor> parameter : parameters)
            learn(parameter.toArray(new Tensor[parameter.size()]));
    }
}
