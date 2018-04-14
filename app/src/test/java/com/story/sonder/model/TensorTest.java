package com.story.sonder.model;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class TensorTest {
    @Test
    public void create() {
        Tensor tensor = new Tensor(2, 1, 3);
        assertArrayEquals("new empty tensor", new double[6], tensor.getArray(), 1e-8);
    }

    @Test
    public void createWithValue() {
        double[] array = new double[]{1, 2, 3, 4};
        Tensor tensor = new Tensor(array, 2, 2);
        assertSame("new tensor", array, tensor.getArray());
    }

    @Test(expected = IllegalArgumentException.class)
    public void wrongSizeArray() {
        new Tensor(new double[]{1, 2}, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void wrongShape() {
        new Tensor(0, 1);
    }

    @Test
    public void defaultTensor() {
        Tensor tensor = new Tensor();
        assertArrayEquals("Shape", new int[]{1}, tensor.getShape());
    }

    @Test
    public void copyEqualsShape() {
        Tensor tensor = new Tensor(new double[]{1, 2, 3, 4, 5, 6}, 2, 3);
        Tensor copy = tensor.copy();
        assertTrue("Copy", tensor.equals(copy));
    }

    @Test
    public void notEqual() {
        assertFalse("Equals", new Tensor().equals(new Object()));
        assertFalse("Equals", new Tensor().equals(new Tensor(new double[]{1})));
    }

    @Test
    public void size() {
        assertEquals("Number of elements", 2 * 3 * 2, new Tensor(2, 3, 2).getSize());
    }

    @Test
    public void getValue() {
        double[] array = new double[]{2, 1, 3, 1};
        Tensor tensor = new Tensor(array, 2, 2);
        assertEquals("Element at 1", array[1], tensor.getValueAt(1), 1e-8);
        assertEquals("Element at 2", array[2], tensor.getValueAt(2), 1e-8);
    }

    @Test
    public void setValue() {
        Tensor tensor = new Tensor(2, 1);
        tensor.setValueAt(1, 2);
        assertEquals("Element at 1", 2, tensor.getValueAt(1), 1e-8);
    }

    @Test
    public void reshape() {
        Tensor tensor = new Tensor(2, 3, 3);
        Tensor another = tensor.reshape(3, 6);
        assertSame("Retain underlying object", tensor.getArray(), another.getArray());
        assertArrayEquals("Shape changed", new int[]{3, 6}, another.getShape());
    }

    @Test(expected = IllegalArgumentException.class)
    public void reshapeError() {
        new Tensor(3, 2).reshape(2, 2);
    }

    @Test
    public void updateEach() {
        Tensor tensor = new Tensor(2, 3).updateEach((i, v) -> i);
        assertEquals("Element at 2", 2., tensor.getValueAt(2), 1e-8);
    }

    @Test
    public void update3D() {
        Tensor tensor = new Tensor(4, 3, 4).updateIn3D((i, j, k, v) -> i + j + k);
        assertEquals("Element at [3][2][2]", 7., tensor.getValueAt((3 * 3 + 2) * 4 + 2), 1e-8);
    }

    @Test(expected = IllegalArgumentException.class)
    public void update3dError() {
        new Tensor(2).updateIn3D(((i, j, k, v) -> 0));
    }

    @Test
    public void forEach() {
        Tensor tensor = new Tensor(2, 3);
        Tensor another = tensor.forEach((i, v) -> {
                    tensor.setValueAt(i, i);
                    return 0;
                }
        );
        assertEquals("Element at 2", 2., tensor.getValueAt(2), 1e-8);
        assertSame("Returns itself", tensor, another);
    }

    @Test
    public void forEach3D() {
        Tensor tensor = new Tensor(4, 3, 4);
        Tensor another = tensor.forEach3D((i, j, k, v) -> {
                    tensor.setValueAt((i * 3 + j) * 4 + k, i + j + k);
                    return 0;
                }
        );
        assertEquals("Element at [3][2][2]", 7., tensor.getValueAt((3 * 3 + 2) * 4 + 2), 1e-8);
        assertSame("Returns itself", tensor, another);
    }

    @Test(expected = IllegalArgumentException.class)
    public void forEach3dError() {
        new Tensor(2).forEach3D(((i, j, k, v) -> 0));
    }

    @Test
    public void reduce() {
        Tensor tensor = new Tensor(2, 3).updateEach((i, v) -> i);
        assertEquals("Max", 5, tensor.reduce(Math::max, null), 1e-8);
        assertEquals("Sum", 18, tensor.reduce((x, y) -> x + y, 3.), 1e-8);
    }

    @Test
    public void mapReduce() {
        assertEquals("Sum of squares", 55, new Tensor(2, 3).mapReduce((x, y) -> x + y, (i, v) -> i * i, 0), 1e-8);
    }
}