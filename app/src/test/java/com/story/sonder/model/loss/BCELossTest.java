package com.story.sonder.model.loss;

import android.support.v4.util.Pair;

import com.story.sonder.model.Tensor;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BCELossTest {
    private static BCELoss bceLoss = new BCELoss();

    @Test
    public void forward() {
        double in = 0.1;
        int target = 0;
        Pair<Double, Object> out = bceLoss.forward(new Tensor(new double[]{in}), target);
        assertEquals("BCELoss output", bce(in, target), out.first, 1e-8);
        assertEquals("Back input", Pair.create(in, target), out.second);
    }

    @Test
    public void backward() {
        double in = 0.1;
        int target = 0;
        Object backIn = bceLoss.forward(new Tensor(new double[]{in}), target).second;
        Tensor gradOut = bceLoss.backward(2, backIn);
        assertEquals("Gradient", new Tensor(new double[]{bceGrad(in, target)}), gradOut);
    }

    @Test
    public void string() {
        assertEquals("String conversion", "BCELoss()", bceLoss.toString());
    }

    private double bce(double x, int y) {
        return -y * Math.log(x) - (1 - y) * Math.log(1 - x);
    }

    private double bceGrad(double x, int y) {
        return (x - y) / (x * (1 - x) + 1e-8);
    }
}