package com.story.sonder.model.loss;

import android.support.v4.util.Pair;

import com.story.sonder.model.Tensor;

public class NLLLoss implements ILoss<Integer> {
    @Override
    public Pair<Double, Object> forward(Tensor input, Integer target) {
        return Pair.create(-input.getValueAt(target), Pair.create(input.getShape(), target));
    }

    @Override
    public Tensor backward(double gradInput, Object backInput) {
        Pair input = (Pair) backInput;
        int[] inShape = (int[]) input.first;
        int target = (int) input.second;
        Tensor output = new Tensor(inShape);
        output.setValueAt(target, -gradInput);
        return output;
    }

    @Override
    public String toString() {
        return "NLLLoss()";
    }
}
