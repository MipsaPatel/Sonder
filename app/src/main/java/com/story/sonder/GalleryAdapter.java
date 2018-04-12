package com.story.sonder;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.MyViewHolder> {

    private Cursor cursor;
    private Context context;

    GalleryAdapter(Context context, Cursor cursor) {
        this.context = context;
        this.cursor = cursor;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery_item, parent, false);
        return new MyViewHolder(item);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        cursor.moveToPosition(position);
        String thumbnail = cursor.getString(cursor.getColumnIndexOrThrow("thumb_path"));

        //TODO: Move bitmap and thumbnail generation to background thread
        if (thumbnail != null) {
            Bitmap thumbnailBitmap;
            thumbnailBitmap = BitmapFactory.decodeFile(thumbnail);
            holder.image.setImageBitmap(thumbnailBitmap);
        }

        else {
            String imagePath = cursor.getString(cursor.getColumnIndexOrThrow("image_path"));
            Bitmap thumbnailBitmap = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(imagePath), 256, 256);
            holder.image.setImageBitmap(thumbnailBitmap);
        }
    }

    @Override
    public int getItemCount() {
        return cursor.getCount();
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
