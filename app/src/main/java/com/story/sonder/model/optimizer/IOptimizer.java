package com.story.sonder.model.optimizer;

import com.story.sonder.model.Tensor;

import java.util.List;

public interface IOptimizer {
    void reset_gradients();

    List<double[]> getParameters();

    void setParameters(List<double[]> parameters);

    void mergeParameters(List<double[]> parameters, double alpha);

    void update();

    void learn(Tensor... parameters);
}
