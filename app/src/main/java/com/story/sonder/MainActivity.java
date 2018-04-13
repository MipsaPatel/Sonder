package com.story.sonder;

import android.app.Activity;
import android.app.Dialog;
import android.arch.persistence.room.Room;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class  MainActivity extends Activity {
    private int selectedTag = 0;

    List<ImageDetails> recyclerViewImages = new ArrayList<>();
    GalleryAdapter galleryAdapter;
    RecyclerView galleryView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        Constants.height = metrics.heightPixels;
        Constants.width = metrics.widthPixels;

        galleryView = findViewById(R.id.gallery_view);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), Constants.galleryColumns);
        galleryView.setLayoutManager(gridLayoutManager);
        galleryAdapter = new GalleryAdapter(this, recyclerViewImages, getContentResolver());
        galleryView.setAdapter(galleryAdapter);

        Constants.imageDatabase = Room.databaseBuilder(getApplicationContext(), ImageDatabase.class, "image-database").build();

        ConstraintLayout filterLayout = findViewById(R.id.constraintLayout);
        final ImageButton closeButton = findViewById(R.id.close_filter_button);
        closeButton.setOnClickListener(view -> {
            filterLayout.setVisibility(View.GONE);
            galleryView.setAdapter(galleryAdapter);
        });

        new LoadImagesFromDB().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.tag:
                showTagSelectionDialog();
                break;

            case R.id.filter:
                showFilterSelectionDialog();
                break;

            case R.id.about_us:
                Intent about_us = new Intent(this, AboutUsActivity.class);
                startActivity(about_us);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showFilterSelectionDialog() {
        final Dialog dialog = Util.createDialog(this, R.layout.filter_popup);
        ConstraintLayout filterLayout = findViewById(R.id.constraintLayout);
        final TextView filterView = findViewById(R.id.filterView);
        final GridView gridView = dialog.findViewById(R.id.filters);
        gridView.setAdapter(new FilterAdapter(getApplicationContext(), Constants.categories));
        gridView.setOnItemClickListener((adapterView, view, pos, id) -> {
            filterLayout.setVisibility(View.VISIBLE);
            filterView.setText(gridView.getItemAtPosition(pos).toString());

            AsyncTask.execute(() -> {
                    List<ImageDetails> filteredImages = Constants.imageDatabase.imageDao().filterImages(Constants.categories[pos]);
                    runOnUiThread(() -> {
                            GalleryAdapter filteredAdapter = new GalleryAdapter(getApplicationContext(), filteredImages, getContentResolver());
                            galleryView.setAdapter(filteredAdapter);
                    });
            });
            dialog.dismiss();
        });
        dialog.show();
        Objects.requireNonNull(dialog.getWindow())
                .setLayout((6 * Constants.width) / 7, (3 * Constants.height) / 5);
    }

    private void showTagSelectionDialog() {
        final Dialog dialog = Util.createDialog(this, R.layout.tag_popup);
        GridView gridView = dialog.findViewById(R.id.tags_grid);
        gridView.setAdapter(new FilterAdapter(getApplicationContext(), Constants.categories));
        gridView.setOnItemClickListener((adapterView, view, pos, id) -> {
            // TODO: Write tag to database, display the next image with categories
            selectedTag = pos;
        });
        dialog.show();
        Objects.requireNonNull(dialog.getWindow())
                .setLayout((6 * Constants.width) / 7, (4 * Constants.height) / 5);
    }

    private class LoadImagesFromDB extends AsyncTask<Void, Void, List<ImageDetails>> {

        @Override
        protected List<ImageDetails> doInBackground(Void... voids) {
            return Constants.imageDatabase.imageDao().getAll();
        }

        @Override
        protected void onPostExecute(List<ImageDetails> imageDetails) {
            if (imageDetails != null) {
                recyclerViewImages.addAll(imageDetails);
                galleryAdapter.notifyDataSetChanged();
            }
            new ReadImagesFromMediaStore().execute();
        }
    }

    private class ReadImagesFromMediaStore extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            Cursor images = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, Constants.imagesProjection,
                    MediaStore.Images.Media.DATA + " like ? ", Constants.imagesFolder, null);

            if (images != null) {
                while (images.moveToNext()) {
                    long image_id = images.getLong(images.getColumnIndexOrThrow(Constants.imagesProjection[0]));
                    String image_path = images.getString(images.getColumnIndexOrThrow(Constants.imagesProjection[1]));
                    String tag = "None";

                    ImageDetails databaseResult = Constants.imageDatabase.imageDao().getRecordFromImagePath(image_path);

                    if (databaseResult == null) {
                        ImageDetails imageDetails = new ImageDetails(image_path, image_id, tag);
                        Constants.imageDatabase.imageDao().insertOneRecord(imageDetails);
                        recyclerViewImages.add(imageDetails);
                        runOnUiThread(() ->
                                galleryAdapter.notifyItemInserted(recyclerViewImages.size() - 1)
                        );
                    } else {
                        ImageDetails imageDetails = new ImageDetails(image_path, image_id, databaseResult.getImageTag());
                        Constants.imageDatabase.imageDao().update(imageDetails);
                    }
                }
                images.close();
            }
            return null;
        }
    }
}
