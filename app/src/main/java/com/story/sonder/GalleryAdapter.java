package com.story.sonder;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.MyViewHolder> {

    private List<ImageDetails> images;
    private Context context;
    private ContentResolver contentResolver;

    GalleryAdapter(Context context, List<ImageDetails> images, ContentResolver contentResolver) {
        this.context = context;
        this.images = images;
        this.contentResolver = contentResolver;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery_item, parent, false);
        return new MyViewHolder(item);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        long imageId = images.get(position).getImageId();
        Bitmap thumbnail = MediaStore.Images.Thumbnails.getThumbnail(contentResolver, imageId, MediaStore.Images.Thumbnails.MICRO_KIND, null);

        if (thumbnail != null) {
            holder.image.setImageBitmap(thumbnail);
        }

        else {
            String imagePath = images.get(position).getImagePath();
            Bitmap thumbnailBitmap = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(imagePath), 96, 96);
            holder.image.setImageBitmap(thumbnailBitmap);
        }
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView image;
         MyViewHolder(View item) {
            super(item);
            image = item.findViewById(R.id.galleryItem);
            item.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(context, ImageViewActivity.class);
            intent.putExtra("image_position", getLayoutPosition());
            context.startActivity(intent );
        }
    }
}
