package com.story.sonder;

import android.provider.MediaStore;
import android.support.v4.util.Pair;

import com.story.sonder.model.layer.ILayer;
import com.story.sonder.model.loss.ILoss;
import com.story.sonder.model.optimizer.IOptimizer;

class Constants {
    static final String[] categories = {"Animal", "Beach", "Bird", "Fabric", "Food", "Furniture",
            "Mountains", "Instrument", "Nature", "Vehicle", "None"};
    static int height;
    static int width;
    static ImageDatabase imageDatabase = null;
    static int galleryColumns = 3;
    static String[] imagesProjection = {MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA};
    static String[] imagesFolder = {"%Dataset%"};
    static String configFile = "config.json";
    static String paramsFile = "params.json";
    static String serverUrl = "http://four-d.tech/model";
    static Pair<ILayer, Pair<ILoss, IOptimizer>> model;
    static double alpha = 0.3;
    static int inputWidth = 32;
    static int inputHeight = 32;
    static int topK = 5;
}
