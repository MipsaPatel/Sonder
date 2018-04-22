package com.story.sonder;

import android.os.AsyncTask;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

@AllArgsConstructor
@RequiredArgsConstructor
class LoadImagesFromDB extends AsyncTask<Void, Void, Void> {
    private final Callback<ImageDetails> addImage;
    private Callback<Void> onPostExecute;

    @Override
    protected Void doInBackground(Void... voids) {
        int lastPageSize = Constants.pageSize;
        int offset = 0;
        while (lastPageSize == Constants.pageSize) {
            List<ImageDetails> images = AppResources.imDB.imageDao()
                    .getNextPage(Constants.pageSize, offset);
            for (ImageDetails image : images) {
                addImage.callback(image);
            }
            lastPageSize = images.size();
            offset += lastPageSize;
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if (onPostExecute != null) {
            onPostExecute.callback(null);
        }
    }
}
