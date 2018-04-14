package com.story.sonder.model.layer;

import android.support.v4.util.Pair;

import com.story.sonder.model.Tensor;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class LayerTest {

    @Test
    public void getParameters() {
        Layer layer = new Layer() {
            @Override
            public Pair<Tensor, Object> forward(Tensor input) {
                return null;
            }

            @Override
            public Tensor backward(Tensor gradInput, Object backInput) {
                return null;
            }
        };
        assertEquals("Number of parameters", 0, layer.getParameters().size());
    }
}