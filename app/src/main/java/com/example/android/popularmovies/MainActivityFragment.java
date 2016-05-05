package com.example.android.popularmovies;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.example.android.popularmovies.adapter.MovieAdapter;
import com.example.android.popularmovies.data.DBContract;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    //private ImageAdapter mImageAdapter;
    private MovieAdapter mMovieAdapter;
    private GridView mGridView;

    public MainActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //inflater.inflate(R.menu.menuitem_refresh_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
       /* if (id == R.id.action_refresh) {

            return true;
        }
        */
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // The ImageAdapter will take data from a source and
        // use it to populate the GridView it's attached to.
        //mImageAdapter = new ImageAdapter(getActivity());


        Uri moviesUri = DBContract.MovieEntry.buildMovieUri();

        Cursor cur = getActivity().getContentResolver().query(moviesUri,
                null, null, null, null);



        // The CursorAdapter will take data from our cursor and populate the GridView
        // However, we cannot use FLAG_AUTO_REQUERY since it is deprecated, so we will end
        // up with an empty list the first time we run.
        mMovieAdapter= new MovieAdapter(getActivity(), cur, 0);


        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // Get a reference to the GridView, and attach this adapter to it.
        mGridView = (GridView) rootView.findViewById(R.id.gridViewId);
        mGridView.setAdapter(mMovieAdapter);
/*
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //Log.v("MainActivityFragment", " Title: " + mImageAdapter.getmMovies()[position].getTitle());
                //Toast.makeText(getActivity(), "" + position, Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(getActivity(), DetailActivity.class)
                        .putExtra("movieId", mImageAdapter.getmMovies()[position].getId())
                        .putExtra("poster_path", mImageAdapter.getmMovies()[position].getPosterPath())
                        .putExtra("title", mImageAdapter.getmMovies()[position].getTitle())
                        .putExtra("overview", mImageAdapter.getmMovies()[position].getOverview())
                        .putExtra("vote_average", mImageAdapter.getmMovies()[position].getVoteAverage())
                        .putExtra("release_date", mImageAdapter.getmMovies()[position].getReleaseDate());
                startActivity(intent);

            }
        });
*/
        return rootView;
    }

    private void updateMovies(){
        FetchMovieInfoTask task=new FetchMovieInfoTask(getActivity());
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sort_by = prefs.getString(getString(R.string.pref_sort_by_key),
                getString(R.string.pref_sort_by_default));
        task.execute(sort_by);
    }


    @Override
    public void onStart() {
        super.onStart();
        updateMovies();
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
