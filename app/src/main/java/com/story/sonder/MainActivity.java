package com.story.sonder;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.arch.persistence.room.Room;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.story.sonder.model.ModelUtils;

import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class MainActivity extends Activity {
    private static final int REQUEST_STORAGE_PERMISSION = 1;
    private static final String allTagged = "All images are tagged";
    private static final String syncLimit = "Cannot sync model twice in 5 minutes";
    private static final String tagNeedsModel = "Cannot tag pictures without model";
    private final Random random = new Random();
    private RecyclerView galleryView;
    private ConstraintLayout filterLayout;
    private TextView filterTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeConstants();
        initializeAppResources();
        checkStoragePermission();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);
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
        int permission = ContextCompat.checkSelfPermission(
                getApplicationContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
        );

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    MainActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_STORAGE_PERMISSION
            );
        }
        else {
            storagePermissionGranted();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_STORAGE_PERMISSION:
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    storagePermissionGranted();
                }
                else {
                    Toast.makeText(
                            this,
                            "STORAGE PERMISSION DENIED. App will not function.",
                            Toast.LENGTH_SHORT
                    ).show();
                    finishAndRemoveTask();
                }
                break;

            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void storagePermissionGranted() {
        displayMainScreen();
        initializeModel();
    }

    private void initializeConstants() {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        Constants.height = metrics.heightPixels;
        Constants.width = metrics.widthPixels;
    }

    private void initializeAppResources() {
        if (AppResources.imDB == null) {
            AppResources.imDB = Room.databaseBuilder(getApplicationContext(), ImageDatabase.class,
                    "image-database").build();
        }
        for (Category category : Category.values()) {
            AppResources.adapter.put(category, new GalleryAdapter(this, category));
        }
    }

    private void displayMainScreen() {
        galleryView = findViewById(R.id.gallery_view);
        WrapGridLayoutManager gridLayoutManager = new WrapGridLayoutManager(
                getApplicationContext(),
                Constants.galleryColumns
        );
        galleryView.setLayoutManager(gridLayoutManager);
        galleryView.setAdapter(AppResources.adapter.get(Category.All));

        filterLayout = findViewById(R.id.constraintLayout);
        filterTextView = findViewById(R.id.filterView);

        new LoadImagesFromDB(this::addNewImage, this::onLoadedImagesFromDB).execute();
    }

    private void initializeModel() {
        new GetInitialConfig(this).fetchInitialModel();
    }

    private void onLoadedImagesFromDB(Void aVoid) {
        new ReadImagesFromMediaStore(
                getContentResolver(),
                this::addNewImage,
                this::invalidateImages
        ).execute();
    }

    private void addNewImage(ImageDetails details) {
        addNewImage(details, AppResources.adapter.get(Category.All));
        addNewImage(details, AppResources.adapter.get(details.getImageTag()));
    }

    private void addNewImage(ImageDetails details, GalleryAdapter adapter) {
        int index = adapter.addImage(details);
        if (index >= 0) {
            runOnUiThread(() -> adapter.notifyItemInserted(index));
        }
    }

    private void invalidateImages(List<Integer> imageIndices) {
        Collections.sort(imageIndices, Collections.reverseOrder());
        GalleryAdapter adapter = AppResources.adapter.get(Category.All);
        runOnUiThread(() -> {
            for (int index : imageIndices) {
                ImageDetails image = adapter.get(index);
                removeImage(image);
            }
        });
    }

    private void removeImage(ImageDetails details) {
        removeImage(details, AppResources.adapter.get(Category.All));
        removeImage(details, AppResources.adapter.get(details.getImageTag()));
    }

    private void removeImage(ImageDetails details, GalleryAdapter adapter) {
        int index = adapter.removeImage(details);
        if (index < 0) {
            Log.d("removeImage", "attempt to remove an image that does not exist in adapter: '" +
                    details.getImagePath() + "'. Restart the app to reload from database");
        }
        else {
            runOnUiThread(() -> adapter.notifyItemRemoved(index));
        }
    }

    public void resetFilter(View view) {
        galleryView.setAdapter(AppResources.adapter.get(Category.All));
        filterLayout.setVisibility(View.GONE);
    }

    private void setFilter(Category tag) {
        if (tag == Category.All) {
            resetFilter(null);
        }
        else {
            filterLayout.setVisibility(View.VISIBLE);
            filterTextView.setText(tag.toString());
            galleryView.setAdapter(AppResources.adapter.get(tag));
        }
    }

    private void showFilterSelectionDialog() {
        Dialog dialog = Util.createDialog(this, R.layout.filter_popup);
        GridView gridView = dialog.findViewById(R.id.filters);

        gridView.setAdapter(new FilterAdapter(this, Category.values()));
        gridView.setOnItemClickListener((adapterView, view, pos, id) -> {
            setFilter((Category) adapterView.getItemAtPosition(pos));
            dialog.dismiss();
        });
        dialog.show();
    }

    private void showTagSelectionDialog() {
        if (AppResources.model == null) {
            Toast.makeText(this, tagNeedsModel, Toast.LENGTH_SHORT).show();
            return;
        }
        Handler handler = new Handler(Looper.getMainLooper());
        Dialog dialog = Util.createDialog(this, R.layout.tag_popup);
        GridView gridView = dialog.findViewById(R.id.tags_grid);
        ImageView imageView = dialog.findViewById(R.id.tag_image);
        imageView.setMaxHeight(Constants.height / 2);

        dialog.setOnDismissListener(dialogInterface -> {
            AppResources.imageInProcess = null;
            handler.postDelayed(() -> AsyncTask.execute(AppResources.model::update), 1000);
        });

        AsyncTask.execute(() -> tagAnImage(dialog, imageView, gridView));

        gridView.setOnItemClickListener((parent, view, position, id) -> {
            ImageData imageData = AppResources.imageInProcess;
            imageData.updateAdapter(position);
            AppResources.imageInProcess = null;
            dialog.hide();
            AsyncTask.execute(() -> tagAnImage(dialog, imageView, gridView));
            handler.postDelayed(imageData::train, 100);
        });
    }

    private void tagAnImage(Dialog dialog, ImageView imageView, GridView gridView) {
        ImageDetails imageDetails = getAnImageToTag();
        if (imageDetails == null) {
            dialog.cancel();
            Toast.makeText(getApplicationContext(), allTagged, Toast.LENGTH_SHORT).show();
        }
        else {
            runOnUiThread(() -> loadImageIntoView(imageDetails, imageView));
            Util.tagImage(this, dialog, gridView, imageDetails);
        }
    }

    private void loadImageIntoView(ImageDetails imageDetails, ImageView imageView) {
        imageView.layout(0, 0, 0, 0);
        Glide.with(this)
                .load(imageDetails.getImagePath())
                .into(imageView);
    }

    private ImageDetails getAnImageToTag() {
        GalleryAdapter adapter = AppResources.adapter.get(Category.None);
        if (adapter.getItemCount() == 0)
            return null;
        return adapter.get(random.nextInt(adapter.getItemCount()));
    }

    private void syncModelWithServer() {
        if (AppResources.model == null) {
            initializeModel();
            return;
        }
        long now = System.currentTimeMillis();
        if (!Constants.syncSuccessful || now - Constants.lastSyncTime > Constants.syncLimit) {
            Constants.lastSyncTime = now;
            SyncModel syncModel = new SyncModel(getApplicationContext());
            syncModel.sendParameters();
            syncModel.fetchParameters();
        }
        else {
            Toast.makeText(getApplicationContext(), syncLimit, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        if (filterLayout.getVisibility() == View.VISIBLE) {
            resetFilter(null);
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (Constants.saveToFile && AppResources.model != null) {
            File paramsFile = new File(getApplicationContext().getFilesDir(), Constants.paramsFile);
            List<double[]> parameters = AppResources.model.getOptimizer().getParameters();
            try {
                Util.writeToFile(paramsFile, ModelUtils.parametersToJSONArray(parameters));
            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }
        }
    }
}
