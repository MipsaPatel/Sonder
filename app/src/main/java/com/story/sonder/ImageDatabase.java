package com.story.sonder;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

@Database(entities = {ImageDetails.class}, version = 1, exportSchema = false)
public abstract class ImageDatabase extends RoomDatabase {
    public abstract ImageDao imageDao();
}
