package com.story.sonder.model.loss;

import android.support.v4.util.Pair;

import com.story.sonder.model.Tensor;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class NLLLossTest {
    private static NLLLoss nllLoss = new NLLLoss();

    @Test
    public void forward() {
        Pair<Double, Object> out = nllLoss.forward(new Tensor(new double[]{1, 2, 3}, 3), 1);
        Pair back = (Pair) out.second;
        assertEquals("NLL Loss output", -2.0, out.first, 1e-8);
        assertArrayEquals("Input shape is returned", new int[]{3}, (int[]) back.first);
        assertEquals("Target is returned", 1, back.second);
    }

    @Test
    public void backward() {
        Object back = nllLoss.forward(new Tensor(new double[]{1, 2, 3}, 3), 1).second;
        Tensor output = nllLoss.backward(2, back);
        assertEquals("Gradient", new Tensor(new double[]{0, -2, 0}, 3), output);
    }

    @Test
    public void string() {
        assertEquals("String conversion", "NLLLoss()", nllLoss.toString());
    }
}