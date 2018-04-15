package com.story.sonder;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity
public class ImageDetails {
    @PrimaryKey
    @NonNull
    private String imagePath;

    private long imageId;

    private String imageTag;

    ImageDetails(@NonNull String imagePath, long imageId, String imageTag) {
        this.imagePath = imagePath;
        this.imageTag = imageTag;
        this.imageId = imageId;
    }

    long getImageId() {
        return imageId;
    }

    public void setImageId(long imageId) {
        this.imageId = imageId;
    }

    @NonNull
    String getImagePath() {
        return imagePath;
    }

    void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    String getImageTag() {
        return imageTag;
    }

    void setImageTag(String imageTag) {
        this.imageTag = imageTag;
    }
}
