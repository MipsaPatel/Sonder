package com.story.sonder.model.layer;

import android.support.v4.util.Pair;

import com.story.sonder.model.Tensor;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public class LogSoftMaxTest {
    private static LogSoftMax logSoftMax;

    @BeforeClass
    public static void setUp() {
        logSoftMax = new LogSoftMax();
    }

    private double prepareAndSum(double[] array) {
        double max = array[0], sum = 0;
        for (double v : array)
            max = Math.max(max, v);
        for (int i = -1; ++i < array.length; ) {
            array[i] -= max;
            sum += Math.exp(array[i]);
        }
        return sum;
    }

    private double[] logSoftMax(double[] array) {
        double sum = Math.log(prepareAndSum(array));
        for (int i = -1; ++i < array.length; )
            array[i] -= sum;
        return array;
    }

    private double[] oneMinusSoftMax(double[] array) {
        double sum = prepareAndSum(array);
        for (int i = -1; ++i < array.length; )
            array[i] = 1 - Math.exp(array[i]) / sum;
        return array;
    }

    @Test
    public void forward() {
        double[] array = new double[]{1, -1, 2, 3};
        Tensor tensor = new Tensor(array, 4);
        Pair<Tensor, Object> out = logSoftMax.forward(tensor);
        Tensor expected = new Tensor(logSoftMax(Arrays.copyOf(array, 4)), 4);
        assertEquals("LogSoftMax output", expected, out.first);
        assertSame("Both are same", out.first, out.second);
    }

    @Test
    public void backward() {
        double[] array = new double[]{1, -1, 2, 3};
        double[] outArray = oneMinusSoftMax(Arrays.copyOf(array, 4));
        double[] gradIn = new double[]{2, 2, 2, 2};
        for (int i = -1; ++i < outArray.length; )
            outArray[i] *= gradIn[i];
        Tensor tensor = new Tensor(array, 4);
        Tensor expected = new Tensor(outArray, 4);
        Object backInput = logSoftMax.forward(tensor).second;
        Tensor gradOut = logSoftMax.backward(new Tensor(gradIn, 4), backInput);
        assertEquals("Gradient", expected, gradOut);
    }

    @Test
    public void string() {
        assertEquals("String conversion", "LogSoftMax()", logSoftMax.toString());
    }
}