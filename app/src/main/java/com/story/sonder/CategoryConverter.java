package com.story.sonder;

import android.arch.persistence.room.TypeConverter;

class CategoryConverter {
    @TypeConverter
    public static int toInt(Category category) {
        return category.ordinal();
    }

    @TypeConverter
    public static Category toCategory(int index) {
        return Category.values()[index];
    }
}
