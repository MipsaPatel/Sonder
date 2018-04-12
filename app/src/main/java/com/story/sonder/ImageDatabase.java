package com.story.sonder;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

@Database(entities = {ImageDetails.class}, version = 1)
public abstract class ImageDatabase extends RoomDatabase {
    public abstract ImageDao imageDao();
}
