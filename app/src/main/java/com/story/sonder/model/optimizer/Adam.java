package com.story.sonder.model.optimizer;

import com.story.sonder.model.Tensor;
import com.story.sonder.model.layer.ILayer;

public class Adam extends Optimizer implements IOptimizer {
    public Adam(ILayer model) {
        super(model);
        // TODO: set hyper parameters
    }

    @Override
    public void learn(Tensor... parameters) {
        // TODO: update parameters
    }

    @Override
    public String toString() {
        // TODO: fill in the parameters
        return "Adam(learning_rate=, rho1=, rho2=, delta=)";
    }
}
