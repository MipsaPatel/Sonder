package com.story.sonder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class FilterAdapter extends BaseAdapter {
    private final Context context;
    private final Category[] tags;

    @Override
    public int getCount() {
        return tags.length;
    }

    @Override
    public Object getItem(int position) {
        return tags[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            LayoutInflater layoutInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (layoutInflater != null) {
                convertView = layoutInflater.inflate(R.layout.grid_item, parent, false);
                holder.tag = convertView.findViewById(R.id.grid_button);
                convertView.setTag(holder);
            }
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tag.setText(getItem(position).toString());
        return convertView;
    }

    static class ViewHolder {
        Button tag;
    }
}
