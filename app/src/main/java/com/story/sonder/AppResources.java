package com.story.sonder;

import com.story.sonder.model.Model;

import java.util.EnumMap;

class AppResources {
    static final EnumMap<Category, GalleryAdapter> adapter = new EnumMap<>(Category.class);
    static ImageDatabase imDB = null;
    static Model model = null;
    static ImageData imageInProcess = null;
}
