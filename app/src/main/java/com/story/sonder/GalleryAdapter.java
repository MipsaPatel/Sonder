package com.story.sonder;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.MyViewHolder> {
    private final Context context;

    @Getter
    private final List<ImageDetails> images = new ArrayList<>();

    private final Category filter;

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.gallery_item, parent, false);
        return new MyViewHolder(item);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Glide.with(context)
                .load(images.get(position).getImagePath())
                .centerCrop()
                .placeholder(R.drawable.ic_icon_placeholder)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .into(holder.image);
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public ImageDetails get(int position) {
        return images.get(position);
    }

    public int addImage(ImageDetails imageDetails) {
        int index = Collections.binarySearch(images, imageDetails);
        if (index >= 0) {
            return -1;
        }
        index = -index - 1;
        images.add(index, imageDetails);
        return index;
    }

    public int removeImage(ImageDetails imageDetails) {
        int index = Collections.binarySearch(images, imageDetails);
        if (index < 0) {
            return -1;
        }
        images.remove(index);
        return index;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final ImageView image;

        MyViewHolder(View item) {
            super(item);
            image = item.findViewById(R.id.galleryItem);
            item.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(context, ImageViewActivity.class);
            intent.putExtra("image_position", getLayoutPosition());
            intent.putExtra("set_filter", CategoryConverter.toInt(filter));
            context.startActivity(intent);
        }
    }
}
