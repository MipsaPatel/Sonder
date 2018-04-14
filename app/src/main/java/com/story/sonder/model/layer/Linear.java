package com.story.sonder.model.layer;

import android.support.v4.util.Pair;

import com.story.sonder.model.Tensor;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Linear extends Layer implements ILayer {
    private final int in;
    private final int out;
    private final Tensor weights;
    private final Tensor bias;
    private final Tensor gWeights;
    private final Tensor gBias;

    public Linear(int in_channels, int out_channels) {
        in = in_channels;
        out = out_channels;
        final Random random = new Random();
        final double std = Math.sqrt(2. / out);
        weights = new Tensor(out, in).updateEach((i, v) -> random.nextGaussian() * std);
        gWeights = new Tensor(out, in);
        bias = new Tensor(out);
        gBias = new Tensor(out);
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
        Tensor output = new Tensor(out)
                .updateEach((i, v) ->
                        input.mapReduce(
                                (x, y) -> x + y,
                                (j, w) -> w * weights.getValueAt(i * in + j),
                                bias.getValueAt(i)
                        )
                );
        return Pair.create(output, input);
    }

    @Override
    public Tensor backward(Tensor gradInput, Object backInput) {
        Tensor input = (Tensor) backInput;
        gBias.updateEach((i, v) -> v + gradInput.getValueAt(i));
        gWeights.updateEach((i, v) -> v + input.getValueAt(i % in) * gradInput.getValueAt(i / in));
        return new Tensor(in).updateEach((i, v) ->
                gradInput.mapReduce(
                        (x, y) -> x + y,
                        (j, w) -> w * weights.getValueAt(j * in + i),
                        0
                )
        );
    }

    @Override
    public String toString() {
        return "Linear(in=" + in + ", out=" + out + ")";
    }
}
