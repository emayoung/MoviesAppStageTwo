package com.android.example.moviesapp;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;

import com.android.example.moviesapp.utilities.MovieJsonUtils;
import com.android.example.moviesapp.utilities.Movies;
import com.android.example.moviesapp.utilities.NetworkUtils;

import java.net.URL;

/**
 * Created by ememobong on 10/05/2017.
 */

public class FetchTrailersTask extends AsyncTask<String, Void, String> {


    private NotifyTrailersReady notifyTrailersReady;
    private Activity activity;

    public interface NotifyTrailersReady{
        void taskFinished(String trailersJson);
    }

    FetchTrailersTask(NotifyTrailersReady listener, Activity activity){
//            this creates the interface with the mainActivity by passing in the context of the mainActivity
        notifyTrailersReady = listener;
        this.activity = activity;
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {

        Log.d("DEBUGGING", "entered do in background of trailers task" );
            /* If there's no zip code, there's nothing to look up. */
        if (params.length == 0) {
            return null;
        }

        String movieID = params[0];
        URL movieUrl = NetworkUtils.buildTrailerUrl(movieID);
        Log.d("DEBUGGING", "this is the built url of the trailers" + movieUrl);

        try {
            String jsonMovieResponse = NetworkUtils
                    .getNetworkResponseFromServer(movieUrl);
            Log.d("DEBUGGING", "this is the json response string of the trailers" + jsonMovieResponse);

            return jsonMovieResponse;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(String trailersJson) {
        Log.d("DEBUGGING", "entered on post execute of trailers");
        notifyTrailersReady.taskFinished(trailersJson);

    }

}