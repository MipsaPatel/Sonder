package com.story.sonder;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.arch.persistence.room.Room;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.story.sonder.model.ModelUtils;

import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class MainActivity extends Activity {
    final private int REQUEST_STORAGE_PERMISSION = 1;
    Random random = new Random();

    private List<ImageDetails> recyclerViewImages = new ArrayList<>();
    private GalleryAdapter galleryAdapter;
    private RecyclerView galleryView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeConstants();
        checkStoragePermission();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ConstraintLayout filterLayout = findViewById(R.id.constraintLayout);
        if (filterLayout.getVisibility() == View.VISIBLE) {
            TextView filterView = findViewById(R.id.filterView);
            String filter = filterView.getText().toString();
            setFilteredImagesAdapter(filter);
        }
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

            case R.id.sync:
                syncModelWithServer();
                break;

            case R.id.about_us:
                startActivity(new Intent(this, AboutUsActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void checkStoragePermission() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission
                    .READ_EXTERNAL_STORAGE}, REQUEST_STORAGE_PERMISSION);
        else {
            displayMainScreen();
            getModelConfigFile();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_STORAGE_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    displayMainScreen();
                    getModelConfigFile();
                } else
                    Toast.makeText(this, "STORAGE PERMISSION DENIED. App will" + " not function.", Toast
                            .LENGTH_SHORT).show();
                break;

            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void initializeConstants() {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        Constants.height = metrics.heightPixels;
        Constants.width = metrics.widthPixels;

        if (Constants.imageDatabase == null)
            Constants.imageDatabase = Room.databaseBuilder(getApplicationContext(), ImageDatabase.class, "image-database").build();
    }

    private void displayMainScreen() {
        galleryView = findViewById(R.id.gallery_view);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), Constants.galleryColumns);
        galleryView.setLayoutManager(gridLayoutManager);
        galleryAdapter = new GalleryAdapter(this, recyclerViewImages, null, getContentResolver());
        galleryView.setAdapter(galleryAdapter);

        ConstraintLayout filterLayout = findViewById(R.id.constraintLayout);
        final ImageButton closeButton = findViewById(R.id.close_filter_button);
        closeButton.setOnClickListener(view -> {
            filterLayout.setVisibility(View.GONE);
            galleryView.setAdapter(galleryAdapter);
        });

        new LoadImagesFromDB().execute();
    }

    private void getModelConfigFile() {
        new GetInitialConfig(getApplicationContext()).fetchInitialModel();
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

            setFilteredImagesAdapter(Constants.categories[pos]);
            dialog.dismiss();
        });

        dialog.show();
        Objects.requireNonNull(dialog.getWindow())
                .setLayout((6 * Constants.width) / 7, (3 * Constants.height) / 5);
    }

    private void setFilteredImagesAdapter(String filter) {
        AsyncTask.execute(() -> {
            List<ImageDetails> filteredImages = Constants.imageDatabase.imageDao().filterImages(filter);
            runOnUiThread(() -> galleryView.setAdapter(
                    new GalleryAdapter(getApplicationContext(), filteredImages,
                            filter, getContentResolver())
            ));
        });
    }

    private void showTagSelectionDialog() {
        final Dialog dialog = Util.createDialog(this, R.layout.tag_popup);
        GridView gridView = dialog.findViewById(R.id.tags_grid);
        ImageView imageView = dialog.findViewById(R.id.tag_image);

        android.view.ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
        layoutParams.height = (4 * Constants.height) / 10;
        imageView.setLayoutParams(layoutParams);

        List<ImageDetails> images = new ArrayList<>();
        Pair<Pair<String[], Object>, ImageDetails> taggedImageData = Pair.create(
                Pair.create(new String[Constants.topK], new Object()), new ImageDetails());

        AsyncTask.execute(() -> {
            List<ImageDetails> filteredImages = Constants.imageDatabase.imageDao().filterImages("None");
            images.addAll(filteredImages);
            runOnUiThread(() -> tagImage(images, taggedImageData.second, imageView, gridView, dialog));
        });

        gridView.setOnItemClickListener((adapterView, view, pos, id) -> {
            taggedImageData.second.setImageTag(gridView.getItemAtPosition(pos).toString());

            AsyncTask.execute(() -> {
                Constants.imageDatabase.imageDao().update(taggedImageData.second);
                runOnUiThread(() -> tagImage(images, taggedImageData.second, imageView, gridView, dialog));
            });
        });
        dialog.show();
        Objects.requireNonNull(dialog.getWindow())
                .setLayout((6 * Constants.width) / 7, (4 * Constants.height) / 5);
    }

    private void syncModelWithServer() {
        long now = System.currentTimeMillis();
        if (!Constants.syncSuccessful || now - Constants.lastSyncTime > 5 * 60 * 1000) {
            Constants.lastSyncTime = now;
            SyncModel syncModel = new SyncModel(getApplicationContext());
            syncModel.sendParameters();
            syncModel.fetchParameters();
        } else {
            Toast.makeText(getApplicationContext(), "Cannot sync model. Been less than 5 minutes since last successful sync.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void tagImage(List<ImageDetails> images, ImageDetails taggedImage, ImageView imageView,
                          GridView gridView, Dialog dialog) {
        if (images.size() > 0) {
            Pair<Pair<String[], Object>, ImageDetails> image = updateTagDialog(images, imageView, gridView);
            taggedImage.setImageId(image.second.getImageId());
            taggedImage.setImagePath(image.second.getImagePath());
        } else {
            dialog.cancel();
            Toast.makeText(getApplicationContext(), "All images are tagged.", Toast.LENGTH_SHORT).show();
        }
    }

    private Pair<Pair<String[], Object>, ImageDetails> updateTagDialog(List<ImageDetails> images, ImageView imageView, GridView gridView) {
        int position = random.nextInt(images.size());

        ImageDetails image = images.get(position);
        imageView.setImageBitmap(BitmapFactory.decodeFile(image.getImagePath()));
        Pair<String[], Object> forwardPhaseOutput = Util.getTagCategories(image.getImagePath());
        gridView.setAdapter(new FilterAdapter(getApplicationContext(), forwardPhaseOutput.first));

        return Pair.create(forwardPhaseOutput, images.remove(position));
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
            Cursor images = getContentResolver().query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    Constants.imagesProjection,
                    MediaStore.Images.Media.DATA + " like ? ", Constants.imagesFolder,
                    null);

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
                        runOnUiThread(() -> galleryAdapter.notifyItemInserted(recyclerViewImages.size() - 1));
                    } else
                        Constants.imageDatabase.imageDao()
                                .update(new ImageDetails(image_path, image_id, databaseResult.getImageTag()));
                }
                images.close();
            }
            return null;
        }
    }

    @Override
    public void onBackPressed() {
        ConstraintLayout filterLayout = findViewById(R.id.constraintLayout);
        if (filterLayout.getVisibility() == View.VISIBLE) {
            ImageButton closeButton = findViewById(R.id.close_filter_button);
            closeButton.callOnClick();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (Constants.saveToFile) {
            File paramsFile = new File(getApplicationContext().getFilesDir(), Constants.paramsFile);
            List<double[]> parameters = Constants.model.second.second.getParameters();
            try {
                Util.writeToFile(paramsFile, ModelUtils.parametersToJSONArray(parameters));
            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }
        }
    }
}
