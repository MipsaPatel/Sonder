package com.story.sonder.model.layer;

import android.support.v4.util.Pair;

import com.story.sonder.model.Tensor;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class SequenceTest {
    private static Sequence sequence;
    private static Linear linear;
    private static ReLU reLU;

    @BeforeClass
    public static void setUp() {
        linear = new Linear(2, 3);
        linear.getParameters().get(0).get(0).updateEach((i, v) -> i - 2);
        linear.getParameters().get(1).get(0).updateEach((i, v) -> -i);
        reLU = new ReLU();
        sequence = new Sequence(linear, reLU);
    }

    @Test
    public void forward() {
        Tensor input = new Tensor(new double[]{1, 2}, 2);
        Tensor linearOutput = new Tensor(new double[]{-4, 1, 6}, 3);
        Tensor output = new Tensor(new double[]{0, 1, 6}, 3);

        Pair<Tensor, Object> out = sequence.forward(input);
        List<Object> backInput = Arrays.asList(input, linearOutput);
        assertEquals("Sequence output", output, out.first);
        assertEquals("Back input", backInput, out.second);
    }

    @Test
    public void backward() {
        Tensor expected = new Tensor(new double[]{2, 4}, 2);
        Tensor gradOut = sequence.backward(new Tensor(new double[]{1, 1, 1}, 3),
                sequence.forward(new Tensor(new double[]{1, 2}, 2)).second);
        assertEquals("Gradient", expected, gradOut);
    }

    @Test
    public void getParameters() {
        assertEquals("Parameters", linear.getParameters(), sequence.getParameters());
    }

    @Test
    public void string() {
        assertEquals("String conversion",
                "Sequence(\n\t" + linear.toString() + ",\n\t" + reLU.toString() + "\n)",
                sequence.toString());
        assertEquals("String conversion empty", "Sequence()", new Sequence().toString());
    }
}