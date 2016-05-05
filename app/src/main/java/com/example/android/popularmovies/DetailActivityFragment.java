package com.example.android.popularmovies;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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
public class DetailActivityFragment extends Fragment {

    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        Intent intent = getActivity().getIntent();
        if (intent != null) {
            if (intent.hasExtra("poster_path")) {
                String imageStr = intent.getStringExtra("poster_path");
                ImageView imageView = (ImageView) rootView.findViewById(R.id.posterimageView);
                Picasso.with(getContext())
                        .load(imageStr)
                        .placeholder(R.drawable.placeholder)
                        .error(R.drawable.placeholder_error)
                        .into(imageView);

            }
            if (intent.hasExtra("title")) {
                String titleStr = intent.getStringExtra("title");
                TextView titleView = (TextView) rootView.findViewById(R.id.titleView);
                titleView.setText(titleStr);
            }
            if (intent.hasExtra("overview")) {
                String titleStr = intent.getStringExtra("overview");
                TextView titleView = (TextView) rootView.findViewById(R.id.synopsisText);
                titleView.setText(titleStr);
            }
            if (intent.hasExtra("vote_average")) {
                String titleStr = intent.getStringExtra("vote_average")+"/10";
                TextView titleView = (TextView) rootView.findViewById(R.id.voteAverageText);
                titleView.setText(titleStr);
            }
            if (intent.hasExtra("release_date")) {
                String titleStr = intent.getStringExtra("release_date");
                TextView titleView = (TextView) rootView.findViewById(R.id.yearText);
                titleView.setText(titleStr);
            }
            if (intent.hasExtra("movieId")) {
                String movieId = intent.getStringExtra("movieId");
               // openVideo(movieId);
            }

        }
        return rootView;
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
}
