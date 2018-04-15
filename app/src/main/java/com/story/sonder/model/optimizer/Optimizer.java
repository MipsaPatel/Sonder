package com.story.sonder.model.optimizer;

import com.story.sonder.model.Tensor;
import com.story.sonder.model.layer.ILayer;

import java.util.ArrayList;
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
    public List<double[]> getParameters() {
        List<double[]> parameterList = new ArrayList<>();
        for (List<Tensor> parameter : parameters)
            parameterList.add(parameter.get(0).getArray());
        return parameterList;
    }

    @Override
    public void setParameters(List<double[]> parameters) {
        for (int p = -1; ++p < parameters.size(); ) {
            double[] parameter = parameters.get(p);
            this.parameters.get(p).get(0).updateEach((i, v) -> parameter[i]);
        }
    }

    @Override
    public void mergeParameters(List<double[]> parameters, double alpha) {
        for (int p = -1; ++p < parameters.size(); ) {
            double[] parameter = parameters.get(p);
            this.parameters.get(p).get(0).updateEach((i, v) -> v * (alpha) + parameter[i] * (1 - alpha));
        }
    }

    @Override
    public void update() {
        for (List<Tensor> parameter : parameters)
            learn(parameter.toArray(new Tensor[parameter.size()]));
    }
}
