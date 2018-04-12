package com.story.sonder;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity
public class ImageDetails {
    @PrimaryKey
    @NonNull
    private String imagePath;

    private String imageTag;

    ImageDetails(@NonNull String imagePath, String imageTag) {
        this.imagePath = imagePath;
        this.imageTag = imageTag;
    }

    @NonNull String getImagePath() {
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
