package com.example.android.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Jerome Durand on 16/03/2016.
 */
public class Movie implements Parcelable {

    private String id;
    private String title;
    private String overview;
    private String releaseDate;
    private String voteAverage;
    private String posterPath;
    private String backdropPath;

    public Movie(){

    }

    public String getId()
    {
        return  id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getTitle()
    {
        return  title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getOverview()
    {
        return  overview;
    }

    public void setOverview(String overview)
    {
        this.overview = overview;
    }

    public String getReleaseDate()
    {
        return  releaseDate;
    }

    public void setReleaseDate(String releaseDate)
    {
        this.releaseDate = releaseDate;
    }

    public String getVoteAverage()
    {
        return  voteAverage;
    }

    public void setVoteAverage(String voteAverage)
    {
        this.voteAverage = voteAverage;
    }

    public String getPosterPath()
    {
        return  posterPath;
    }

    public void setPosterPath(String posterPath)
    {
        this.posterPath = posterPath;
    }

    public String getBackdropPath()
    {
        return  backdropPath;
    }

    public void setBackdropPath(String backdropPath)
    {
        this.backdropPath = backdropPath;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(overview);
        dest.writeString(releaseDate);
        dest.writeString(voteAverage);
        dest.writeString(posterPath);
        dest.writeString(backdropPath);
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>()
    {
        @Override
        public Movie createFromParcel(Parcel source)
        {
            return new Movie(source);
        }

        @Override
        public Movie[] newArray(int size)
        {
            return new Movie[size];
        }
    };

    public Movie(Parcel in){
        this.id = in.readString();
        this.title = in.readString();
        this.overview = in.readString();
        this.releaseDate = in.readString();
        this.voteAverage = in.readString();
        this.posterPath = in.readString();
        this.backdropPath = in.readString();
    }
}
