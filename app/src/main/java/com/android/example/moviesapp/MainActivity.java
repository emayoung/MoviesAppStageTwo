package com.android.example.moviesapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.example.moviesapp.utilities.MovieJsonUtils;
import com.android.example.moviesapp.utilities.NetworkUtils;

import java.net.URL;

public class MainActivity extends AppCompatActivity implements MoviesAdapter.MovieAdapterOnClickHandler{

    private final String BUNDLE_KEY = "movie_key";
    public static String EXTRA_POSITION = "position";
    MoviesAdapter moviesAdapter;
    GridLayoutManager gridLayoutManager;
    RecyclerView recyclerView;

    String userMoviePreference = "popular";

    TextView errorTv;


//    Remember to remove this toast we make not be needing it
    Toast mToast;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putStringArray(BUNDLE_KEY, MovieJsonUtils.posterPaths);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if(itemId == R.id.sort_popular){
            userMoviePreference = "popular";
            FetchMovieTask fetchMovieTask = new FetchMovieTask();
            fetchMovieTask.execute(userMoviePreference);
        }else{
            userMoviePreference = "top_rated";
            FetchMovieTask fetchMovieTask = new FetchMovieTask();
            fetchMovieTask.execute(userMoviePreference);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user_movie_pref, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.rv_movies);
        errorTv = (TextView) findViewById(R.id.error_tv);
        gridLayoutManager = new GridLayoutManager(this, 2);
        moviesAdapter = new MoviesAdapter(this, this);
        recyclerView.setLayoutManager(gridLayoutManager);


        recyclerView.setAdapter(moviesAdapter);

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo= connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()){

//            network is available continue as usual
            if (savedInstanceState != null && savedInstanceState.containsKey(BUNDLE_KEY)){

                moviesAdapter.setMovieData(savedInstanceState.getStringArray(BUNDLE_KEY));

            }else{

                FetchMovieTask fetchMovieTask = new FetchMovieTask();
                fetchMovieTask.execute(userMoviePreference);
            }

        }
        else{
//            make visible the error textview and hide the recyclerview
            recyclerView.setVisibility(View.INVISIBLE);
            errorTv.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void movieClicked(String movieData) {
        //start the details activity here
        Intent intent = new Intent(MainActivity.this, MovieDetails.class);
        intent.putExtra(MainActivity.EXTRA_POSITION, movieData);
        startActivity(intent);
    }


    public class FetchMovieTask extends AsyncTask<String, Void, String[]> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String[] doInBackground(String... params) {

            /* If there's no zip code, there's nothing to look up. */
            if (params.length == 0) {
                return null;
            }

            String userMoviePreference = params[0];
            URL movieUrl = NetworkUtils.buildUrl(userMoviePreference);

            try {
                String jsonWeatherResponse = NetworkUtils
                        .getNetworkResponseFromServer(movieUrl);

                String[] moviePosterPaths = MovieJsonUtils
                        .getAndSetMovieDataFromJson(MainActivity.this, jsonWeatherResponse);

                return moviePosterPaths;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String[] movieData) {

            if (movieData != null) {
//                TODO: set the data to be used by the movie adapter for now loop through and display the data
                for(String poster : movieData){
                    Log.d("TAG", " " + poster);
                }
                moviesAdapter.setMovieData(movieData);

            } else {

            }
        }
    }





}
