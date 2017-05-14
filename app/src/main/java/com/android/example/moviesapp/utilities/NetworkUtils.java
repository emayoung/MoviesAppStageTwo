package com.android.example.moviesapp.utilities;

import android.net.Uri;
import android.support.compat.BuildConfig;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by ememobong on 29/03/2017.
 */

public final class NetworkUtils {

    private static final String MOVIE_BASE_URL = "http://api.themoviedb.org/3/movie";
    private static final String TRAILER_PATH = "videos";
    private static final String REVIEWS_PATH = "reviews";
    private static final String API_KEY_PARAM = "api_key";

    private static final String API_KEY = com.android.example.moviesapp.BuildConfig.API_KEY;

    public static URL buildUrl(String userMoviePreference){

        Uri.Builder builtUrl = Uri.parse(MOVIE_BASE_URL).buildUpon();
        builtUrl.appendPath(userMoviePreference);
        builtUrl.appendQueryParameter(API_KEY_PARAM, API_KEY).build();

        URL url = null;
        try {
            url = new URL(builtUrl.toString());
            return url;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;

    }
    public static URL buildTrailerUrl(String id){
        Uri.Builder builtUrl = Uri.parse(MOVIE_BASE_URL).buildUpon();
        builtUrl.appendPath(id);
        builtUrl.appendPath(TRAILER_PATH);
        builtUrl.appendQueryParameter(API_KEY_PARAM, API_KEY).build();

        URL url = null;
        try {
            url = new URL(builtUrl.toString());
            return url;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }
    public static URL buildReviewsUrl(String id){
        Uri.Builder builtUrl = Uri.parse(MOVIE_BASE_URL).buildUpon();
        builtUrl.appendPath(id);
        builtUrl.appendPath(REVIEWS_PATH);
        builtUrl.appendQueryParameter(API_KEY_PARAM, API_KEY).build();

        URL url = null;
        try {
            url = new URL(builtUrl.toString());
            return url;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    public static String getNetworkResponseFromServer(URL url)  throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

}
