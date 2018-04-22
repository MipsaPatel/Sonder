package com.story.sonder.model;

import android.support.v4.util.Pair;

import com.story.sonder.model.layer.Conv2d;
import com.story.sonder.model.layer.ILayer;
import com.story.sonder.model.layer.Linear;
import com.story.sonder.model.layer.LogSoftMax;
import com.story.sonder.model.layer.MaxPool2d;
import com.story.sonder.model.layer.ReLU;
import com.story.sonder.model.layer.Reshape;
import com.story.sonder.model.layer.Sequence;
import com.story.sonder.model.layer.Sigmoid;
import com.story.sonder.model.loss.BCELoss;
import com.story.sonder.model.loss.ILoss;
import com.story.sonder.model.loss.NLLLoss;
import com.story.sonder.model.optimizer.Adam;
import com.story.sonder.model.optimizer.IOptimizer;
import com.story.sonder.model.optimizer.SGD;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public final class ModelUtils {
    private static Conv2d convolution(JSONObject parameters) throws JSONException {
        int in_channels = parameters.getInt("in_channels");
        int out_channels = parameters.getInt("out_channels");
        Object kernel = parameters.get("kernel_size");
        if (kernel instanceof JSONArray) {
            JSONArray kernelSize = (JSONArray) kernel;
            return new Conv2d(in_channels, out_channels, kernelSize.getInt(0), kernelSize.getInt(1));
        }
        return new Conv2d(in_channels, out_channels, parameters.getInt("kernel_size"));
    }

    private static Linear linear(JSONObject parameters) throws JSONException {
        return new Linear(parameters.getInt("in_features"), parameters.getInt("out_features"));
    }

    private static LogSoftMax logSoftMax() {
        return new LogSoftMax();
    }

    private static MaxPool2d maxPool(JSONObject parameters) throws JSONException {
        Object kernel = parameters.get("kernel_size");
        if (kernel instanceof JSONArray) {
            JSONArray kernelSize = (JSONArray) kernel;
            return new MaxPool2d(kernelSize.getInt(0), kernelSize.getInt(1));
        }
        return new MaxPool2d(parameters.getInt("kernel_size"));
    }

    private static ReLU reLU() {
        return new ReLU();
    }

    public static Reshape reshape(JSONArray parameter) throws JSONException {
        int[] shape = new int[parameter.length()];
        for (int i = -1; ++i < shape.length; )
            shape[i] = parameter.getInt(i);
        return new Reshape(shape);
    }

    private static ILayer reshape(JSONObject parameters) throws JSONException {
        return reshape(parameters.getJSONArray("shape"));
    }

    private static Sequence sequence(JSONObject parameters) throws JSONException {
        int numLayers = parameters.getInt("num_layers");
        JSONArray layerArray = parameters.getJSONArray("layers");
        ILayer[] layers = new ILayer[numLayers];
        for (int i = -1; ++i < numLayers; ) {
            JSONObject layer = layerArray.getJSONObject(i);
            layers[i] = getLayer(layer.getString("name"), layer.getJSONObject("parameters"));
        }
        return new Sequence(layers);
    }

    private static Sigmoid sigmoid() {
        return new Sigmoid();
    }

    public static ILayer getLayer(String name, JSONObject parameters) throws JSONException {
        switch (name) {
            case "Convolution":
                return convolution(parameters);
            case "Linear":
                return linear(parameters);
            case "LogSoftMax":
                return logSoftMax();
            case "MaxPool":
                return maxPool(parameters);
            case "ReLU":
                return reLU();
            case "Reshape":
                return reshape(parameters);
            case "Sequence":
                return sequence(parameters);
            case "Sigmoid":
                return sigmoid();
        }
        throw new IllegalArgumentException("invalid name " + name);
    }

    private static BCELoss bceLoss() {
        return new BCELoss();
    }

    private static NLLLoss nllLoss() {
        return new NLLLoss();
    }

    public static ILoss getLoss(String name) {
        switch (name) {
            case "BCELoss":
                return bceLoss();
            case "NLLLoss":
                return nllLoss();
        }
        throw new IllegalArgumentException("invalid name " + name);
    }

    private static Adam adam(ILayer model, JSONObject parameters) throws JSONException {
        if (!parameters.has("lr"))
            return new Adam(model);
        double learningRate = parameters.getDouble("lr");
        if (!parameters.has("eps"))
            return new Adam(model, learningRate);
        double delta = parameters.getDouble("eps");
        JSONArray betas = parameters.getJSONArray("betas");
        double rho1 = betas.getDouble(0);
        double rho2 = betas.getDouble(1);
        return new Adam(model, learningRate, rho1, rho2, delta);
    }

    private static SGD sgd(ILayer model, JSONObject parameters) throws JSONException {
        double learningRate = parameters.getDouble("lr");
        return new SGD(model, learningRate);
    }

    public static IOptimizer getOptimizer(String name, ILayer model, JSONObject parameters) throws JSONException {
        switch (name) {
            case "Adam":
                return adam(model, parameters);
            case "SGD":
                return sgd(model, parameters);
        }
        throw new IllegalArgumentException("invalid name " + name);
    }

    public static Pair<JSONObject, JSONArray> parseModel(JSONObject jsonModel) throws
            JSONException {
        JSONObject jsonObject = jsonModel.getJSONObject("optimizer").getJSONObject("parameters");
        JSONArray parameters = jsonObject.getJSONArray("params");
        jsonObject.remove("params");
        return Pair.create(jsonModel, parameters);
    }

    public static List<double[]> jsonParametersToArray(JSONArray parameters) throws JSONException {
        List<double[]> parameterList = new ArrayList<>();
        for (int i = -1; ++i < parameters.length(); ) {
            JSONArray jsonArray = parameters.getJSONArray(i);
            double[] array = new double[jsonArray.length()];
            for (int j = -1; ++j < array.length; ) {
                array[j] = jsonArray.getDouble(j);
            }
            parameterList.add(array);
        }
        return parameterList;
    }

    public static JSONArray parametersToJSONArray(List<double[]> parameters) throws JSONException {
        JSONArray jsonArray = new JSONArray();
        for (double[] p : parameters) {
            jsonArray.put(new JSONArray(p));
        }
        return jsonArray;
    }
}
