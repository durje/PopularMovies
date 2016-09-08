/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.popularmovies.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

public class DBProvider extends ContentProvider {

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private DbHelper mOpenHelper;

    static final int MOVIE = 100;
    static final int MOVIE_VIDEO = 101;
    static final int MOVIE_REVIEW = 102;
    static final int MOVIE_ID= 103;
    static final int VIDEO = 200;
    static final int REVIEW = 300;

    /*
        This UriMatcher will match each URI to the MVOIE, integer constants defined above.
        You can test this by uncommenting the testUriMatcher test within TestUriMatcher.
     */
    static UriMatcher buildUriMatcher() {
        // I know what you're thinking.  Why create a UriMatcher when you can use regular
        // expressions instead?  Because you're not crazy, that's why.

        // All paths added to the UriMatcher have a corresponding code to return when a match is
        // found.  The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

        final String authority = DBContract.CONTENT_AUTHORITY;

        // For each type of URI you want to add, create a corresponding code.
        // /* String (location,...)  /# number (date,..)
        matcher.addURI(authority, DBContract.PATH_MOVIE, MOVIE);
        matcher.addURI(authority, DBContract.PATH_MOVIE + "/#", MOVIE_ID);
        matcher.addURI(authority, DBContract.PATH_MOVIE + "/*" + DBContract.PATH_VIDEO, MOVIE_VIDEO);
        matcher.addURI(authority, DBContract.PATH_MOVIE + "/*" + DBContract.PATH_REVIEW, MOVIE_REVIEW);
        matcher.addURI(authority, DBContract.PATH_VIDEO, VIDEO);
        matcher.addURI(authority, DBContract.PATH_REVIEW, REVIEW);

        return matcher;
    }

    /*
       create a new DbHelper for later use here.
     */
    @Override
    public boolean onCreate() {
        //getContext().deleteDatabase("movie.db");
        mOpenHelper = new DbHelper(getContext());
        return true;
    }

    /*
        getType function that uses the UriMatcher.  You can
        test this by uncommenting testGetType in TestProvider.

     */

