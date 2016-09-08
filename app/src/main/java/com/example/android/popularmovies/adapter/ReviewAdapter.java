package com.example.android.popularmovies.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.popularmovies.DetailActivityFragment;
import com.example.android.popularmovies.R;

/**
 * Created by Jerome Durand on 08/09/2016.
 */
public class ReviewAdapter extends CursorAdapter {

    public ReviewAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.review_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        // Read video information from cursor
        viewHolder.textAuthor.setText("A movie review by "+cursor.getString(DetailActivityFragment.COL_REVIEW_AUTHOR));
        viewHolder.textContent.setText(cursor.getString(DetailActivityFragment.COL_REVIEW_CONTENT));
    }

    // Cache of the children view
    static class ViewHolder {
        TextView textAuthor;
        TextView textContent;

        public ViewHolder(View view) {
            textAuthor = (TextView) view.findViewById(R.id.textView_author);
            textContent = (TextView) view.findViewById(R.id.textView_content);
        }
    }
}
