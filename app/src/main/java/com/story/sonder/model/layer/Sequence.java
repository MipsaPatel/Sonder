package com.story.sonder.model.layer;

import android.support.v4.util.Pair;

import com.story.sonder.model.Tensor;

import java.util.ArrayList;
import java.util.List;

public class Sequence extends Layer implements ILayer {
    private final ILayer[] layers;
    private String string;

    public Sequence(ILayer... layers) {
        this.layers = layers;
    }

    @Override
    public Pair<Tensor, Object> forward(Tensor input) {
        List<Object> backInputs = new ArrayList<>();
        for (ILayer layer : layers) {
            Pair<Tensor, Object> out = layer.forward(input);
            input = out.first;
            backInputs.add(out.second);
        }
        return Pair.create(input, backInputs);
    }

    @Override
    public Tensor backward(Tensor gradInput, Object backInput) {
        List backInputs = (List) backInput;
        for (int i = layers.length; i-- > 0; )
            gradInput = layers[i].backward(gradInput, backInputs.get(i));
        return gradInput;
    }

    @Override
    public List<List<Tensor>> getParameters() {
        List<List<Tensor>> parameters = new ArrayList<>();
        for (ILayer layer : layers)
            parameters.addAll(layer.getParameters());
        return parameters;
    }

    @Override
    public String toString() {
        if (string == null) {
            if (layers == null || layers.length == 0)
                return "Sequence()";
            StringBuilder str = new StringBuilder("Sequence(");
            boolean first = true;
            for (ILayer layer : layers) {
                str.append(first ? "\n\t" : ",\n\t").append(layer.toString().replaceAll("\n", "\n\t"));
                first = false;
            }
            str.append("\n)");
            string = str.toString();
        }
        return string;
    }
}
