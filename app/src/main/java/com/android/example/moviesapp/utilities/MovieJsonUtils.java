package com.android.example.moviesapp.utilities;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ememobong on 29/03/2017.
 */

public final class MovieJsonUtils {

    public static String[] posterPaths;
    public static String[] overviews;
    public static String[] releaseDates;
    public static String[] originalTitles;
    public static double[] voteAverages;

    private static final String RESULTS = "results";
    private static final String POSTER_PATH = "poster_path";
    private static final String OVERVIEW = "overview";
    private static final String RELEASE_DATE = "release_date";
    private static final String ORIGINAL_TITLE = "original_title";
    private static final String VOTE_AVERAGE = "vote_average";


    public static String[] getAndSetMovieDataFromJson(Context context, String movieJsonData)
            throws JSONException {

        JSONObject movieData = new JSONObject(movieJsonData);

        JSONArray resultsJsonArray = movieData.getJSONArray(RESULTS);

        setMovieDataArrayLength(resultsJsonArray.length());

        for(int i = 0; i < resultsJsonArray.length(); i++){
            posterPaths[i] = resultsJsonArray.getJSONObject(i).getString(POSTER_PATH);
            overviews[i] = resultsJsonArray.getJSONObject(i).getString(OVERVIEW);
            releaseDates[i] = resultsJsonArray.getJSONObject(i).getString(RELEASE_DATE);
            originalTitles[i] = resultsJsonArray.getJSONObject(i).getString(ORIGINAL_TITLE);
            voteAverages[i] = resultsJsonArray.getJSONObject(i).getDouble(VOTE_AVERAGE);
        }

        return posterPaths;
    }

    public static void setMovieDataArrayLength(int length){
        posterPaths = new String[length];
        overviews = new String[length];
        releaseDates = new String[length];
        originalTitles = new String[length];
        voteAverages = new double[length];
    }
}
