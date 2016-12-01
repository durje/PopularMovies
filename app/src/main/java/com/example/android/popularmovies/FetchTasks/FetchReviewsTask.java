package com.example.android.popularmovies.FetchTasks;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.example.android.popularmovies.BuildConfig;
import com.example.android.popularmovies.data.DBContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

/**
 * Created by Jerome Durand on 08/09/2016.
 */
public class FetchReviewsTask extends AsyncTask<String, Void, String> {

    private final String LOG_TAG = FetchReviewsTask.class.getSimpleName();

    private final Context mContext;
    private long mMovieId;


    public interface Callback {
        void onFetchVideoFinished();
    }

    public FetchReviewsTask(Context context)
    {
        mContext = context;
    }


    @Override
    protected String doInBackground(String... params) {

        if (params.length == 0)
            return null;

        mMovieId = Long.parseLong(params[0]);
        //Log.v(LOG_TAG, "doInBackground mMovieId: "+mMovieId);
        //http://api.themoviedb.org/3/movie/264644/videos?api_key=xxxxx
        final String BASE_URL = "http://api.themoviedb.org/3/movie";
        final String VIDEOS_PATH = "reviews";
        Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                .appendPath(Long.toString(mMovieId))
                .appendPath(VIDEOS_PATH)
                .appendQueryParameter("api_key", BuildConfig.THE_MOVIE_DB_API_KEY)
                .build();

        String jsonStr = null;

        try{
            URL url = new URL(builtUri.toString());
            //Log.v(LOG_TAG, "doInBackground mMovieId url: "+url.toString());
            jsonStr = downloadJson(url);

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            e.printStackTrace();
        }
        return jsonStr;
    }

    public void getReviewDataFromJson(String jsonStr) {

        // These are the names of the JSON objects that need to be extracted.
        final String OWN_RESULTS = "results";
        final String REVIEW_ID = "id";
        final String OWN_AUTHOR = "author";
        final String OWN_CONTENT = "content";
        final String OWN_URL = "url";

        try {
            // get the root object from JSON string
            JSONObject jsonObject = new JSONObject(jsonStr);
            JSONArray reviewListArray = jsonObject.getJSONArray(OWN_RESULTS);

            Vector<ContentValues> cVector = new Vector<ContentValues>(reviewListArray.length());


            for (int i = 0; i < reviewListArray.length(); i++) {
                // get the review data from results list
                JSONObject jsonVideoObject = reviewListArray.getJSONObject(i);

                ContentValues videoValues = new ContentValues();
                //videoValues.put(DBContract.VideoEntry._ID, jsonVideoObject.getString(VIDEO_ID));
                videoValues.put(DBContract.ReviewEntry.COLUMN_REVIEW_ID, jsonVideoObject.getString(REVIEW_ID));
                videoValues.put(DBContract.ReviewEntry.COLUMN_MOVIE_ID, String.valueOf(mMovieId));
                videoValues.put(DBContract.ReviewEntry.COLUMN_AUTHOR, jsonVideoObject.getString(OWN_AUTHOR));
                videoValues.put(DBContract.ReviewEntry.COLUMN_CONTENT, jsonVideoObject.getString(OWN_CONTENT));
                videoValues.put(DBContract.ReviewEntry.COLUMN_URL, jsonVideoObject.getString(OWN_URL));


                //Log.v(LOG_TAG, "Review: " +(i+1)+"  Author"+ jsonVideoObject.getString(OWN_AUTHOR));
                cVector.add(videoValues);

            }

            int inserted = 0;
            // add to database
            if ( cVector.size() > 0 ) {
                ContentValues[] cvArray = new ContentValues[cVector.size()];
                cVector.toArray(cvArray);
                // Finally, insert movies data into the database.
                inserted = mContext.getContentResolver().bulkInsert(DBContract.ReviewEntry.CONTENT_URI, cvArray);
/*
                // delete old data so we don't build up an endless history
                mContext.getContentResolver().delete(DBContract.MovieEntry.CONTENT_URI,
                        WeatherContract.WeatherEntry.COLUMN_DATE + " <= ?",
                        new String[] {Long.toString(dayTime.setJulianDay(julianStartDay-1))});

                notifyWeather();*/
            }

            // Students: Uncomment the next lines to display what you stored in the bulkInsert
            //Cursor cur = mContext.getContentResolver().query(DBContract.VideoEntry .CONTENT_URI, null, null, null, null);
            Cursor cur = mContext.getContentResolver().query(
                    DBContract.ReviewEntry.CONTENT_URI,
                    null,
                    DBContract.ReviewEntry.COLUMN_MOVIE_ID + " = ?" ,
                    new String[]{String.valueOf(mMovieId)},
                    null);

            cVector = new Vector<ContentValues>(cur.getCount());
            if ( cur.moveToFirst() ) {
                do {
                    ContentValues cv = new ContentValues();
                    DatabaseUtils.cursorRowToContentValues(cur, cv);
                    cVector.add(cv);
                    //Log.d(LOG_TAG, "cv. " + cv );
                } while (cur.moveToNext());
            }
            cur.close();

            //Log.d(LOG_TAG, "Complete. " + cVector.size()+ "    Inserted:" +inserted );

        } catch (JSONException e) {
            Log.d(LOG_TAG, "JSON Error", e);
        }
    }


    public static String downloadJson(URL url) {
        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        // Will contain the raw JSON response as a string.
        String jsonStr = null;

        try {

            // Create the request, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
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
                return null;
            }

            jsonStr = buffer.toString();

        } catch (IOException e) {
            Log.d("downloadJson", "Error", e);
            return null;
        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();

            if (reader != null)
                try {
                    reader.close();
                } catch (Exception e) {
                    Log.e("downloadJson", "Error closing stream", e);
                }
        }
        return jsonStr;
    }

    @Override
    protected void onPostExecute(String jsonStr) {
        super.onPostExecute(jsonStr);

        if(jsonStr!=null) {
            getReviewDataFromJson(jsonStr);
        }
        else
        {
            Log.e(LOG_TAG, "onPostExecute jsonStr null");
        }
        //Log.v(LOG_TAG, "onPostExecute:  jsonStr"+ jsonStr);
        // notify the callback data changed!!
        //mCallback.onFetchReviewFinished();
    }
}
