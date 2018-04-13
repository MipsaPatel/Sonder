package com.story.sonder;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface ImageDao {
    @Query("SELECT * FROM ImageDetails")
    List<ImageDetails> getAll();

    @Query("SELECT * FROM ImageDetails WHERE imagePath LIKE :image_path")
    ImageDetails getRecordFromImagePath(String image_path);

    @Query("SELECT * FROM ImageDetails WHERE imageTag LIKE :image_tag")
    List<ImageDetails> filterImages(String image_tag);

    @Insert
    void insertOneRecord(ImageDetails imageDetails);

    @Delete
    void delete(ImageDetails imageDetails);

    @Update
    void update(ImageDetails imageDetails);
}
