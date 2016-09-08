package com.example.android.popularmovies.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies.DetailActivityFragment;
import com.example.android.popularmovies.R;

/**
 * Created by Jerome Durand on 28/06/2016.
 */
public class VideoAdapter extends CursorAdapter {


    public VideoAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.trailer_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        // Read video information from cursor
        viewHolder.textName.setText(cursor.getString(DetailActivityFragment.COL_VIDEO_NAME));
        Log.d("VideoAdapter", "bindView: "+ cursor.getString(DetailActivityFragment.COL_VIDEO_NAME));
    }

    // Cache of the children view
    static class ViewHolder {
        TextView textName;
        ImageView imageView;

        public ViewHolder(View view) {
            textName = (TextView) view.findViewById(R.id.textView_name);
            imageView = (ImageView) view.findViewById(R.id.imageView_thumbnail);
        }
    }
}
