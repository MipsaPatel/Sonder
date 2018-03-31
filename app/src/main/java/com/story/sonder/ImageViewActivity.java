package com.story.sonder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ImageView;

public class ImageViewActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_view);

        Intent intent = getIntent();
        int image = intent.getExtras().getInt("image");
        ImageView item = findViewById(R.id.image);
        item.setImageResource(image);
    }
}
