package com.story.sonder;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;

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
            if (filter != null)
                images.addAll(Constants.imageDatabase.imageDao().filterImages(filter));
            else
                images.addAll(Constants.imageDatabase.imageDao().getAll());
            runOnUiThread(() -> {
                imagePager.setAdapter(new ImagePagerAdapter(images, this));
                imagePager.setCurrentItem(imagePosition);
            });
        });

        if (imagePosition == 0)
            setTag(0);

        imagePager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                setTag(position);
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
        int currentPage = imagePager.getCurrentItem();
        Pair<String[], Object> forwardPhaseOutput = Util.getTagCategories(images.get(currentPage).getImagePath());

        gridView.setAdapter(new FilterAdapter(getApplicationContext(), forwardPhaseOutput.first));

        gridView.setOnItemClickListener((adapterView, v, pos, id) -> {
            String tag = forwardPhaseOutput.first[pos];
            images.get(currentPage).setImageTag(tag);
            tagView.setText(tag);
            AsyncTask.execute(() -> Constants.imageDatabase.imageDao().update(images.get(currentPage)));
            dialog.dismiss();
        });

        dialog.show();
        Objects.requireNonNull(dialog.getWindow())
                .setLayout((6 * Constants.width) / 7, (Constants.height) / 3);
    }

    public void openShareView(View view) {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("image/*");
        ImageDetails imageDetails = images.get(imagePager.getCurrentItem());
        share.putExtra(Intent.EXTRA_STREAM, Uri.parse(imageDetails.getImagePath()));
        share.putExtra(Intent.EXTRA_TEXT, imageDetails.getImageTag());
        startActivity(Intent.createChooser(share, "Share with"));
    }

    void setTag(int position) {
        AsyncTask.execute(() -> {
            String tag = Constants.imageDatabase.imageDao()
                    .getRecordFromImagePath(images.get(position).getImagePath())
                    .getImageTag();
            runOnUiThread(() -> tagView.setText(tag));
        });
    }
}
