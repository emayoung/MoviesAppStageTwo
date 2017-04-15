package com.android.example.moviesapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.example.moviesapp.utilities.MovieJsonUtils;
import com.android.example.moviesapp.utilities.MoviePreferenceUtils;
import com.android.example.moviesapp.utilities.Movies;
import com.android.example.moviesapp.utilities.NetworkUtils;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements MoviesAdapter.MovieAdapterOnClickHandler, SharedPreferences.OnSharedPreferenceChangeListener{

    private final String BUNDLE_KEY = "movie_key";
    public static String EXTRA_MOVIES = "position";
    MoviesAdapter moviesAdapter;
    GridLayoutManager gridLayoutManager;
    @BindView(R.id.rv_movies) RecyclerView recyclerView;

    Movies[] movies;

    private int position;

    @BindView(R.id.error_tv) TextView errorTv;
    @BindView(R.id.progress_bar) ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        gridLayoutManager = new GridLayoutManager(this, numberOfColumns());
        moviesAdapter = new MoviesAdapter(this, this);
        recyclerView.setLayoutManager(gridLayoutManager);


        recyclerView.setAdapter(moviesAdapter);

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo= connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()){

//            network is available continue as usual
            if (savedInstanceState != null && savedInstanceState.containsKey(BUNDLE_KEY)){

                movies = (Movies[]) savedInstanceState.getParcelableArray(BUNDLE_KEY);
                moviesAdapter.setMovieData(movies);

            }else{
                fetchMovies();
            }

        }
        else{
//            make visible the error textview and hide the recyclerview
            recyclerView.setVisibility(View.INVISIBLE);
            errorTv.setVisibility(View.VISIBLE);
        }

    }

    private int numberOfColumns() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        // You can change this divider to adjust the size of the poster
//        this logic holds true in that if i have a small size phones of say 320 - 700 pixels diving this by 400 will yield a
//        value less than 2. so we can display 2 columns at once but this would be crude on a tablet say > 1080px because it would be
//        too large. so in this case we can display three columns
        int widthDivider = 400;
        int width = displayMetrics.widthPixels;
        int nColumns = width / widthDivider;
        if (nColumns < 2) return 2;
        return nColumns;
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {

        if(movies != null){
            outState.putParcelableArray(BUNDLE_KEY, movies);
        }

        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if(itemId == R.id.setting){
            startActivity(new Intent(this, SettingsActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user_movie_pref, menu);
        return true;
    }


    @Override
    public void movieClicked(String moviePosition) {
        //start the details activity here

        position = Integer.parseInt(moviePosition);
        Intent intent = new Intent(MainActivity.this, MovieDetails.class);
        Movies newMovies = movies[Integer.parseInt(moviePosition)];
        intent.putExtra(MainActivity.EXTRA_MOVIES,
               newMovies );
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //check if user has gone to details screen before trying to scroll to position
        if (position != 0){
            recyclerView.scrollToPosition(position);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    private void hideProgressBar(){
        if (progressBar.getVisibility() == View.VISIBLE){
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        if(key.equals(getString(R.string.pref_movie_key))){
//            relaod the data for top rated movies
          fetchMovies();
        }
    }

    private void fetchMovies(){
        progressBar.setVisibility(View.VISIBLE);
        new FetchMovieTask().execute(MoviePreferenceUtils.getUserMoviePreference(this));
    }


    public class FetchMovieTask extends AsyncTask<String, Void, Movies[]> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Movies[] doInBackground(String... params) {

            /* If there's no zip code, there's nothing to look up. */
            if (params.length == 0) {
                return null;
            }

            String userMoviePreference = params[0];
            URL movieUrl = NetworkUtils.buildUrl(userMoviePreference);

            try {
                String jsonWeatherResponse = NetworkUtils
                        .getNetworkResponseFromServer(movieUrl);

                Movies[] moviesList = MovieJsonUtils
                        .getAndSetMovieDataFromJson(MainActivity.this, jsonWeatherResponse);


                return moviesList;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Movies[] movieData) {

            hideProgressBar();

            if (movieData != null) {
                movies = movieData;
                moviesAdapter.setMovieData(movieData);

            }

        }

    }
}
