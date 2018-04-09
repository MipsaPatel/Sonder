package com.story.sonder.model;

public class Tensor {
    public static Tensor normal(double mean, double std, int... shape) {
        // TODO: create a tensor of given shape. Set the elements from a random normal distribution
        // (use Random.nextGaussian())
        return new Tensor();
    }

    public static Tensor zeros(int... shape) {
        // TODO: create a tensor of given shape with all elements 0
        return new Tensor();
    }

    public void fill(double value) {
        // TODO: Set all elements of tensor to value
    }
}
