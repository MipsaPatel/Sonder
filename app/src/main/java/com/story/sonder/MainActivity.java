package com.story.sonder;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
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
    private int[] images = {R.drawable.sunset_portrait, R.drawable.sunset, R.drawable.sunset, R.drawable.sunset, R.drawable.sunset};
    private int selectedFilter = 0;
    private int selectedTag = 0;
    private int galleryColumns = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        Constants.height = metrics.heightPixels;
        Constants.width = metrics.widthPixels;

        RecyclerView galleryView = findViewById(R.id.gallery_view);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, galleryColumns);
        galleryView.setLayoutManager(gridLayoutManager);
        GalleryAdapter galleryAdapter = new GalleryAdapter(this, images);
        galleryView.setAdapter(galleryAdapter);

        final ImageButton closeButton = findViewById(R.id.close_filter_button);
        final TextView filterView = findViewById(R.id.filterView);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: Display all images in RecyclerView
                filterView.setVisibility(View.GONE);
                closeButton.setVisibility(View.GONE);
            }
        });
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
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
                // TODO: Display only filtered images in RecyclerView.
                selectedFilter = pos;
                filterView.setVisibility(View.VISIBLE);
                closeButton.setVisibility(View.VISIBLE);
                filterView.setText(gridView.getItemAtPosition(pos).toString());
                dialog.dismiss();
            }
        });
        dialog.show();
        Objects.requireNonNull(dialog.getWindow())
                .setLayout((6 * Constants.width) / 7, (3 * Constants.height) / 5);
    }

    private void showTagSelectionDialog() {
        final Dialog dialog = Util.createDialog(this, R.layout.tag_popup);
        GridView gridView = dialog.findViewById(R.id.tags_grid);
        gridView.setAdapter(new FilterAdapter(getApplicationContext(), Constants.categories));
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
                // TODO: Write tag to database, display the next image with categories
                selectedTag = pos;
            }
        });
        dialog.show();
        Objects.requireNonNull(dialog.getWindow())
                .setLayout((6 * Constants.width) / 7, (4 * Constants.height) / 5);
    }
}
