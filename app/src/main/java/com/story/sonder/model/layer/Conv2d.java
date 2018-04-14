package com.story.sonder.model.layer;

import android.support.v4.util.Pair;

import com.story.sonder.model.Tensor;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Conv2d extends Layer implements ILayer {
    private final int in;
    private final int out;
    private final int[] kernel;
    private final int kernelParamCount;
    private final Tensor weights;
    private final Tensor bias;
    private final Tensor gWeights;
    private final Tensor gBias;

    public Conv2d(int in_channels, int out_channels, int kernelWidth, int kernelHeight) {
        in = in_channels;
        out = out_channels;
        kernel = new int[]{kernelWidth, kernelHeight};
        kernelParamCount = in * kernel[0] * kernel[1];
        final double std = Math.sqrt(2. / kernelParamCount);
        final Random random = new Random();
        weights = new Tensor(out, in, kernel[0], kernel[1])
                .updateEach((i, v) -> random.nextGaussian() * std);
        gWeights = new Tensor(out, in, kernel[0], kernel[1]);
        bias = new Tensor(out);
        gBias = new Tensor(out);
    }

    public Conv2d(int in_channels, int out_channels, int kernelSize) {
        this(in_channels, out_channels, kernelSize, kernelSize);
    }

    @Override
    public List<List<Tensor>> getParameters() {
        return Arrays.asList(
                Arrays.asList(weights, gWeights),
                Arrays.asList(bias, gBias)
        );
    }

    @Override
    public Pair<Tensor, Object> forward(Tensor input) {
        int[] shape = input.getShape();
        int rank = shape.length;
        int p = shape[rank - 2];
        int q = shape[rank - 1];
        int matrixSize = p * q;
        int inputChannels = input.getSize() / matrixSize;
        if (inputChannels != in)
            throw new IllegalArgumentException("Expected " + in + " input channels, got " + inputChannels);
        Tensor output = new Tensor(
                out,
                shape[rank - 2] - kernel[0] + 1,
                shape[rank - 1] - kernel[1] + 1
        ).updateIn3D((i, j, k, v) -> {
                    double value = bias.getValueAt(i);
                    int iw = i * kernelParamCount;
                    int ix = j * q + k;
                    for (int x = -1; ++x < in; ix += matrixSize) {
                        int iy = ix;
                        for (int y = -1; ++y < kernel[0]; iy += q) {
                            int iz = iy;
                            for (int z = -1; ++z < kernel[1]; iz++)
                                value += weights.getValueAt(iw++) * input.getValueAt(iz);
                        }
                    }
                    return value;
                }
        );
        return Pair.create(output, input);
    }

    @Override
    public Tensor backward(Tensor gradInput, Object backInput) {
        Tensor input = (Tensor) backInput;
        int[] shape = input.getShape();
        int rank = shape.length;
        int p = shape[rank - 2];
        int q = shape[rank - 1];
        int matrixSize = p * q;

        Tensor output = new Tensor(shape);
        gradInput.forEach3D((i, j, k, gIn) -> {
                    gBias.setValueAt(i, gBias.getValueAt(i) + gIn);

                    int iw = i * kernelParamCount;
                    int ix = j * q + k;
                    for (int x = -1; ++x < in; ix += matrixSize) {
                        int iy = ix;
                        for (int y = -1; ++y < kernel[0]; iy += q) {
                            int iz = iy;
                            for (int z = -1; ++z < kernel[1]; iw++, iz++) {
                                gWeights.setValueAt(iw, gWeights.getValueAt(iw) + gIn * input.getValueAt(iz));
                                output.setValueAt(iz, output.getValueAt(iz) + gIn * weights.getValueAt(iw));
                            }
                        }
                    }
                    return 0;
                }
        );
        return output;
    }

    @Override
    public String toString() {
        return "Convolution2D(in=" + in + ", out=" + out + ", kernel=" +
                kernel[0] + "x" + kernel[1] + ")";
    }
}
