package com.android.example.moviesapp.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by ememobong on 08/05/2017.
 */

public class MovieContract {

    /*
    * The "Content authority" is a name for the entire content provider, similar to the
    * relationship between a domain name and its website. A convenient string to use for the
    * content authority is the package name for the app, which is guaranteed to be unique on the
    * Play Store.
    */
    public static final String CONTENT_AUTHORITY = "com.android.example.moviesapp";

    /*
     * Using CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     * the content provider for Sunshine.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /*
         * Possible paths that can be appended to BASE_CONTENT_URI to form valid URI's that Sunshine
         * can handle. For instance,
         *
         *     content://com.example.android.sunshine/weather/
         *     [           BASE_CONTENT_URI         ][ PATH_WEATHER ]
         *
         * is a valid path for looking at weather data.
         *
         *      content://com.example.android.sunshine/givemeroot/
         *
         * will fail, as the ContentProvider hasn't been given any information on what to do with
         * "givemeroot". At least, let's hope not. Don't be that dev, reader. Don't be that dev.
         */
    public static final String PATH_FAVORITES = "favorites";


    public static class FavoritesMovieEntry implements BaseColumns{

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_FAVORITES)
                .build();

        /* Used internally as the name of our movie table. */
        public static final String TABLE_NAME = "favorites";

        public static final String COLUMN_MOVIE_ID = "movie_id";

        /* movie id used to fetch the trailers  */
        public static final String COLUMN_MOVIE_TITLE = "movie_title";

        /* movie poster link from api and short synopsis of the movie */
        public static final String COLUMN_MOVIE_POSTER = "movie_poster";
        public static final String COLUMN_MOVIE_SYNOPSIS = "movie_synopsis";

        /* user rating of the particular movie with highest rating of 10 */
        public static final String COLUMN_USER_RATING = "user_rating";

        /* year movie was released */
        public static final String COLUMN_RELEASE_YEAR = "release_year";

        public static final String COLUMN_BACKDROP = "back_drop";

        /**
         * Builds a URI that appends a movie id to the favorites and which we can use to query the database
         *
         * @param id
         * @return Uri to query single movie entry
         */
        public static Uri buildMovieUriWithID(long id) {
            return CONTENT_URI.buildUpon()
                    .appendPath(Long.toString(id))
                    .build();
        }

    }
}
