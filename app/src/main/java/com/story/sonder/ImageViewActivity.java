package com.story.sonder;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ImageViewActivity extends Activity {

    private List<ImageDetails> images = new ArrayList<>();
    private ViewPager imagePager;
    private TextView tagView;
    private String filter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_view);

        Intent intent = getIntent();
        int imagePosition = Objects.requireNonNull(intent.getExtras()).getInt("image_position");
        filter = intent.getExtras().getString("set_filter");

        imagePager = findViewById(R.id.image_pager);
        tagView = findViewById(R.id.image_tag_text);

        AsyncTask.execute(() -> {
            if (filter != null) {
                images.addAll(Constants.imageDatabase.imageDao().filterImages(filter));
            } else {
                images.addAll(Constants.imageDatabase.imageDao().getAll());
            }
            runOnUiThread(() -> {
                ImagePagerAdapter imagePagerAdapter = new ImagePagerAdapter(images, this);
                imagePager.setAdapter(imagePagerAdapter);
                imagePager.setCurrentItem(imagePosition);
            });
        });

        imagePager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                AsyncTask.execute(() -> {
                    String tag = Constants.imageDatabase.imageDao().getRecordFromImagePath(images.get(position).getImagePath()).getImageTag();
                    runOnUiThread(() -> tagView.setText(tag));
                });
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    public void openTagView(View view) {
        final Dialog dialog = Util.createDialog(this, R.layout.filter_popup);
        final TextView tagView = findViewById(R.id.image_tag_text);
        final GridView gridView = dialog.findViewById(R.id.filters);
        gridView.setAdapter(new FilterAdapter(getApplicationContext(), Constants.categories));
        gridView.setOnItemClickListener((adapterView, v, pos, id) -> {
            int currentPage = imagePager.getCurrentItem();
            images.get(currentPage).setImageTag(Constants.categories[pos]);
            tagView.setText(Constants.categories[pos]);
            AsyncTask.execute(() ->
                    Constants.imageDatabase.imageDao().update(images.get(currentPage))
            );
            dialog.dismiss();
        });
        dialog.show();
        Objects.requireNonNull(dialog.getWindow())
                .setLayout((6 * Constants.width) / 7, (3 * Constants.height) / 5);
    }

    public void openShareView(View view) {
        // TODO: Error on Android N and above- file:// URIs not allowed, fix it.
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("image/*");
        String imagePath = Environment.getExternalStorageDirectory()
                + "@drawable/sunset.jpg"; // TODO: use image field

        File imageFileToShare = new File(imagePath);
        Uri uri = Uri.fromFile(imageFileToShare);

        share.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(share, "Share Image!"));
    }
}
