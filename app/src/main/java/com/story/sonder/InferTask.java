package com.story.sonder;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.util.Pair;

import com.story.sonder.model.Tensor;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class InferTask extends AsyncTask<Void, Void, Void> {
    private final ImageData imageData;
    private final Callback<Category[]> tagCallback;

    @Override
    protected Void doInBackground(Void... voids) {
        Bitmap bitmap = BitmapFactory.decodeFile(imageData.getImageDetails().getImagePath());
        Tensor input = Util.bitmapToTensor(bitmap, Constants.inputWidth, Constants.inputHeight);
        Pair<Tensor, Object> output = AppResources.model.infer(input);
        imageData.setTrainOutput(output);
        int[] indices = Util.topKIndices(output.first, Constants.topK);
        Category[] tags = new Category[indices.length + 1];
        for (int i = -1; ++i < indices.length; ) {
            tags[i] = CategoryConverter.toCategory(indices[i]);
        }
        tags[indices.length] = Category.None;
        imageData.setCategories(tags);
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if (tagCallback != null) {
            tagCallback.callback(imageData.getCategories());
        }
    }
}
