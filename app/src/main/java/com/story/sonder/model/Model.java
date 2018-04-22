package com.story.sonder.model;

import android.support.v4.util.Pair;

import com.story.sonder.model.layer.ILayer;
import com.story.sonder.model.loss.ILoss;
import com.story.sonder.model.optimizer.IOptimizer;

import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class Model {
    private static final Object object = new Object();
    private final ILayer layer;
    private final ILoss loss;
    private final IOptimizer optimizer;
    private final double alpha;
    private boolean updateRequired = false;

    public void mergeParameters(List<double[]> array) {
        synchronized (object) {
            optimizer.mergeParameters(array, alpha);
        }
    }

    public List<double[]> getParameters() {
        synchronized (object) {
            return optimizer.getParameters();
        }
    }

    public void update() {
        synchronized (object) {
            optimizer.update();
            optimizer.reset_gradients();
        }
    }

    public Pair<Tensor, Object> infer(Tensor input) {
        synchronized (object) {
            return layer.forward(input);
        }
    }

    public double train(Pair<Tensor, Object> output, Object target) {
        synchronized (object) {
            Pair<Double, Object> lossOutput = loss.forward(output.first, target);
            Tensor gradient = loss.backward(1., lossOutput.second);
            layer.backward(gradient, output.second);
            return lossOutput.first;
        }
    }
}
