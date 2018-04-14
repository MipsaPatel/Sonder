package com.story.sonder.model.layer;

import android.support.v4.util.Pair;

import com.story.sonder.model.Tensor;

import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class MaxPool2dTest {
    private static MaxPool2d maxPool2d;

    @BeforeClass
    public static void setUp() {
        maxPool2d = new MaxPool2d(3);
    }

    @Test(expected = IllegalArgumentException.class)
    public void initialize() {
        new MaxPool2d(2, 1, 4);
    }

    @Test
    public void forward() {
        Tensor input = new Tensor(2, 7, 5).updateEach((i, v) -> i);
        Pair<Tensor, Object> output = maxPool2d.forward(input);
        Pair back = (Pair) output.second;
        Tensor exp = new Tensor(new double[]{12, 27, 47, 62}, 2, 2, 1);
        assertEquals("MaxPool2D output", exp, output.first);
        assertEquals("Index of MaxPool2D", exp, back.first);
        assertArrayEquals("Input shape", new int[]{2, 7, 5}, (int[]) back.second);
    }

    @Test
    public void backward() {
        Tensor input = new Tensor(2, 7, 5).updateEach((i, v) -> i);
        Object back = maxPool2d.forward(input).second;
        double[] grad = new double[]{2, 3, 4, 5};
        Tensor gradOut = maxPool2d.backward(new Tensor(grad, 2, 2, 1), back);
        int[] arr = new int[]{12, 27, 47, 62};
        Tensor exp = new Tensor(2, 7, 5);
        for (int i = -1; ++i < arr.length; )
            exp.setValueAt(arr[i], grad[i]);
        assertEquals("Gradient", exp, gradOut);
    }

    @Test
    public void string() {
        assertEquals("String conversion", "MaxPool2D(kernel=3x3)", maxPool2d.toString());
    }
}