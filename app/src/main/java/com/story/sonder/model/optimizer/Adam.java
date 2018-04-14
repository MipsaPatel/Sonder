package com.story.sonder.model.optimizer;

import com.story.sonder.model.Tensor;
import com.story.sonder.model.layer.ILayer;

import java.util.List;

public class Adam extends Optimizer implements IOptimizer {
    private final double learningRate;
    private final double rho1;
    private final double rho2;
    private final double delta;
    private double pr1;
    private double pr2;

    public Adam(ILayer model, double learningRate, double rho1, double rho2, double delta) {
        super(model);
        this.learningRate = learningRate;
        this.rho1 = rho1;
        this.rho2 = rho2;
        this.delta = delta;
        pr1 = rho1;
        pr2 = rho2;
        for (List<Tensor> parameter : parameters) {
            Tensor zeroes = new Tensor(parameter.get(0).getShape());
            parameter.add(zeroes);
            parameter.add(zeroes.copy());
        }
    }

    public Adam(ILayer model, double learningRate) {
        this(model, learningRate, 0.9, 0.999, 1e-8);
    }

    public Adam(ILayer model) {
        this(model, 0.001);
    }

    @Override
    public void learn(Tensor... parameters) {
        Tensor w = parameters[0], g = parameters[1], s = parameters[2], r = parameters[3];
        w.updateEach((i, v) -> {
                    double grad = g.getValueAt(i);
                    s.setValueAt(i, rho1 * s.getValueAt(i) * (1 - rho1) * grad);
                    r.setValueAt(i, rho2 * r.getValueAt(i) + (1 - rho2) * grad * grad);
                    return v - learningRate * s.getValueAt(i) /
                            ((1 - pr1) * (Math.sqrt(r.getValueAt(i) / (1 - pr2)) + delta));
                }
        );
    }

    @Override
    public void update() {
        super.update();
        pr1 *= rho1;
        pr2 *= rho2;
    }

    @Override
    public String toString() {
        return "Adam(learningRate=" + learningRate + ", rho1=" + rho1 + ", rho2=" + rho2 + ", delta=" + delta + ")";
    }
}
