package com.example.android.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.android.popularmovies.FetchTasks.FetchMovieInfoTask;
import com.example.android.popularmovies.adapter.MovieAdapter;
import com.example.android.popularmovies.data.DBContract;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final int MOVIE_LOADER = 0;
    private MovieAdapter mMovieAdapter;
    private GridView mGridView;

    private static final String[] MOVIE_COLUMNS = {
            DBContract.MovieEntry._ID,
            DBContract.MovieEntry.COLUMN_POSTER_PATH
    };

    // These indices are tied to MOVIE_COLUMNS. If MOVIE_COLUMNS changes, these must change.
    static final int COL_ID = 0;
    public static final int COL_POSTER_PATH = 1;

    public static final String SORT_BY_KEY = "movie_sort_order";

    public MainActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //inflater.inflate(R.menu., menu);
    }
/*
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_refresh) {

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
*/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //Log.d("MainActivityFragment", "onCreateView" );
        // The CursorAdapter will take data from our cursor and populate the GridView
        mMovieAdapter= new MovieAdapter(getActivity(), null, 0);

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // Get a reference to the GridView, and attach this adapter to it.
        mGridView = (GridView) rootView.findViewById(R.id.gridViewId);
        mGridView.setAdapter(mMovieAdapter);

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                // CursorAdapter returns a cursor at the correct position for getItem(), or null
                // if it cannot seek to that position.
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if (cursor != null) {
                    Intent intent = new Intent(getActivity(), DetailActivity.class)
                            .setData(DBContract.MovieEntry.buildMovieUri(
                                    cursor.getLong(COL_ID)
                            ));
                    startActivity(intent);
                }
            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = new Bundle();
        bundle.putString(SORT_BY_KEY, getPreferenceSortOrder());
        getLoaderManager().initLoader(MOVIE_LOADER  ,null, this);

    }

    public  String getPreferenceSortOrder() {
        // get sort by from share preference
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sort_by = prefs.getString(getString(R.string.pref_sort_by_key),
                getString(R.string.pref_sort_by_default));

        return sort_by;
    }

    private void updateMovies(){
        //Log.d("MainActivityFragment", "updateMovies" );

        String prefSortOrder = getPreferenceSortOrder();
        if(prefSortOrder.equals("popular")||prefSortOrder.equals("top_rated"))
        {
            FetchMovieInfoTask task=new FetchMovieInfoTask(getActivity());
            task.execute(prefSortOrder);

        }else if(prefSortOrder.equals("favorite"))
        {

        }

        getLoaderManager().restartLoader(MOVIE_LOADER, null, this);
    }


    @Override
    public void onStart() {
        super.onStart();
        //updateMovies();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateMovies();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {

        String prefSortOrder = getPreferenceSortOrder();
        //Log.d("MainActivityFragment", "onCreateLoader, getPreferenceSortOrder: "+prefSortOrder );
        String sortOrder="favorite";
        //String sortOrder="vote_average";
        if(prefSortOrder.equals("popular"))
        {
            prefSortOrder= DBContract.MovieEntry.COLUMN_POPULARITY + " DESC";

        }
        else if(prefSortOrder.equals("top_rated"))
        {
            sortOrder= DBContract.MovieEntry.COLUMN_VOTE_AVERAGE+ " DESC";
        }
        else if(prefSortOrder.equals("favorite"))
        {
            sortOrder= DBContract.MovieEntry.COLUMN_FAVOURITE+ " DESC";
        }

        Uri movieUri = DBContract.MovieEntry.CONTENT_URI;
        //Log.d("MainActivityFragment", "onCreateLoader"+movieUri.toString()+"  sortOrder: "+sortOrder);
        return new CursorLoader(getActivity(), movieUri, MOVIE_COLUMNS, null, null, sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        mMovieAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMovieAdapter.swapCursor(null);
    }
}
