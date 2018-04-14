package com.story.sonder.model.layer;

import android.support.v4.util.Pair;

import com.story.sonder.model.Tensor;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class ReshapeTest {
    @Test
    public void forward() {
        double[] array = new double[]{0, 1, 2, 3, 4, 5};
        Tensor tensor = new Tensor(array, 2, 3);
        Reshape reshape = new Reshape(3, 2);
        Pair<Tensor, Object> out = reshape.forward(tensor);
        Tensor expected = new Tensor(array, 3, 2);

        assertEquals("Reshape output", expected, out.first);
        assertArrayEquals("Returned input shape", new int[]{2, 3}, (int[]) out.second);
    }

    @Test
    public void backward() {
        double[] array = new double[]{2, 2, 2, 2, 2, 2};
        Tensor gradInput = new Tensor(array, 3, 2);
        Tensor expected = new Tensor(array, 2, 3);
        Reshape reshape = new Reshape(3, 2);
        Object backInput = reshape.forward(new Tensor(2, 3)).second;
        Tensor gradOut = reshape.backward(gradInput, backInput);
        assertEquals("Gradient", expected, gradOut);
    }

    @Test
    public void string() {
        Reshape reshape = new Reshape(3, 2);
        assertEquals("String conversion", "Reshape(3, 2)", reshape.toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void multipleInfer() {
        new Reshape(2, -1, 2, -1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void wrongShape() {
        new Reshape(2, -1, 2, -2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void wrongInputShape() {
        Reshape reshape = new Reshape(2, -1, 2);
        reshape.forward(new Tensor(5));
    }

    @Test
    public void inferForward() {
        Reshape reshape = new Reshape(2, -1, 2);
        Pair<Tensor, Object> out = reshape.forward(new Tensor(8));
        assertArrayEquals("Output shape", new int[]{2, 2, 2}, out.first.getShape());
        assertArrayEquals("Input shape", new int[]{8}, (int[]) out.second);
    }

}