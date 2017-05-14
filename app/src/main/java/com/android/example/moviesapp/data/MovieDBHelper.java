package com.android.example.moviesapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.android.example.moviesapp.data.MovieContract.FavoritesMovieEntry;

/**
 * Created by ememobong on 08/05/2017.
 */

public class MovieDBHelper extends SQLiteOpenHelper {

    /**
     *TODO: You will need to update this version of the database before submitting this project
     */
    public static final String DATABASE_NAME = "favorites.db";
    private static final int DATABASE_VERSION = 2;


    public MovieDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_FAVORITES_TABLE =

                "CREATE TABLE " + FavoritesMovieEntry.TABLE_NAME + " (" +

                        FavoritesMovieEntry._ID               + " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                        FavoritesMovieEntry.COLUMN_MOVIE_ID       + " REAL NOT NULL, "                 +

                        FavoritesMovieEntry.COLUMN_MOVIE_TITLE + " TEXT NOT NULL,"                  +

                        FavoritesMovieEntry.COLUMN_MOVIE_POSTER   + " TEXT NOT NULL, "                    +
                        FavoritesMovieEntry.COLUMN_MOVIE_SYNOPSIS   + " TEXT NOT NULL, "                    +

                        FavoritesMovieEntry.COLUMN_RELEASE_YEAR   + " REAL NOT NULL, "                    +
                        FavoritesMovieEntry.COLUMN_USER_RATING   + " STRING NOT NULL, "                    +
                        FavoritesMovieEntry.COLUMN_BACKDROP   + " STRING NOT NULL, "                    +
                /*
                 * To ensure that there is only one movie id stored in the database at a time
                 */
                        " UNIQUE (" + FavoritesMovieEntry.COLUMN_MOVIE_ID + ") ON CONFLICT REPLACE);";

        /*
         * After we've spelled out our SQLite table creation statement above, we actually execute
         * that SQL with the execSQL method of our SQLite database object.
         */
        sqLiteDatabase.execSQL(SQL_CREATE_FAVORITES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldversion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FavoritesMovieEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
