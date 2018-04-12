package com.story.sonder;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewParent;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.Objects;

public class ImageViewActivity extends Activity {

    private int imagePosition;
    private int[] images = {R.drawable.sunset_portrait, R.drawable.sunset, R.drawable.sunset, R.drawable.sunset, R.drawable.sunset};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_view);

        Intent intent = getIntent();
        imagePosition = intent.getExtras().getInt("image_position");

        ViewPager imagePager = findViewById(R.id.image_pager);
        ImagePagerAdapter imagePagerAdapter = new ImagePagerAdapter(images, this);
        imagePager.setAdapter(imagePagerAdapter);
        imagePager.setCurrentItem(imagePosition);
    }

    public void openTagView(View view) {
        final Dialog dialog = Util.createDialog(this, R.layout.filter_popup);
        final TextView tagView = findViewById(R.id.image_tag_text);
        final GridView gridView = dialog.findViewById(R.id.filters);
        gridView.setAdapter(new FilterAdapter(getApplicationContext(), Constants.categories));
        gridView.setOnItemClickListener((adapterView, v, pos, id) -> {
            // TODO: Set the tag for the image in database
            // tagView.setText(gridView.getItemAtPosition(pos).toString());
            dialog.dismiss();
        });
        dialog.show();
        Objects.requireNonNull(dialog.getWindow())
                .setLayout((6 * Constants.width) / 7, (3 * Constants.height) / 5);
    }

    public void openShareView(View view){
        //TODO: Error on Android N and above- file:// URIs not allowed, fix it.
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("image/*");
        String imagePath = Environment.getExternalStorageDirectory()
                + "@drawable/sunset.jpg"; //TODO: use image field

        File imageFileToShare = new File(imagePath);
        Uri uri = Uri.fromFile(imageFileToShare);

        share.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(share, "Share Image!"));
    }
}
