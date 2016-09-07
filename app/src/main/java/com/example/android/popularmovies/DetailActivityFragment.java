package com.example.android.popularmovies;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies.adapter.VideoAdapter;
import com.example.android.popularmovies.data.DBContract;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,FetchVideosTask.Callback {

    private ShareActionProvider mShareActionProvider;
    private String mShareVideoStr="https://youtu.be/";
    private Uri mUri;

    private static final int DETAIL_LOADER = 0;
    private static final int VIDEO_LOADER = 1;

    private static final String[] DETAIL_COLUMNS = {
            DBContract.MovieEntry._ID,
            DBContract.MovieEntry.COLUMN_TITLE,
            DBContract.MovieEntry.COLUMN_OVERVIEW,
            DBContract.MovieEntry.COLUMN_RELEASE_DATE,
            DBContract.MovieEntry.COLUMN_VOTE_AVERAGE,
            DBContract.MovieEntry.COLUMN_VOTE_COUNT,
            DBContract.MovieEntry.COLUMN_POSTER_PATH,
            DBContract.MovieEntry.COLUMN_BACKDROP_PATH,
            DBContract.MovieEntry.COLUMN_FAVOURITE
    };


    private static final int COL_ID = 0;
    private static final int COL_TITLE = 1;
    private static final int COL_OVERVIEW = 2;
    private static final int COL_RELEASE_DATE = 3;
    private static final int COL_VOTE_AVERAGE = 4;
    private static final int COL_VOTE_COUNT = 5;
    private static final int COL_POSTER_PATH = 6;
    private static final int COL_BACKDROP_PATH = 7;
    private static final int COL_FAVORITE = 8;

    private static final String[] VIDEO_COLUMNS = {
            DBContract.VideoEntry._ID,
            DBContract.VideoEntry.COLUMN_VIDEO_ID,
            DBContract.VideoEntry.COLUMN_NAME,
            DBContract.VideoEntry.COLUMN_KEY
    };

    // These indices are tied to VIDEO_COLUMNS. If VIDEO_COLUMNS changes, these must change.
    static final int _ID = 0;
    static final int COL_VIDEO_ID = 1;
    public static final int COL_VIDEO_NAME = 2;
    public static final int COL_VIDEO_KEY = 3;

    private static final String[] REVIEW_COLUMNS = {
            DBContract.ReviewEntry._ID,
            DBContract.ReviewEntry.COLUMN_AUTHOR,
            DBContract.ReviewEntry.COLUMN_CONTENT
    };

    // These indices are tied to REVIEW_COLUMNS. If REVIEW_COLUMNS changes, these must change.
    public static final int COL_REVIEW_ID = 0;
    public static final int COL_REVIEW_AUTHOR = 1;
    public static final int COL_REVIEW_CONTENT = 2;

    private ImageView mPosterView;
    private ImageView mBackdropView;
    private TextView mTitleView;
    private TextView mReleaseDateView;
    private TextView mRatingView;
    private TextView mOverviewView;
    private CheckBox mFavorite_btn_star;
    private ListView mVideoView;

    private VideoAdapter mVideoAdatper;
    //private ReviewAdapter mReviewAdapter;

    public DetailActivityFragment() {
        setHasOptionsMenu(true);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        mPosterView = (ImageView) rootView.findViewById(R.id.posterimageView);
        mBackdropView = (ImageView) rootView.findViewById(R.id.backdropimageView);
        mTitleView = (TextView) rootView.findViewById(R.id.titleView);
        mReleaseDateView = (TextView) rootView.findViewById(R.id.yearText);
        mRatingView = (TextView) rootView.findViewById(R.id.voteAverageText);
        mOverviewView = (TextView) rootView.findViewById(R.id.synopsisText);
        mFavorite_btn_star =(CheckBox) rootView.findViewById(R.id.btn_star);
        mVideoView = (ListView) rootView.findViewById(R.id.listView_video);


        // video adapter
        mVideoAdatper = new VideoAdapter(getActivity(), null, 0);
        mVideoView.setAdapter(mVideoAdatper);

        mVideoView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
               /*
                if (cursor != null) {
                    String key = cursor.getString(COL_VIDEO_KEY);

                    if (key != null) {
                        // open video in youtube
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + key));
                        intent.putExtra("VIDEO_ID", key);
                        startActivity(intent);
                    } else {
                        Log.d("DetailActivityFragment", "video key is null");
                    }
                } else {
                    Log.d("DetailActivityFragment", "cursor is null?");
                }
                */
            }
        });


        mFavorite_btn_star.setTag(1);
        mFavorite_btn_star.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                ContentValues updateValues = new ContentValues();
                updateValues.put(DBContract.MovieEntry.COLUMN_FAVOURITE, mFavorite_btn_star.isChecked());

                Intent intent = getActivity().getIntent();
                getActivity().getContentResolver().update(
                        DBContract.MovieEntry.CONTENT_URI,
                        updateValues,
                        DBContract.MovieEntry._ID + "=?",
                        new String[] {intent.getData().getLastPathSegment()});


                if (mFavorite_btn_star.isChecked()) {
                    Toast.makeText(getActivity(), "Added to your favorite!", Toast.LENGTH_LONG).show();
                }
                else
                {
                    Toast.makeText(getActivity(), "Removed from your favorite!", Toast.LENGTH_LONG).show();
                }
            }

        });



        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        // loader movie detail from content provider
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);

        // loader movie video from content provider
        getLoaderManager().initLoader(VIDEO_LOADER, null, this);

        //if (null != mUri)
        {

            Intent intent = getActivity().getIntent();
            //Log.v("DetailActivityFragment", "onActivityCreated "+intent.getData().getLastPathSegment());
            // fetch movie video data from internet
            FetchVideosTask videoTask=new FetchVideosTask(getActivity(),this);
            videoTask.execute(intent.getData().getLastPathSegment());
        }

        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_detail, menu);

        // Retrieve the share menu item
        MenuItem menuItem = menu.findItem(R.id.action_share);

        // Get the provider and hold onto it to set/change the share intent.
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        // If onLoadFinished happens before this, we can go ahead and set the share intent now.
        if (mShareActionProvider != null && mShareVideoStr!=null)
        {
            mShareActionProvider.setShareIntent(createShareVideoIntent());
        }
    }

    private void openVideo(String strId){


        //http://api.themoviedb.org/3/movie/293660/videos?api_key=3cbbff71f9774f0a17ca0ae0bed2fc41
        String BASE_URL="http://api.themoviedb.org/3/movie/"+strId+"/videos";

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        // Will contain the raw JSON response as a string.
        String jsonStr = null;

        Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                .appendQueryParameter("api_key", BuildConfig.THE_MOVIE_DB_API_KEY)
                .build();
        try {
            URL url = new URL(builtUri.toString()); //URL url = new URL(baseUrl.concat(apiKey));
            Log.v("url", url.toString());
            // Create the request, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return;
            }
            jsonStr = buffer.toString();

        } catch (IOException e) {

            return;
        } finally {
            if (urlConnection != null) {
                //urlConnection.disconnect();
            }
            if (reader != null) {

                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.v("Error:", "Error closing stream");
                }
            }
        }

        try {
            JSONObject json = new JSONObject(jsonStr);
            JSONArray videosListArray = json.getJSONArray("results");

            List<String> videoKey= new ArrayList<>();
            for(int i = 0; i < videosListArray.length(); i++) {
                videoKey.add(videosListArray.getJSONObject(i).getString("key"));
            }

            //run video Intent
            if(!videoKey.isEmpty()){
                String youtube_url="https://www.youtube.com/watch?v="+videoKey.get(0).toString();
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(youtube_url));
                try{
                    startActivity(intent);
                }
                catch (ActivityNotFoundException ex){
                    //Log.e(TAG, "Couldn't find activity to view this video");
                }
            }

        } catch (JSONException e) {
            //Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Now create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed.

        //Log.v("DetailActivityFragment", "onCreateLoader id="+id);
        Intent intent = getActivity().getIntent();
        if (intent != null) {
            switch (id) {
                case DETAIL_LOADER:

                        //Log.v("DetailActivityFragment", "In onCreateLoader "+intent.getData().getLastPathSegment().toString());
                        // Now create and return a CursorLoader that will take care of
                        // creating a Cursor for the data being displayed.
                        Uri movieUri = DBContract.MovieEntry.CONTENT_URI;

                        return new CursorLoader(
                                getActivity(),
                                movieUri,
                                DETAIL_COLUMNS,
                                DBContract.MovieEntry._ID + "=?",
                                new String[]{intent.getData().getLastPathSegment()},
                                null
                        );
                case VIDEO_LOADER:

                    //Log.v("DetailActivityFragment", "In onCreateLoader VIDEO_LOADER "+intent.getData().getLastPathSegment().toString());
                    // Now create and return a CursorLoader that will take care of
                    // creating a Cursor for the data being displayed.
                    Uri videoUri = DBContract.VideoEntry.CONTENT_URI;

                    return new CursorLoader(
                            getActivity(),
                            videoUri,
                            VIDEO_COLUMNS,
                            DBContract.VideoEntry.COLUMN_MOVIE_ID + "=?",
                            new String[]{intent.getData().getLastPathSegment()},
                            null
                    );
                default:
                    Log.d("DetailActivityFragment", "onCreateLoader: Unknown loader id");
                    break;

            }
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if (data != null && data.moveToFirst()) {
            //Log.v("DetailActivityFragment", "onLoadFinished  COL_POSTER_PATH: "+data.getString(COL_BACKDROP_PATH));
            switch (loader.getId()) {
                case DETAIL_LOADER:
                    Picasso.with(getContext())
                            .load(data.getString(COL_POSTER_PATH))
                            .placeholder(R.drawable.placeholder)
                            .error(R.drawable.placeholder_error)
                            .into(mPosterView);


                    Picasso.with(getContext())
                            .load(data.getString(COL_BACKDROP_PATH))
                            .placeholder(R.drawable.placeholder)
                            .error(R.drawable.placeholder_error)
                            .into(mBackdropView);

                    mTitleView.setText(data.getString(COL_TITLE));
                    mOverviewView.setText(data.getString(COL_OVERVIEW));
                    mRatingView.setText(data.getString(COL_VOTE_AVERAGE) + "/10");
                    String year = data.getString(COL_RELEASE_DATE).split("-")[0];
                    mReleaseDateView.setText(year);

                    mFavorite_btn_star.setChecked(data.getInt(COL_FAVORITE) == 1);
                    break;
                case VIDEO_LOADER:

                    mVideoAdatper.swapCursor(data);
                    //mVideoAdatper.changeCursor(data);
                    //Log.v("onLoadFinished", "VIDEO_LOADER mVideoAdatper.getCount() "+ mVideoAdatper.getCount()+"  data.getCount():  "+data.getCount());
                    //mVideoView.setAdapter(mVideoAdatper);
                    //mVideoView.refreshDrawableState();
                    break;
                default:
                    Log.d("onLoadFinished", "Unknown loader id: "+ loader.getId());
                    break;
            }
            // If onCreateOptionsMenu has already happened, we need to update the share intent now.
            if (mShareActionProvider != null) {
                mShareActionProvider.setShareIntent(createShareVideoIntent());
            }

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch (loader.getId()) {
            case DETAIL_LOADER:
                break;
            case VIDEO_LOADER:
                mVideoAdatper.swapCursor(null);
                break;
            default:
                Log.d("DetailFragment", "onLoaderReset: Unknown loader id: "+ loader.getId());
                break;
        }
    }



    private Intent createShareVideoIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, mShareVideoStr);
        return shareIntent;
    }

    @Override
    public void onFetchVideoFinished() {
        getLoaderManager().restartLoader(VIDEO_LOADER, null, this);
    }
}
