package com.example.android.popularmovies.adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import com.example.android.popularmovies.R;
import com.squareup.picasso.Picasso;

/**
 * Created by Jerome Durand on 22/03/2016.
 *  {@link MovieAdapter} exposes a list of movie info
 * from a {@link Cursor} to a {@link android.widget.BaseAdapter}.
 */
public class MovieAdapter  extends CursorAdapter {


    /**
     * Cache of the children views for image list item.
     */
    public static class ViewHolder {
        public final ImageView posterView;

        public ViewHolder(View view) {
            posterView = (ImageView) view.findViewById(R.id.list_item_poster_imageview);
        }
    }

    @TargetApi(11)
    public MovieAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View imageView = LayoutInflater.from(context).inflate(R.layout.list_item_poster, parent, false);

        ViewHolder viewHolder = new ViewHolder(imageView);
        imageView.setTag(viewHolder);
        Log.d("MovieAdapter", "new View " );
        return imageView;
    }

    static final int COL_POSTER_PATH = 5;
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        final String base_path="http://image.tmdb.org/t/p/w185/";
        String posterUrlStr =/*base_path+*/cursor.getString(COL_POSTER_PATH);

        ViewHolder viewHolder = (ViewHolder) view.getTag();


        if (((ImageView)view).getDrawable() == null) {
            Picasso.with(context)
                    .load(posterUrlStr)
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder_error)
                    .into(viewHolder.posterView);
            Log.d("MovieAdapter", "posterUrlStr: " + posterUrlStr);
            notifyDataSetChanged();
        } else {
            //view = (ImageView) view;
        }

    }
}
