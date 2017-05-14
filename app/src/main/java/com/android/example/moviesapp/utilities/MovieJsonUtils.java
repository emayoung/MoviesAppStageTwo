package com.android.example.moviesapp.utilities;

import android.content.Context;
import android.util.Log;

import com.android.example.moviesapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by ememobong on 29/03/2017.
 */

public final class MovieJsonUtils {

    private static long[] ids;
    private static String[] posterPaths;
    private static String[] overviews;
    private static String[] releaseDates;
    private static String[] originalTitles;
    private static double[] voteAverages;
    private static String[] backdrops;

    private static final String RESULTS = "results";
    private static final String MOVIE_ID = "id";
    private static final String POSTER_PATH = "poster_path";
    private static final String OVERVIEW = "overview";
    private static final String RELEASE_DATE = "release_date";
    private static final String ORIGINAL_TITLE = "original_title";
    private static final String VOTE_AVERAGE = "vote_average";
    private static final String BACKDROP = "backdrop_path";


    static Movies[] movies;


    public static Movies[] getAndSetMovieDataFromJson(Context context, String movieJsonData)
            throws JSONException {

        JSONObject movieData = new JSONObject(movieJsonData);

        JSONArray resultsJsonArray = movieData.getJSONArray(RESULTS);

        setMovieDataArrayLength(resultsJsonArray.length());

        for(int i = 0; i < resultsJsonArray.length(); i++){
            ids[i] = resultsJsonArray.getJSONObject(i).getLong(MOVIE_ID);
            posterPaths[i] = resultsJsonArray.getJSONObject(i).getString(POSTER_PATH);
            overviews[i] = resultsJsonArray.getJSONObject(i).getString(OVERVIEW);
            releaseDates[i] = resultsJsonArray.getJSONObject(i).getString(RELEASE_DATE);
            originalTitles[i] = resultsJsonArray.getJSONObject(i).getString(ORIGINAL_TITLE);
            voteAverages[i] = resultsJsonArray.getJSONObject(i).getDouble(VOTE_AVERAGE);
            backdrops[i] = resultsJsonArray.getJSONObject(i).getString(BACKDROP);

            movies[i] = new Movies(ids[i],
                    posterPaths[i],
                    overviews[i],
                    releaseDates[i],
                    originalTitles[i],
                    voteAverages[i],
                    backdrops[i]);

            Log.i("poster size"," sixe is " + movies.length + "poster path " + posterPaths[i]);
        }


        return movies;
    }

    public static String[] getTrailers(Context context, String trailersStr)
         throws JSONException {


        JSONObject trailersData = new JSONObject(trailersStr);

        JSONArray resultsJsonArray = trailersData.getJSONArray(RESULTS);
        final String TRAILER_TYPE = context.getString(R.string.trialer_type);
        final String TRAILER_KEY = context.getString(R.string.trialer_key);

        ArrayList<String> trailers = new ArrayList<>();
        for(int i = 0; i < resultsJsonArray.length(); i++){
            String type = resultsJsonArray.getJSONObject(i).getString(TRAILER_TYPE);
            if (type.equals(context.getString(R.string.trialer))){
//                go ahead and add the trailer to the array
                String trailer = resultsJsonArray.getJSONObject(i).getString(TRAILER_KEY);
                Log.d("DEBUGGING", "trailers link" + trailer);
                trailers.add(trailer);
            }
            }
            String[] trailerArray = new String[trailers.size()];
            for(int i=0; i<trailers.size(); i++){
                trailerArray[i] = trailers.get(i);
            }


            return (trailerArray);
    }

    public static HashMap<String, String> getReviews(Context context, String reviewsJsonStr)
            throws JSONException {


        JSONObject trailersData = new JSONObject(reviewsJsonStr);

        JSONArray resultsJsonArray = trailersData.getJSONArray(RESULTS);
        final String REVIEWER_NAME = context.getString(R.string.reviewer_name);
        final String REVIEWER_COMMENT = context.getString(R.string.reviewer_comment);

        HashMap<String, String> hmap = new HashMap<String, String>();
        for(int i = 0; i < resultsJsonArray.length(); i++){
            String reviewerName = resultsJsonArray.getJSONObject(i).getString(REVIEWER_NAME);
            String reviewerComment = resultsJsonArray.getJSONObject(i).getString(REVIEWER_COMMENT);
            hmap.put(reviewerName, reviewerComment);

        }
        return hmap;
    }



    public static void setMovieDataArrayLength(int length){
        ids = new long[length];
        posterPaths = new String[length];
        overviews = new String[length];
        releaseDates = new String[length];
        originalTitles = new String[length];
        voteAverages = new double[length];
        backdrops = new String[length];

        movies = new Movies[length];
    }
}
