package com.story.sonder.model.optimizer;

import com.story.sonder.model.Tensor;
import com.story.sonder.model.layer.ILayer;

public class SGD extends Optimizer implements IOptimizer {
    private final double learningRate;

    public SGD(ILayer model, double learningRate) {
        super(model);
        this.learningRate = learningRate;
    }

    @Override
    public void learn(Tensor... parameters) {
        Tensor g = parameters[1];
        parameters[0].updateEach((i, v) -> v - learningRate * g.getValueAt(i));
    }

    @Override
    public String toString() {
        return "SGD(learning_rate=" + learningRate + ")";
    }
}
