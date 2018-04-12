package com.story.sonder;

import android.app.Activity;
import android.app.Dialog;
import android.arch.persistence.room.Room;
import android.content.Intent;
import android.database.Cursor;
import android.database.CursorJoiner;
import android.database.MatrixCursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Objects;

public class MainActivity extends Activity {
    private int selectedFilter = 0;
    private int selectedTag = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        Constants.height = metrics.heightPixels;
        Constants.width = metrics.widthPixels;

        Constants.imageDatabase = Room.databaseBuilder(getApplicationContext(), ImageDatabase.class, "image-db").build();

        final ImageButton closeButton = findViewById(R.id.close_filter_button);
        final TextView filterView = findViewById(R.id.filterView);
        closeButton.setOnClickListener(view -> {
            // TODO: Display all images in RecyclerView
            filterView.setVisibility(View.GONE);
            closeButton.setVisibility(View.GONE);
        });

        new ImageAsyncTask().execute();
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
        final TextView filterView = findViewById(R.id.filterView);
        final ImageButton closeButton = findViewById(R.id.close_filter_button);
        final GridView gridView = dialog.findViewById(R.id.filters);
        gridView.setAdapter(new FilterAdapter(getApplicationContext(), Constants.categories));
        gridView.setOnItemClickListener((adapterView, view, pos, id) -> {
            // TODO: Display only filtered images in RecyclerView.
            selectedFilter = pos;
            filterView.setVisibility(View.VISIBLE);
            closeButton.setVisibility(View.VISIBLE);
            filterView.setText(gridView.getItemAtPosition(pos).toString());
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

    private class ImageAsyncTask extends AsyncTask<Void, Void, Cursor> {

        //TODO: Put a progress update while images are loading, if time permits.

        @Override
        protected Cursor doInBackground(Void... voids) {
//            ImageDetails imageDetails = new ImageDetails("file://img.png", "sunset");
//            Constants.imageDatabase.imageDao().insertAll(imageDetails);

            Cursor thumbnails = getContentResolver().query(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, Constants.thumbnailsProjection,
                    null, null, "(" + MediaStore.Images.Thumbnails.IMAGE_ID + "*(-1))");

            Cursor images = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, Constants.imagesProjection,
                    MediaStore.Images.Media.DATA + " like ? ", Constants.imagesFolder, "(" + MediaStore.Images.Media._ID + "*(-1))");

            CursorJoiner cursorJoiner = new CursorJoiner(images, new String[] {MediaStore.Images.Media._ID},
                    thumbnails, new String[] {MediaStore.Images.Thumbnails.IMAGE_ID});

            MatrixCursor results = new MatrixCursor(new String[] {"thumb_path", "image_id", "image_path"});

            for (CursorJoiner.Result joinerResult: cursorJoiner) {
                switch (joinerResult) {
                    case LEFT:
                        results.addRow(new Object[] {
                                null,
                                images.getLong(images.getColumnIndexOrThrow(Constants.imagesProjection[0])),
                                images.getString(images.getColumnIndexOrThrow(Constants.imagesProjection[1]))
                        });
                        break;

                    case RIGHT:
                        break;

                    case BOTH:
                        results.addRow(new Object[] {
                                thumbnails.getString(thumbnails.getColumnIndexOrThrow(Constants.thumbnailsProjection[1])),
                                images.getLong(images.getColumnIndexOrThrow(Constants.imagesProjection[0])),
                                images.getString(images.getColumnIndexOrThrow(Constants.imagesProjection[1]))
                        });
                        break;
                }
            }
            images.close();
            thumbnails.close();

            return results;
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            RecyclerView galleryView = findViewById(R.id.gallery_view);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), Constants.galleryColumns);
            galleryView.setLayoutManager(gridLayoutManager);
            GalleryAdapter galleryAdapter = new GalleryAdapter(getApplicationContext(), cursor);
            galleryView.setAdapter(galleryAdapter);
        }
    }
}
