package com.story.sonder.model.layer;

import android.support.v4.util.Pair;

import com.story.sonder.model.Tensor;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public class Conv2dTest {
    private static Conv2d conv2d;

    @BeforeClass
    public static void setUp() {
        conv2d = new Conv2d(2, 4, 3);
        conv2d.getParameters().get(0).get(0).updateEach((i, v) -> i + 1);
        conv2d.getParameters().get(1).get(0).updateEach((i, v) -> i + 1);
    }

    @Test
    public void getParameters() {
        List<List<Tensor>> parameters = Arrays.asList(
                Arrays.asList(new Tensor(4, 2, 3, 3).updateEach((i, v) -> i + 1), new Tensor(4, 2, 3, 3)),
                Arrays.asList(new Tensor(4).updateEach((i, v) -> i + 1), new Tensor(4))
        );
        assertEquals("Parameters", parameters, conv2d.getParameters());
    }

    @Test
    public void forward() {
        Tensor input = new Tensor(2, 5, 5).updateEach((i, v) -> i + 1);
        Pair<Tensor, Object> output = conv2d.forward(input);

        final double[] array = new double[]{
                4540, 4711, 4882,
                5395, 5566, 5737,
                6250, 6421, 6592,

                10859, 11354, 11849,
                13334, 13829, 14324,
                15809, 16304, 16799,

                17178, 17997, 18816,
                21273, 22092, 22911,
                25368, 26187, 27006,

                23497, 24640, 25783,
                29212, 30355, 31498,
                34927, 36070, 37213
        };
        Tensor expected = new Tensor(array, 4, 3, 3);

        assertSame("Input is returned", input, output.second);
        assertEquals("Output of convolution", expected, output.first);
    }

    @Test
    public void backward() {
        Tensor input = new Tensor(2, 5, 5).updateEach((i, v) -> i + 1);
        double[] array = new double[]{
//          [
                411, 456, 501,
                636, 681, 726,
                861, 906, 951,

                1536, 1581, 1626,
                1761, 1806, 1851,
                1986, 2031, 2076,
//          ],
//          [
                978, 1104, 1230,
                1608, 1734, 1860,
                2238, 2364, 2490,

                4128, 4254, 4380,
                4758, 4884, 5010,
                5388, 5514, 5640,
//          ],
//          [
                1545, 1752, 1959,
                2580, 2787, 2994,
                3615, 3822, 4029,

                6720, 6927, 7134,
                7755, 7962, 8169,
                8790, 8997, 9204,
//          ],
//          [
                2112, 2400, 2688,
                3552, 3840, 4128,
                4992, 5280, 5568,

                9312, 9600, 9888,
                10752, 11040, 11328,
                12192, 12480, 12768
//          ]
        };
        double[] bias = new double[]{45, 126, 207, 288};
        double[] grad = new double[]{
                2434, 5038, 7816, 5394, 2790,
                5378, 11120, 17234, 11880, 6138,
                8868, 18318, 28362, 19530, 10080,
                6542, 13496, 20870, 14352, 7398,
                3598, 7414, 11452, 7866, 4050,

                2956, 6118, 9490, 6546, 3384,
                6530, 13496, 20906, 14400, 7434,
                10758, 22206, 34356, 23634, 12186,
                7910, 16304, 25190, 17304, 8910,
                4336, 8926, 13774, 9450, 4860
        };

        Tensor gW = new Tensor(array, 4, 2, 3, 3);
        Tensor gB = new Tensor(bias, 4);
        Tensor gOut = new Tensor(grad, 2, 5, 5);

        Object backInput = conv2d.forward(input).second;
        Tensor gradOut = conv2d.backward(new Tensor(4, 3, 3).updateEach((i, v) -> i + 1), backInput);

        assertEquals("Gradient", gOut, gradOut);
        assertEquals("Weight gradient", conv2d.getParameters().get(0).get(1), gW);
        assertEquals("Bias gradient", conv2d.getParameters().get(1).get(1), gB);
    }

    @Test
    public void string() {
        assertEquals("String conversion",
                "Convolution2D(in=2, out=4, kernel=3x3)",
                conv2d.toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void wrongInput() {
        conv2d.forward(new Tensor(3, 2, 2));
    }
}