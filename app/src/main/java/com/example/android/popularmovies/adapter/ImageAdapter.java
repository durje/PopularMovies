package com.example.android.popularmovies.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.model.Movie;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jerome Durand on 08/03/2016.
 */
public class ImageAdapter extends BaseAdapter {

    private Context mContext;
    List<String> list = new ArrayList<>();
    Movie[] mMovies;


    public Movie[] getmMovies()
    {
        return mMovies;
    }
    public ImageAdapter(Context c) {
        mContext = c;
    }

    @Override
    public int getCount() {

        return list.size();
    }

    @Override
    public Object getItem(int position) {

        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setAdjustViewBounds(true);
            //imageView.setLayoutParams(new GridView.LayoutParams(85, 85));
            //imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            //imageView.setPadding(0, 0, 0, 0);
        } else {
            imageView = (ImageView) convertView;
        }
        //Log.v("", "movie: " + position + " posterPath: " + list.get(position));
        Picasso.with(mContext)
                .load(list.get(position))
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder_error)
                .into(imageView);

        return imageView;
    }

    public void add(String s) {
        list.add(s);
        notifyDataSetChanged();
    }
/*
    public void addAll(String[] c_string) {
        list.addAll(Arrays.asList(c_string));
        notifyDataSetChanged();
    }

    public void addAll(List<String> c_string) {
        list=new ArrayList<>(c_string);
        notifyDataSetChanged();
    }
*/
    public void addAll(Movie[] movies) {
        //mMovies=new Movie[movies.length];
        mMovies=movies.clone();
        for (Movie movie : movies) {
                list.add(movie.getPosterPath());

            //Log.v("ImageAdapter"," addAll "+i + " PosterPath: "+movies[i].getPosterPath() );
        }
        notifyDataSetChanged();
        }

    public void clear() {
        list.clear();
    }
}
