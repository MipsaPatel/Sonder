package com.story.sonder;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.util.Pair;

import com.story.sonder.model.Tensor;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
class ImageData {
    private final ImageDetails imageDetails;
    private Pair<Tensor, Object> trainOutput;
    private Category[] categories;
    private Category output = Category.None;

    public void train() {
        if (output != Category.None) {
            int out = CategoryConverter.toInt(output);
            AsyncTask.execute(() -> AppResources.model.train(trainOutput, out));
        }
    }

    public void updateAdapter(int position) {
        output = position >= categories.length ? Category.None : categories[position];
        if (imageDetails.getImageTag() != output) {
            GalleryAdapter oldAdapter = AppResources.adapter.get(imageDetails.getImageTag());
            GalleryAdapter newAdapter = AppResources.adapter.get(output);
            Handler handler = new Handler(Looper.getMainLooper());

            int oldIndex = oldAdapter.removeImage(imageDetails);
            handler.post(() -> oldAdapter.notifyItemRemoved(oldIndex));

            imageDetails.setImageTag(output);
            int newIndex = newAdapter.addImage(imageDetails);
            handler.post(() -> newAdapter.notifyItemInserted(newIndex));
            AsyncTask.execute(() -> AppResources.imDB.imageDao().update(imageDetails));
        }
    }
}
