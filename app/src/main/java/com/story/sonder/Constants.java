package com.story.sonder;

import android.provider.MediaStore;

class Constants {
    static final String[] categories = {"Sunset", "Beach", "Selfie", "Portrait", "Scenery", "Friends",
            "Hills", "Meme", "Work", "Meee", "None"};
    static int height;
    static int width;
    static ImageDatabase imageDatabase;
    static int galleryColumns = 3;
    static String[] imagesProjection = {MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA};
    static String[] imagesFolder = {"%Dataset%"};
    static int inputWidth = 32;
    static int inputHeight = 32;
}
