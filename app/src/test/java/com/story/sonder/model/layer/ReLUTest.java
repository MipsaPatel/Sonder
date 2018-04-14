package com.story.sonder.model.layer;

import android.support.v4.util.Pair;

import com.story.sonder.model.Tensor;

import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ReLUTest {
    private static ReLU reLU;

    @BeforeClass
    public static void setUp() {
        reLU = new ReLU();
    }

    @Test
    public void forward() {
        Tensor tensor = new Tensor(new double[]{-1, 3, 2, -2}, 2, 2);
        Pair<Tensor, Object> out = reLU.forward(tensor);
        assertEquals("ReLU output", new Tensor(new double[]{0, 3, 2, 0}, 2, 2), out.first);
        assertEquals("Input is returned", tensor, out.second);
    }

    @Test
    public void backward() {
        Tensor tensor = new Tensor(new double[]{-1, 3, 2, -2}, 2, 2);
        Tensor gradInput = new Tensor(new double[]{2, 2, 2, 2}, 2, 2);
        Tensor gradOut = reLU.backward(gradInput, reLU.forward(tensor).second);
        assertEquals("Gradient", new Tensor(new double[]{0, 2, 2, 0}, 2, 2), gradOut);
    }

    @Test
    public void string() {
        assertEquals("String conversion", "ReLU()", reLU.toString());
    }
}