package com.story.sonder.model.layer;

import android.support.v4.util.Pair;

import com.story.sonder.model.Tensor;

import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public class SigmoidTest {
    private static Sigmoid sigmoid;


    @BeforeClass
    public static void setUp() {
        sigmoid = new Sigmoid();
    }

    private double sigmoid(double v) {
        return 1 / (1 + Math.exp(-v));
    }

    private double xTimesOneMinusX(double x) {
        return x * (1 - x);
    }

    @Test
    public void forward() {
        Tensor tensor = new Tensor(new double[]{1, -1, 2, -2}, 2, 2);
        Pair<Tensor, Object> out = sigmoid.forward(tensor);
        Tensor expected = new Tensor(new double[]{sigmoid(1), sigmoid(-1),
                sigmoid(2), sigmoid(-2)}, 2, 2);
        assertEquals("Sigmoid output", expected, out.first);
        assertSame("Output is returned", out.first, out.second);
    }

    @Test
    public void backward() {
        Tensor tensor = new Tensor(new double[]{1, -1, 2, -2}, 2, 2);
        Tensor gradInput = new Tensor(new double[]{2, 2, 2, 2}, 2, 2);
        Tensor expected = new Tensor(new double[]{
                2 * xTimesOneMinusX(sigmoid(1)),
                2 * xTimesOneMinusX(sigmoid(-1)),
                2 * xTimesOneMinusX(sigmoid(2)),
                2 * xTimesOneMinusX(sigmoid(-2))
        }, 2, 2);

        Object backInput = sigmoid.forward(tensor).second;
        Tensor gradOut = sigmoid.backward(gradInput, backInput);
        assertEquals("Gradient", expected, gradOut);
    }

    @Test
    public void string() {
        assertEquals("String conversion", "Sigmoid()", sigmoid.toString());
    }
}