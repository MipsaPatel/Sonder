package com.story.sonder;

import android.content.ContentResolver;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;


@AllArgsConstructor
@RequiredArgsConstructor
class ReadImagesFromMediaStore extends AsyncTask<Void, Void, List<Integer>> {
    private final ContentResolver contentResolver;
    private final Callback<ImageDetails> addImage;
    private final Callback<List<Integer>> invalidate;
    private Callback<Void> onPostExecute;

    @Override
    protected List<Integer> doInBackground(Void... voids) {
        List<Integer> invalidImages = new ArrayList<>();
        Cursor images = contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                Constants.imagesProjection,
                MediaStore.Images.Media.DATA + " like ? ", Constants.imagesFolder,
                MediaStore.Images.Media.DATE_TAKEN + " DESC"
        );

        if (images != null) {
            Set<String> validImages = new HashSet<>();
            int pathIndex = images.getColumnIndexOrThrow(Constants.imagesProjection[0]);
            int dateIndex = images.getColumnIndexOrThrow(Constants.imagesProjection[1]);

            while (images.moveToNext()) {
                String imagePath = images.getString(pathIndex);
                long date = images.getLong(dateIndex);
                ImageDetails databaseResult = AppResources.imDB.imageDao().getRecordFrom(imagePath);

                if (databaseResult == null) {
                    ImageDetails imageDetails = new ImageDetails(imagePath, date, Category.None);
                    AppResources.imDB.imageDao().insertOneRecord(imageDetails);
                    addImage.callback(imageDetails);
                    validImages.add(imageDetails.getImagePath());
                }
                else {
                    validImages.add(databaseResult.getImagePath());
                }
            }
            images.close();

            List<ImageDetails> imageDetails = AppResources.adapter.get(Category.All).getImages();
            int numImages = imageDetails.size();
            for (int i = -1; ++i < numImages; ) {
                ImageDetails image = imageDetails.get(i);
                if (!validImages.contains(image.getImagePath())) {
                    AppResources.imDB.imageDao().delete(image);
                    invalidImages.add(i);
                }
            }
        }
        return invalidImages;
    }

    @Override
    protected void onPostExecute(List<Integer> invalidImages) {
        if (invalidate != null) {
            invalidate.callback(invalidImages);
        }
        if (onPostExecute != null) {
            onPostExecute.callback(null);
        }
    }
}
