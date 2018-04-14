package com.story.sonder.model.layer;

import android.support.v4.util.Pair;

import com.story.sonder.model.Tensor;

import java.util.Arrays;

public class MaxPool2d extends Layer implements ILayer {
    private final int[] kernel;

    public MaxPool2d(int... kernelSize) {
        if (kernelSize.length > 2)
            throw new IllegalArgumentException("kernel cannot have more than 2 dimensions");
        kernel = Arrays.copyOf(kernelSize, 2);
        if (kernelSize.length == 1)
            kernel[1] = kernel[0];
    }

    @Override
    public Pair<Tensor, Object> forward(Tensor input) {
        int n = input.getShape().length;
        int[] shape = Arrays.copyOf(input.getShape(), n);
        int p = shape[n - 2], q = shape[n - 1];
        shape[n - 2] /= kernel[0];
        shape[n - 1] /= kernel[1];
        Tensor index = new Tensor(shape)
                .updateIn3D((i, j, k, v) -> {
                            j *= kernel[0];
                            k *= kernel[1];
                            int max = (i * p + j) * q + k;
                            double maxValue = input.getValueAt(max);
                            int ix = max;
                            for (int x = -1; ++x < kernel[0]; ix += q) {
                                int iy = ix;
                                for (int y = -1; ++y < kernel[1]; iy++) {
                                    if (input.getValueAt(iy) > maxValue) {
                                        maxValue = input.getValueAt(iy);
                                        max = iy;
                                    }
                                }
                            }
                            return max;
                        }
                );
        Tensor output = index.copy().updateEach((i, v) -> input.getValueAt((int) v));
        return Pair.create(output, Pair.create(index, input.getShape()));
    }

    @Override
    public Tensor backward(Tensor gradInput, Object backInput) {
        Pair backInPair = (Pair) backInput;
        Tensor index = (Tensor) backInPair.first;
        Tensor output = new Tensor((int[]) backInPair.second);
        index.forEach((i, v) -> {
                    output.setValueAt((int) v, gradInput.getValueAt(i));
                    return 0;
                }
        );
        return output;
    }

    @Override
    public String toString() {
        return "MaxPool2D(kernel=" + kernel[0] + "x" + kernel[1] + ")";
    }
}
