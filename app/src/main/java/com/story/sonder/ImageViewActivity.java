package com.story.sonder;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ImageViewActivity extends Activity {
    private static final String tagNeedsModel = "Cannot tag pictures without model";
    private Category filter;
    private ViewPager imagePager;
    private TextView tagView;
    private List<ImageDetails> images;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_view);

        Intent intent = getIntent();
        int imagePosition = Objects.requireNonNull(intent.getExtras()).getInt("image_position");
        filter = CategoryConverter.toCategory(intent.getExtras().getInt("set_filter"));

        imagePager = findViewById(R.id.image_pager);
        tagView = findViewById(R.id.image_tag_text);
        images = new ArrayList<>(AppResources.adapter.get(filter).getImages());

        imagePager.setAdapter(new ImagePagerAdapter(this, images));
        imagePager.setCurrentItem(imagePosition);
        setTag(imagePosition);

        imagePager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int pos, float positionOffset, int positionOffsetPixels) {
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
        if (AppResources.model == null) {
            Toast.makeText(this, tagNeedsModel, Toast.LENGTH_SHORT).show();
            return;
        }
        Dialog dialog = Util.createDialog(this, R.layout.filter_popup);
        GridView gridView = dialog.findViewById(R.id.filters);
        dialog.setOnDismissListener(dialogInterface -> {
            AppResources.imageInProcess = null;
            AsyncTask.execute(AppResources.model::update);
        });

        ImageDetails imageDetails = images.get(imagePager.getCurrentItem());
        AsyncTask.execute(() -> Util.tagImage(this, dialog, gridView, imageDetails));

        gridView.setOnItemClickListener((parent, v, position, id) -> {
            AppResources.imageInProcess.updateAdapter(position);
            AppResources.imageInProcess.train();
            setTag(imagePager.getCurrentItem());
            dialog.dismiss();
        });
    }

    public void openShareView(View view) {
        // TODO: Use content-provider
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("image/*");
        ImageDetails imageDetails = images.get(imagePager.getCurrentItem());
        share.putExtra(Intent.EXTRA_STREAM, Uri.parse(imageDetails.getImagePath()));
        share.putExtra(Intent.EXTRA_TEXT, imageDetails.getImageTag().toString());
        startActivity(Intent.createChooser(share, "Share with"));
    }

    private void setTag(int position) {
        tagView.setText(images.get(position).getImageTag().toString());
    }
}
