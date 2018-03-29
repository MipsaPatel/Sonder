package com.story.sonder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

public class FilterAdapter extends BaseAdapter {

    private Context context;
    private String[] tags;

    FilterAdapter(Context context, String[] tags) {
        this.context = context;
        this.tags = tags;
    }

    @Override
    public int getCount() {
        return tags.length;
    }

    @Override
    public Object getItem(int i) {
        return tags[i];
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int pos, View view, ViewGroup viewGroup) {
        if (view != null)
            return view;
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View gridView = null;
        if (layoutInflater != null) {
            gridView = layoutInflater.inflate(R.layout.grid_item, null);
            Button tag = gridView.findViewById(R.id.grid_button);
            tag.setText(tags[pos]);
        }
        return gridView;
    }
}
