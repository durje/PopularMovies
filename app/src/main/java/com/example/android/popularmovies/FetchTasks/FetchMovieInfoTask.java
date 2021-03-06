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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Vector;

/**
 * Created by Jerome Durand on 15/03/2016.
 *

 */
public class FetchMovieInfoTask extends AsyncTask<String, Void, String> {

    private final String LOG_TAG = FetchMovieInfoTask.class.getSimpleName();

    private final Context mContext;

    public FetchMovieInfoTask(Context context)
    {
        mContext = context;
    }



    /**
     * Helper method to handle insertion of a new movie in the movie database.
     *
     * @param

     * @return the row ID of the added movie.
     */
/*
    long addMovieToDB(Movie aMovie ) {

        long movieId;

        // First, check if the movie exists in the db
        Cursor movieCursor = mContext.getContentResolver().query(
                DBContract.MovieEntry.CONTENT_URI,
                new String[]{DBContract.MovieEntry._ID},
                DBContract.MovieEntry._ID + " = ?" ,
                new String[]{aMovie.getId()},
                null);


        if (movieCursor.moveToFirst()) {
            int movieIdIndex = movieCursor.getColumnIndex(DBContract.MovieEntry._ID);
            movieId = movieCursor.getLong(movieIdIndex);
            //Log.v("addMovieToDB EXIST "," ID: "+  movieId);
        } else {
            // Now that the content provider is set up, inserting rows of data is pretty simple.
            // First create a ContentValues object to hold the data you want to insert.
            ContentValues movieValues = new ContentValues();

            // Then add the data, along with the corresponding name of the data type,
            // so the content provider knows what kind of value is being inserted.
            movieValues.put(DBContract.MovieEntry._ID, aMovie.getId());
            movieValues.put(DBContract.MovieEntry.COLUMN_TITLE, aMovie.getTitle());
            movieValues.put(DBContract.MovieEntry.COLUMN_RELEASE_DATE, aMovie.getReleaseDate());
            movieValues.put(DBContract.MovieEntry.COLUMN_VOTE_AVERAGE, aMovie.getVoteAverage());
            movieValues.put(DBContract.MovieEntry.COLUMN_OVERVIEW, aMovie.getOverview());
            movieValues.put(DBContract.MovieEntry.COLUMN_POSTER_PATH, aMovie.getPosterPath());
            movieValues.put(DBContract.MovieEntry.COLUMN_BACKDROP_PATH, aMovie.getBackdropPath());
            movieValues.put(DBContract.MovieEntry.COLUMN_FAVOURITE, "0");

            // Finally, insert movie data into the database.
            Uri insertedUri = mContext.getContentResolver().insert(
                    DBContract.MovieEntry.CONTENT_URI,
                    movieValues
            );


            // The resulting URI contains the ID for the row.  Extract the locationId from the Uri.
            movieId = ContentUris.parseId(insertedUri);

            //Log.v("addMovieToDB new "," ID: "+  movieId);
        }

        movieCursor.close();
        // Wait, that worked?  Yes!
        return movieId;
    }
*/
    /**
     * Take the String representing the complete JSON Format and
     * pull out the data we need to construct the Strings needed for the wireframes.
     *
     * Fortunately parsing is easy:  constructor takes the JSON string and converts it
     * into an Object hierarchy for us.
     */
    private void getDataFromJson(String jsonStr) throws JSONException
    {
        // These are the names of the JSON objects that need to be extracted.
        final String TMDB_RESULTS = "results";
        final String TMDB_POSTER_PATH = "poster_path";
        final String TMDB_TITLE = "title";
        final String base_path="http://image.tmdb.org/t/p/w185";


        try{

            JSONObject json = new JSONObject(jsonStr);
            JSONArray movieListArray = json.getJSONArray(TMDB_RESULTS);

            // Insert the new movie information into the database
            Vector<ContentValues> cVector = new Vector<ContentValues>(movieListArray.length());
            //Log.d(LOG_TAG, "movieListArray.length():  " + movieListArray.length() );
            for(int i = 0; i < movieListArray.length(); i++) {

                // Get the JSON object representing the movie
                JSONObject movieJson = movieListArray.getJSONObject(i);

                ContentValues movieValues = new ContentValues();

                // Then add the data, along with the corresponding name of the data type,
                // so the content provider knows what kind of value is being inserted.
                movieValues.put(DBContract.MovieEntry._ID, movieJson.getString("id"));
                movieValues.put(DBContract.MovieEntry.COLUMN_TITLE, movieJson.getString(TMDB_TITLE));
                movieValues.put(DBContract.MovieEntry.COLUMN_ORIGINAL_TITLE, movieJson.getString("original_title"));
                movieValues.put(DBContract.MovieEntry.COLUMN_OVERVIEW, movieJson.getString("overview"));
                movieValues.put(DBContract.MovieEntry.COLUMN_RELEASE_DATE, movieJson.getString("release_date"));
                movieValues.put(DBContract.MovieEntry.COLUMN_ADULT, movieJson.getString("adult"));
                movieValues.put(DBContract.MovieEntry.COLUMN_VIDEO, movieJson.getString("video"));
                movieValues.put(DBContract.MovieEntry.COLUMN_POSTER_PATH, base_path + movieJson.getString(TMDB_POSTER_PATH));
                movieValues.put(DBContract.MovieEntry.COLUMN_BACKDROP_PATH, base_path + movieJson.getString("backdrop_path"));

                movieValues.put(DBContract.MovieEntry.COLUMN_POPULARITY, movieJson.getString("popularity"));
                movieValues.put(DBContract.MovieEntry.COLUMN_VOTE_AVERAGE, movieJson.getString("vote_average"));
                movieValues.put(DBContract.MovieEntry.COLUMN_VOTE_COUNT, movieJson.getString("vote_count"));

                movieValues.put(DBContract.MovieEntry.COLUMN_FAVOURITE, "0");
                cVector.add(movieValues);
                //mContext.getContentResolver().insert(DBContract.MovieEntry.CONTENT_URI,movieValues);
            }

            int inserted = 0;
            // add to database
            if ( cVector.size() > 0 ) {
                ContentValues[] cvArray = new ContentValues[cVector.size()];
                cVector.toArray(cvArray);
                // Finally, insert movies data into the database.
                inserted = mContext.getContentResolver().bulkInsert(DBContract.MovieEntry.CONTENT_URI, cvArray);
/*
                // delete old data so we don't build up an endless history
                mContext.getContentResolver().delete(DBContract.MovieEntry.CONTENT_URI,
                        WeatherContract.WeatherEntry.COLUMN_DATE + " <= ?",
                        new String[] {Long.toString(dayTime.setJulianDay(julianStartDay-1))});

                notifyWeather();*/
            }

            // Students: Uncomment the next lines to display what you stored in the bulkInsert
            Cursor cur = mContext.getContentResolver().query(DBContract.MovieEntry.CONTENT_URI,
                    null, null, null, null);

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
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

    }

    @Override
    protected String doInBackground(String... params) {

        String jsonStr = null;
        // https://api.themoviedb.org/3/movie/popular?api_key=3cbbff71f9774f0a17ca0ae0bed2fc41
        // Construct the URL for the THE_MOVIE_DB query
        final String BASE_URL = "https://api.themoviedb.org/3/movie/";
        String sortOrder=params[0];
       /* String sortOrder="top_rated";
        if(params[0].equals("popular"))
        {
            sortOrder="popular";
        }else //if(params[0].equals("top_rated"))
        {
            //sortOrder="top_rated";
        }*/
        if(!sortOrder.equals("favorite")) {
            Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                    .appendPath(sortOrder)
                    .appendQueryParameter("api_key", BuildConfig.THE_MOVIE_DB_API_KEY)
                    .build();
            try {

                URL url = new URL(builtUri.toString()); //URL url = new URL(baseUrl.concat(apiKey));
                //Log.v(LOG_TAG, " builtUri url "+url.toString());
                jsonStr = downloadJson(url);
                //Log.v(LOG_TAG, "jsonStr "+jsonStr);

            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        return jsonStr;
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
            Log.d("downloadJson", "Error: " +url.toString(), e);
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

        try {
            if(jsonStr!=null) {
                getDataFromJson(jsonStr);
            }
            else
            {
                Log.e(LOG_TAG, "onPostExecute jsonStr null");
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

    }

}