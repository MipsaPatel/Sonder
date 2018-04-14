package com.story.sonder.model.layer;

import android.support.v4.util.Pair;

import com.story.sonder.model.Tensor;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public class LinearTest {
    private static Linear linear;
    private static int[] weightShape;
    private static int[] biasShape;

    @BeforeClass
    public static void setUp() {
        linear = new Linear(2, 3);
        linear.getParameters().get(0).get(0).updateEach((i, v) -> i);
        linear.getParameters().get(1).get(0).updateEach((i, v) -> 1);
        weightShape = new int[]{3, 2};
        biasShape = new int[]{3};
    }

    @Test
    public void getParameters() {
        List<List<Tensor>> parameters = linear.getParameters();
        assertEquals("Number of parameters", 2, parameters.size());
        assertEquals("Weight tensors", 2, parameters.get(0).size());
        assertArrayEquals("Weight Shape", weightShape, parameters.get(0).get(0).getShape());
        assertArrayEquals("Weight gradient Shape", weightShape, parameters.get(0).get(1).getShape());
        assertEquals("Bias tensors", 2, parameters.get(1).size());
        assertArrayEquals("Bias Shape", biasShape, parameters.get(1).get(0).getShape());
        assertArrayEquals("Bias gradient Shape", biasShape, parameters.get(1).get(1).getShape());
    }

    @Test
    public void forward() {
        Tensor input = new Tensor(new double[]{1, 2}, 2);
        Tensor expected = new Tensor(new double[]{3, 9, 15}, biasShape);

        Pair<Tensor, Object> output = linear.forward(input);
        Tensor out = output.first, in = (Tensor) output.second;
        assertEquals("Output", expected, out);
        assertSame("Input to back-prop", input, in);
    }

    @Test
    public void backward() {
        Tensor input = new Tensor(new double[]{1, 2}, 2);
        Tensor grad = new Tensor(new double[]{1, 4, 9}, 3);
        Tensor wGrad = new Tensor(new double[]{1, 2, 4, 8, 9, 18}, 3, 2);
        Tensor expectedGradOut = new Tensor(new double[]{44, 58}, 2);

        Object back = linear.forward(input).second;
        Tensor gradOut = linear.backward(grad, back);
        assertEquals("Gradient", expectedGradOut, gradOut);
        assertEquals("Bias gradient", grad, linear.getParameters().get(1).get(1));
        assertEquals("Weight gradient", wGrad, linear.getParameters().get(0).get(1));
    }

    @Test
    public void string() {
        assertEquals("String conversion", "Linear(in=2, out=3)", linear.toString());
    }
}