    @Override
    public String getType(Uri uri) {

        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case MOVIE:
                return DBContract.MovieEntry.CONTENT_TYPE;
            case MOVIE_ID:
                return DBContract.MovieEntry.CONTENT_TYPE;
            case MOVIE_VIDEO:
                return DBContract.VideoEntry.CONTENT_TYPE;
            case MOVIE_REVIEW:
                return DBContract.ReviewEntry.CONTENT_TYPE;
            case VIDEO:
                return DBContract.VideoEntry.CONTENT_TYPE;
            case REVIEW:
                return DBContract.ReviewEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Here's the switch statement that, given a URI, will determine what kind of request it is,
        // and query the database accordingly.
        Cursor retCursor=null;
        //Log.d("DBProvider", "******query uri: " + uri);
        switch (sUriMatcher.match(uri)) {
            // "movie"
            case MOVIE: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        DBContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                if(selectionArgs!=null)
                {
                    //Log.d("DBProvider", "MOVIE uri: " + uri+"  "+selectionArgs[0]);
                }

                break;
            }
            case MOVIE_ID: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        DBContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                Log.d("DBProvider", "MOVIE_ID uri: " + uri);
                break;
            }
            // movie video
            case MOVIE_VIDEO:
            {
                long id = DBContract.VideoEntry.getMovieIdFromUri(uri);

                retCursor = mOpenHelper.getReadableDatabase().query(
                        DBContract.VideoEntry.TABLE_NAME,
                        projection,
                        DBContract.VideoEntry.COLUMN_MOVIE_ID + "=?",
                        new String[] {Long.toString(id)},
                        null,
                        null,
                        sortOrder);
                break;
            }
            // movie review
            case MOVIE_REVIEW:
            {
                long id = DBContract.ReviewEntry.getMovieIdFromUri(uri);

                retCursor = mOpenHelper.getReadableDatabase().query(
                        DBContract.ReviewEntry.TABLE_NAME,
                        projection,
                        DBContract.ReviewEntry.COLUMN_MOVIE_ID + "=?",
                        new String[] {Long.toString(id)},
                        null,
                        null,
                        sortOrder);
                break;
            }
            // video
            case VIDEO:
            {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        DBContract.VideoEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }
            // review
            case REVIEW:
            {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        DBContract.ReviewEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri=null;

        switch (match) {

            case MOVIE: {
                // default favorite to 'no'
                //values.put(DBContract.MovieEntry.COLUMN_FAVOURITE, 0);

                long _id = db.insert(DBContract.MovieEntry.TABLE_NAME, null, values);

                if ( _id > 0 ) {
                    returnUri = DBContract.MovieEntry.buildMovieUri(_id);
                }
                else
                {
                    Log.d("DBProvider ", "Failed to insert row into: "+ uri);
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            // video
            case VIDEO: {
                long _id = db.insert(DBContract.VideoEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = DBContract.VideoEntry.buildVideoUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            // review
            case REVIEW: {
                long _id = db.insert(DBContract.ReviewEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = DBContract.ReviewEntry.buildReviewUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);

        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }


    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted=0;
        // this makes delete all rows return the number of rows deleted
        if ( null == selection ) selection = "1";
        switch (match) {
            case MOVIE:
                rowsDeleted = db.delete(
                        DBContract.MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            // video
            case VIDEO: {
                rowsDeleted = db.delete(DBContract.VideoEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            // review
            case REVIEW: {
                rowsDeleted = db.delete(DBContract.ReviewEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }



    @Override
    public int update(
            Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated=0;

        switch (match) {
            case MOVIE:
                rowsUpdated = db.update(DBContract.MovieEntry.TABLE_NAME, values, selection, selectionArgs);
                Log.d("DBProvider", "update uri: " + uri);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            // video
            case VIDEO: {
                rowsUpdated = db.update(DBContract.VideoEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            }
            // review
            case REVIEW: {
                rowsUpdated = db.update(DBContract.ReviewEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            }
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }


    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int returnCount = 0;

        switch (match) {
            case MOVIE:
                db.beginTransaction();

                try {
                    for (ContentValues value : values) {
                        // First, check if the movie exists in the db
                        String key=value.get("_id").toString();

                        // First, check if the movie exists in the db
                        Cursor movieCursor = this.query(
                                DBContract.MovieEntry.CONTENT_URI,
                                new String[]{DBContract.MovieEntry._ID},
                                DBContract.MovieEntry._ID + " = ?" ,
                                new String[]{key},
                                null);

                        if (movieCursor.moveToFirst()) {
                        /*
                            int movieIdIndex = movieCursor.getColumnIndex(DBContract.MovieEntry._ID);
                            long movieId = movieCursor.getLong(movieIdIndex);
                            Log.v("bulkInsert EXIST "," ID: "+  movieId);*/
                        } else
                        {
                            //Log.d("bulkInsert", value.get("_id").toString());

                            long _id = db.insert(DBContract.MovieEntry.TABLE_NAME, null, value);
                            //Log.d("DBProvider ", "bulkInsert "+ _id);
                            if (_id != -1) {
                                returnCount++;
                            }
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            // video
            case VIDEO:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        // First, check if the video exists in the db
                        Cursor videoCursor = this.query(
                                DBContract.VideoEntry.CONTENT_URI,
                                new String[]{DBContract.VideoEntry.COLUMN_VIDEO_ID},
                                DBContract.VideoEntry.COLUMN_VIDEO_ID + " = ?" ,
                                new String[]{value.get(DBContract.VideoEntry.COLUMN_VIDEO_ID).toString()},
                                null);

                        if (!videoCursor.moveToFirst()) {

                            long key = db.insert(DBContract.VideoEntry.TABLE_NAME, null, value);
                            if (key != -1) {
                                returnCount++;
                            }
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            // review
            case REVIEW:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        // First, check if the review exists in the db
                        Cursor reviewCursor = this.query(
                                DBContract.ReviewEntry.CONTENT_URI,
                                new String[]{DBContract.ReviewEntry.COLUMN_REVIEW_ID},
                                DBContract.ReviewEntry.COLUMN_REVIEW_ID + " = ?" ,
                                new String[]{value.get(DBContract.ReviewEntry.COLUMN_REVIEW_ID).toString()},
                                null);

                        if (!reviewCursor.moveToFirst()) {
                            long _id = db.insert(DBContract.ReviewEntry.TABLE_NAME, null, value);
                            if (_id != -1) {
                                returnCount++;
                            }
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    // You do not need to call this method. This is a method specifically to assist the testing
    // framework in running smoothly. You can read more at:
    // http://developer.android.com/reference/android/content/ContentProvider.html#shutdown()
    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }
}