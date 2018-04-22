package com.story.sonder;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.List;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ImagePagerAdapter extends PagerAdapter {
    private final Context context;
    private final List<ImageDetails> images;

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        LayoutInflater inflater = (LayoutInflater) this.context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View imagePagerItem = inflater.inflate(R.layout.image_pager_item, container, false);
        ImageView item = imagePagerItem.findViewById(R.id.image);
        Glide.with(context).load(images.get(position).getImagePath()).into(item);
        container.addView(imagePagerItem);
        return imagePagerItem;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}
