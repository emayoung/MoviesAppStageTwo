package com.android.example.moviesapp.databaseSync;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.android.example.moviesapp.R;
import com.android.example.moviesapp.data.MovieContract;

public class FavoritesMoviesIntentService extends IntentService {

    public FavoritesMoviesIntentService () {
        super("FavoritesMoviesIntentService");
    }
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

//        get the intent data from the intent that started this service
        if (intent != null && intent.hasExtra(this.getString(R.string.movie_id))){
//            write the values from the intent to database

            final String COLUMN_MOVIE_ID = intent.getStringExtra(this.getString(R.string.movie_id_backup));;
            final String COLUMN_MOVIE_TITLE = intent.getStringExtra(this.getString(R.string.movie_title));
            final String COLUMN_MOVIE_POSTER = intent.getStringExtra(this.getString(R.string.movie_poster));
            final String COLUMN_MOVIE_SYNOPSIS = intent.getStringExtra(this.getString(R.string.movie_synopsis));
            final String COLUMN_USER_RATING = intent.getStringExtra(this.getString(R.string.user_rating));
            final String COLUMN_RELEASE_YEAR = intent.getStringExtra(this.getString(R.string.release_year));
            final String COLUMN_BACKDROP = intent.getStringExtra(this.getString(R.string.backdrop));

            ContentValues movieValues = new ContentValues();

            movieValues.put(MovieContract.FavoritesMovieEntry.COLUMN_MOVIE_ID, Long.valueOf(COLUMN_MOVIE_ID));
            movieValues.put(MovieContract.FavoritesMovieEntry.COLUMN_USER_RATING, COLUMN_USER_RATING);
            movieValues.put(MovieContract.FavoritesMovieEntry.COLUMN_MOVIE_SYNOPSIS, COLUMN_MOVIE_SYNOPSIS);
            movieValues.put(MovieContract.FavoritesMovieEntry.COLUMN_RELEASE_YEAR, Long.valueOf(COLUMN_RELEASE_YEAR));
            movieValues.put(MovieContract.FavoritesMovieEntry.COLUMN_MOVIE_TITLE, COLUMN_MOVIE_TITLE);
            movieValues.put(MovieContract.FavoritesMovieEntry.COLUMN_MOVIE_POSTER, COLUMN_MOVIE_POSTER);
            movieValues.put(MovieContract.FavoritesMovieEntry.COLUMN_BACKDROP, COLUMN_BACKDROP);

            ContentValues simulateBulkInsertValues[] = new ContentValues[]{movieValues};

            int insertCount = getContentResolver().bulkInsert(
                /* URI at which to insert data */
                    MovieContract.FavoritesMovieEntry.CONTENT_URI,
                    simulateBulkInsertValues);

            if (insertCount != -1){
                stopSelf();
            }
        }
    }
}
