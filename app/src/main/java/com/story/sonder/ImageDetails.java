package com.story.sonder;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.support.annotation.NonNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
class ImageDetails implements Comparable<ImageDetails> {
    @PrimaryKey
    @NonNull
    private String imagePath;

    private long dateTaken;

    @TypeConverters(CategoryConverter.class)
    private Category imageTag;

    @Override
    public int compareTo(@NonNull ImageDetails o) {
        if (dateTaken == o.dateTaken)
            return imagePath.compareTo(o.imagePath);
        return dateTaken < o.dateTaken ? 1 : -1;
    }
}
