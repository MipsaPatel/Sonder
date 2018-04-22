package com.story.sonder;

import android.provider.MediaStore;

class Constants {
    static int height;
    static int width;

    static final int galleryColumns = 3;

    static final int pageSize = 10;

    static final String[] imagesProjection = {
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.DATE_TAKEN
    };
    static final String[] imagesFolder = {"%Dataset%"};

    static final String configFile = "config.json";
    static final String paramsFile = "params.json";
    static final String serverUrl = "http://four-d.tech/model";

    static final int numClasses = 10;

    static final double[] mean = new double[]{0.498420, 0.458495, 0.397866};
    static final double[] std = new double[]{0.313047, 0.269076, 0.231632};

    static final int inputWidth = 32;
    static final int inputHeight = 32;

    static final double alpha = 0.3;

    static final int topK = 5;

    static boolean saveToFile = false;

    static final long syncLimit = 5 * 60 * 1000;
    static long lastSyncTime = 0;
    static boolean syncSuccessful = false;
}
