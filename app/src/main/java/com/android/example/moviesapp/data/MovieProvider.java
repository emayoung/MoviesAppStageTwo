package com.android.example.moviesapp.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

public class MovieProvider extends ContentProvider {


    /*
         * These constant will be used to match URIs with the data they are looking for. We will take
         * advantage of the UriMatcher class to make that matching MUCH easier than doing something
         * ourselves, such as using regular expressions.
         */
    public static final int CODE_MOVIE_ID = 100; // i would have loved to change this name to CODE_MOVIE but i've used it extensively already especially for my tests
//    this particular code will be used to
    public static final int CODE_MOVIE_FAVORITES_ID = 101;

    /*
     * The URI Matcher used by this content provider. The leading "s" in this variable name
     * signifies that this UriMatcher is a static member variable of MovieProvider and is a
     * common convention in Android programming.
     */
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    public static UriMatcher buildUriMatcher() {
/*
         * All paths added to the UriMatcher have a corresponding code to return when a match is
         * found. The code passed into the constructor of UriMatcher here represents the code to
         * return for the root URI. It's common to use NO_MATCH as the code for this case.
         */
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;

        /*
         * For each type of URI you want to add, create a corresponding code. Preferably, these are
         * constant fields in your class so that you can use them throughout the class and you no
         * they aren't going to change. In Movie Favorites, we use CODE_MOVIE_ID.
         */

        /* This URI is content://com.android.example.moviesapp/favorites/ */
        matcher.addURI(authority, MovieContract.PATH_FAVORITES, CODE_MOVIE_ID);
        matcher.addURI(authority, MovieContract.PATH_FAVORITES + "/#", CODE_MOVIE_FAVORITES_ID);

        return matcher;
    }

    private MovieDBHelper mOpenHelper;


    @Override
    public boolean onCreate() {
         /*
         * As noted in the comment above, onCreate is run on the main thread, so performing any
         * lengthy operations will cause lag in your app.
         */
        mOpenHelper = new MovieDBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
        public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                String[] selectionArgs, String sortOrder) {

            Cursor cursor;

        /*
         * Here's the switch statement that, given a URI, will determine what kind of request is
         * being made and query the database accordingly.
         */
            switch (sUriMatcher.match(uri)) {

            /*
             * When sUriMatcher's match method is called with a URI that looks EXACTLY like this
             *
             *      content://<package-name>/favorites/
             *
             * sUriMatcher's match method will return the code that indicates to us that we need
             * to return all of the weather in our weather table.
             *
             * In this case, we want to return a cursor that contains every row of weather data
             * in our weather table.
             */
                case CODE_MOVIE_ID: {
                    Log.d("TAG", "we just matched the uri for CODE_MOVIE_ID in the query method");
                    cursor = mOpenHelper.getReadableDatabase().query(
                            MovieContract.FavoritesMovieEntry.TABLE_NAME,
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

            cursor.setNotificationUri(getContext().getContentResolver(), uri);
            return cursor;
        }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        throw new RuntimeException( "We are not supporting this operation in the app");
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
         /* Users of the delete method will expect the number of rows deleted to be returned. */
        int numRowsDeleted;

        /*
         * If we pass null as the selection to SQLiteDatabase#delete, our entire table will be
         * deleted. However, if we do pass null and delete all of the rows in the table, we won't
         * know how many rows were deleted. According to the documentation for SQLiteDatabase,
         * passing "1" for the selection will delete all rows and return the number of rows
         * deleted, which is what the caller of this method expects.
         */
        if (null == selection) selection = "1";

        switch (sUriMatcher.match(uri)) {

            case CODE_MOVIE_ID:
                Log.d("TAG", "we just matched the uri for CODE_MOVIE_ID in the delete method");
                numRowsDeleted = mOpenHelper.getWritableDatabase().delete(
                        MovieContract.FavoritesMovieEntry.TABLE_NAME,
                        selection,
                        selectionArgs);

                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        /* If we actually deleted any rows, notify that a change has occurred to this URI */
        if (numRowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return numRowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        throw new RuntimeException("We are not implementing update in Sunshine");
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        switch (sUriMatcher.match(uri)) {

            case CODE_MOVIE_ID:
                Log.d("TAG", "we just matched the uri for CODE_MOVIE_ID in the bulk insert method");
                db.beginTransaction();
                int rowsInserted = 0;
                try {
                    for (ContentValues value : values) {
//check for any values that you would love to be in a specified format
// like checking that the year has been formated properly we could also check if the year has a length if 4
                        if ((value.get(MovieContract.FavoritesMovieEntry.COLUMN_RELEASE_YEAR).toString()).length() < 4) {
                            throw new IllegalArgumentException("Date must be normalized to insert");
                        }
                        long _id = db.insert(MovieContract.FavoritesMovieEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            rowsInserted++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }

                if (rowsInserted > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }

                return rowsInserted;

            default:
                return super.bulkInsert(uri, values);
        }
    }

    /**
     * This is a method specifically to assist the testing
     * framework in running smoothly. You can read more at:
     */
    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }

}
