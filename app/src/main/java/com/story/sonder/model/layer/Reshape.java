package com.story.sonder.model.layer;

import android.support.v4.util.Pair;

import com.story.sonder.model.Tensor;

import java.util.Arrays;

public class Reshape extends Layer implements ILayer {
    private final int[] shape;
    private final String shapeStr;
    private int infer_index;
    private boolean infer_size;
    private int element_count;

    public Reshape(int... shape) {
        this.shape = shape;
        element_count = 1;
        infer_size = false;
        infer_index = -1;
        initialize();
        String str = Arrays.toString(shape);
        shapeStr = str.substring(1, str.length() - 1);
    }

    @Override
    public Pair<Tensor, Object> forward(Tensor input) {
        int count = input.getSize();
        int[] s = Arrays.copyOf(shape, shape.length);
        if (infer_size) {
            if (count % element_count != 0)
                throw new IllegalArgumentException("cannot convert array of shape " +
                        Arrays.toString(input.getShape()) + " to shape [" + shapeStr + "]");
            s[infer_index] = count / element_count;
        }
        return Pair.create(input.reshape(s), input.getShape());
    }

    @Override
    public Tensor backward(Tensor gradInput, Object backInput) {
        int[] inShape = (int[]) backInput;
        return gradInput.reshape(inShape);
    }

    @Override
    public String toString() {
        return "Reshape(" + shapeStr + ")";
    }

    private void initialize() {
        for (int i = -1; ++i < shape.length; ) {
            if (shape[i] == -1) {
                if (infer_size)
                    throw new IllegalArgumentException("cannot infer length of more than one axes.");
                infer_size = true;
                infer_index = i;
            } else if (shape[i] <= 0)
                throw new IllegalArgumentException("shape should be positive");
            else
                element_count *= shape[i];
        }
    }
}
