package com.story.sonder.model.optimizer;

import com.story.sonder.model.Tensor;

public interface IOptimizer {
    void reset_gradients();

    void update();

    void learn(Tensor... parameters);
}
