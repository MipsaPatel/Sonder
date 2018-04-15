package com.story.sonder.model;

import java.util.Arrays;

public class Tensor {
    private final int[] shape;
    private final int size;
    private final double[] array;

    public Tensor(double[] value, int... shape) {
        this.shape = checkShape(shape);
        size = countElements(shape);
        if (value.length != size)
            throw new IllegalArgumentException("the array should have same number of elements specified by shape");
        array = value;
    }

    public Tensor(int... shape) {
        this.shape = checkShape(shape);
        size = countElements(shape);
        array = new double[size];
    }

    private int[] checkShape(int[] shape) {
        if (shape == null || shape.length == 0)
            return new int[]{1};
        for (int s : shape)
            if (s <= 0)
                throw new IllegalArgumentException("shape should be positive");
        return Arrays.copyOf(shape, shape.length);
    }

    private int countElements(int[] shape) {
        int count = 1;
        for (int s : shape)
            count *= s;
        return count;
    }

    public double[] getArray() {
        return array;
    }

    public int[] getShape() {
        return shape;
    }

    public int getSize() {
        return size;
    }

    public Tensor copy() {
        return new Tensor(Arrays.copyOf(array, size), shape);
    }

    public boolean sameShape(Tensor tensor) {
        return size == tensor.size && Arrays.equals(shape, tensor.shape);
    }

    public double getValueAt(int index) {
        return array[index];
    }

    public void setValueAt(int index, double value) {
        array[index] = value;
    }

    public Tensor reshape(int... s) {
        s = checkShape(s);
        int count = countElements(s);
        if (count != size)
            throw new IllegalArgumentException("Cannot convert array of shape " +
                    Arrays.toString(shape) + " to shape " + Arrays.toString(s));
        return new Tensor(array, s);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof Tensor) {
            Tensor tensor = (Tensor) obj;
            return sameShape(tensor) && Arrays.equals(array, tensor.array);
        }
        return false;
    }

    public Tensor updateEach(Apply apply) {
        for (int i = -1; ++i < size; )
            array[i] = apply.apply(i, array[i]);
        return this;
    }

    public Tensor updateIn3D(Apply3D apply) {
        if (shape.length < 3)
            throw new IllegalArgumentException("Expected tensor with rank of at least 3, got " + shape.length);
        int y = shape[shape.length - 2];
        int z = shape[shape.length - 1];
        int x = size / (y * z);
        int c = 0;
        for (int i = -1; ++i < x; )
            for (int j = -1; ++j < y; )
                for (int k = -1; ++k < z; c++)
                    array[c] = apply.apply(i, j, k, array[c]);
        return this;
    }

    public Tensor forEach(Apply apply) {
        for (int i = -1; ++i < size; )
            apply.apply(i, array[i]);
        return this;
    }

    public Tensor forEach3D(Apply3D apply) {
        if (shape.length < 3)
            throw new IllegalArgumentException("Expected tensor with rank of at least 3, got " + shape.length);
        int y = shape[shape.length - 2];
        int z = shape[shape.length - 1];
        int x = size / (y * z);
        int c = 0;
        for (int i = -1; ++i < x; )
            for (int j = -1; ++j < y; )
                for (int k = -1; ++k < z; c++)
                    apply.apply(i, j, k, array[c]);
        return this;
    }

    public double reduce(Aggregate aggregate, Double initial) {
        double start;
        if (initial != null)
            start = aggregate.aggregate(initial, array[0]);
        else
            start = array[0];
        for (int i = 0; ++i < size; )
            start = aggregate.aggregate(start, array[i]);
        return start;
    }

    public double mapReduce(Aggregate aggregate, Apply apply, double initial) {
        for (int i = -1; ++i < size; )
            initial = aggregate.aggregate(initial, apply.apply(i, array[i]));
        return initial;
    }

    public interface Apply {
        double apply(int i, double v);
    }

    public interface Apply3D {
        double apply(int i, int j, int k, double v);
    }

    public interface Aggregate {
        double aggregate(double prev, double next);
    }
}
