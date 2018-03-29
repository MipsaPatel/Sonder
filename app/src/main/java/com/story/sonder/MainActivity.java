package com.story.sonder;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.Objects;

public class MainActivity extends Activity {
    private String[] tags = {"Sunset", "Beach", "Selfie", "Portrait", "Scenery", "Friends",
            "Hills", "Meme", "Work", "Meee", "None"};
    private int selectedFilter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.tag:
                break;

            case R.id.filter:
                showFilterSelectionDialog();
                break;

            case R.id.about_us:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showFilterSelectionDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(R.layout.filter_popup);
        dialog.setTitle(R.string.filter_select);
        GridView gridView = dialog.findViewById(R.id.filters);
        gridView.setAdapter(new FilterAdapter(getApplicationContext(), tags));
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
                // TODO: Set the option in MainActivity
                selectedFilter = pos;
                dialog.dismiss();
            }
        });
        dialog.show();
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        Objects.requireNonNull(dialog.getWindow())
                .setLayout((6 * width) / 7, (3 * height) / 5);
    }
}
