package com.android.example.moviesapp;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Point;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
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

import com.android.example.moviesapp.data.MovieContract;
import com.android.example.moviesapp.utilities.MovieJsonUtils;
import com.android.example.moviesapp.utilities.MoviePreferenceUtils;
import com.android.example.moviesapp.utilities.Movies;
import com.android.example.moviesapp.utilities.NetworkUtils;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements
        MoviesAdapter.MovieAdapterOnClickHandler, SharedPreferences.OnSharedPreferenceChangeListener,
        LoaderManager.LoaderCallbacks<Cursor>{

    private final String BUNDLE_KEY = "movie_key";
    public static String EXTRA_MOVIES = "position";

    public static boolean favoritesClicked = false;
    MoviesAdapter moviesAdapter;
    GridLayoutManager gridLayoutManager;
    @BindView(R.id.rv_movies) RecyclerView recyclerView;
    @BindView(R.id.bottom_navigation_bar) BottomNavigationView bottomNavigationView;


    Movies[] movies;

    private int position;

    @BindView(R.id.error_tv) TextView errorTv;
    @BindView(R.id.progress_bar) ProgressBar progressBar;

    public static TextView errorTV;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        errorTv = errorTv;

        gridLayoutManager = new GridLayoutManager(this, numberOfColumns());
        moviesAdapter = new MoviesAdapter(this, this);
        recyclerView.setLayoutManager(gridLayoutManager);


        recyclerView.setAdapter(moviesAdapter);

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo= connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()){

//            network is available continue as usual
            if (savedInstanceState != null && savedInstanceState.containsKey(BUNDLE_KEY)){

                if (favoritesClicked){
                    populateUIUsingLoaderCursor(this);
                }else{
                    movies = (Movies[]) savedInstanceState.getParcelableArray(BUNDLE_KEY);
                    moviesAdapter.setMovieData(movies, null);
                }


            }else{
                fetchMovies();
            }

        }
        else{
//            make visible the error textview and hide the recyclerview
            recyclerView.setVisibility(View.INVISIBLE);
            errorTv.setVisibility(View.VISIBLE);
        }

        setUpBottomNavigationView();
        testingDatabaseForUI();

    }

    private void setUpBottomNavigationView(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        String preference = sharedPreferences.getString(getString(R.string.pref_movie_key),
                getString(R.string.pref_movie_default));

        if(preference.equals(getString(R.string.pref_movie_default))){
//            here 0 stands for popular
            Menu menu = bottomNavigationView.getMenu();
            menu.getItem(0).setChecked(true);
        }else {
//            here 1 stands for top_rated
            Menu menu = bottomNavigationView.getMenu();
            menu.getItem(1).setChecked(true);
        }
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int navID = item.getItemId();
                final Context context = MainActivity.this;
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                String popular = sharedPreferences.getString(context.getString(R.string.pref_movie_key),
                        context.getString(R.string.pref_movie_default));


                switch (navID){
                    case R.id.bottom_nav_popular:
//                        previosly while using setting fragment the behaviour that made the app request for more data was by changing
//                        the preferences and we want tot keep that with the nav bottom
                        favoritesClicked = false; //set this to false since if this is true the UI will use a LoaderManager to populate the UI
                        if (popular.equals(context.getString(R.string.pref_movie_default))){
//                            the user's current pref is already popular no need to write to the database
                            fetchMovies();
                        }else{
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(context.getString(R.string.pref_movie_key),
                                    context.getString(R.string.pref_movie_default));
                            editor.apply();
                        }

                        return true;
                    case R.id.bottom_nav_top_rated:
//                        previosly while using setting fragment the behaviour that made the app request for more data was by changing
//                        the preferences and we want tot keep that with the nav bottom

                        favoritesClicked = false; //set this to false since if this is true the UI will use a LoaderManager to populate the UI
                        String topRated = sharedPreferences.getString(context.getString(R.string.pref_movie_key),
                                context.getString(R.string.pref_movie_default));
                        if (topRated.equals(context.getString(R.string.pref_movie_top_raed))){
//                            the user's current pref is already popular no need to write to the database
                            fetchMovies();
                        }else{
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(context.getString(R.string.pref_movie_key),
                                    context.getString(R.string.pref_movie_top_raed));
                            editor.apply();
                        }
                        return true;
                    case R.id.bottom_nav_favorites:
                        if (recyclerView.getVisibility() == View.INVISIBLE){
                            recyclerView.setVisibility(View.VISIBLE);
                            errorTv.setVisibility(View.INVISIBLE);
                        }
                        favoritesClicked = true;
                        populateUIUsingLoaderCursor(context);

                        return true;
                    default:
                        return false;
                }
            }

        });
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
            if (favoritesClicked){
                outState.putString(BUNDLE_KEY, "");  // we don't really care about this key that's why we are leaving it at "" for now
            }else{
                outState.putParcelableArray(BUNDLE_KEY, movies);
            }

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
    public void movieClicked(String moviePosition, Cursor cursor) {
        //start the details activity here but check firs if the cursor is null-
        Movies newMovies = null;
        if (null == cursor){
            newMovies = movies[Integer.parseInt(moviePosition)];
        } else {
            newMovies = new Movies(
                    cursor.getLong(INDEX_COLUMN_MOVIE_ID),
                    cursor.getString(INDEX_MOVIE_POSTER),
                    cursor.getString(INDEX_MOVIE_SYNOPSIS),
                    String.valueOf(cursor.getLong(INDEX_RELEASE_YEAR)),
                    cursor.getString(INDEX_MOVIE_TITLE),
                    Double.valueOf(cursor.getString(INDEX_MOVIE_USER_RATING)),
                    cursor.getString(INDEX_BACKDROP));

        }
        position = Integer.parseInt(moviePosition);
        Intent intent = new Intent(MainActivity.this, MovieDetails.class);
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
    public void testingDatabaseForUI(){
//        testing for delete on the database
//        int numberOfRowDeleted = -1;
//        numberOfRowDeleted = getContentResolver().delete(
//                MovieContract.FavoritesMovieEntry.CONTENT_URI,
//                null,
//                null
//        );
//        Toast.makeText(this, "rows deleted " + numberOfRowDeleted, Toast.LENGTH_SHORT).show();
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
    private void showProgressBar(){
        if (progressBar.getVisibility() == View.INVISIBLE){
            progressBar.setVisibility(View.VISIBLE);
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

    /*
     * The columns of data that we are interested in displaying within our MainActivity's list of
     */
    public static final String[] MAIN_FORECAST_PROJECTION = {
            MovieContract.FavoritesMovieEntry.COLUMN_MOVIE_ID,
            MovieContract.FavoritesMovieEntry.COLUMN_MOVIE_TITLE,
            MovieContract.FavoritesMovieEntry.COLUMN_MOVIE_POSTER,
            MovieContract.FavoritesMovieEntry.COLUMN_USER_RATING,
            MovieContract.FavoritesMovieEntry.COLUMN_RELEASE_YEAR,
            MovieContract.FavoritesMovieEntry.COLUMN_MOVIE_SYNOPSIS,
            MovieContract.FavoritesMovieEntry.COLUMN_BACKDROP
    };

    /*
         * We store the indices of the values in the array of Strings above to more quickly be able to
         * access the data from our query. If the order of the Strings above changes, these indices
         * must be adjusted to match the order of the Strings.
         */
    public static final int INDEX_COLUMN_MOVIE_ID = 0;
    public static final int INDEX_MOVIE_TITLE = 1;
    public static final int INDEX_MOVIE_POSTER = 2;
    public static final int INDEX_MOVIE_USER_RATING = 3;
    public static final int INDEX_RELEASE_YEAR = 4;
    public static final int INDEX_MOVIE_SYNOPSIS = 5;
    public static final int INDEX_BACKDROP = 5;

    private static final int ID_FORECAST_LOADER = 44;
    private int mPosition = RecyclerView.NO_POSITION; //using this to track recycler view position and return it to the beginner when trying to load from favorites

    private  void populateUIUsingLoaderCursor(Context context){
        getSupportLoaderManager().initLoader(ID_FORECAST_LOADER, null, this);
        onCreateLoader(ID_FORECAST_LOADER, null);

    }

    public boolean isNetWorkAvailable(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo= connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()){

//            network is available continue as usual
           return true;

        }
        else{
            return false;
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {
        if(favoritesClicked){
            switch (loaderId) {

                case ID_FORECAST_LOADER:
                /* URI for all rows of favorites data in our favorites table */
                    Uri favoritesMovieUri = MovieContract.FavoritesMovieEntry.CONTENT_URI;
                /* Sort order: Ascending by user_rating */
                    String sortOrder = MovieContract.FavoritesMovieEntry.COLUMN_USER_RATING + " ASC";
                /*
                 * A SELECTION in SQL declares which rows you'd like to return. In our case, we need all
                 * favorites movies the user has added
                 */

                    return new CursorLoader(this,
                            favoritesMovieUri,
                            MAIN_FORECAST_PROJECTION,
                            null,
                            null,
                            sortOrder);

                default:
                    throw new RuntimeException("Loader Not Implemented: " + loaderId);
            }
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
//        first check if the favorites movie is checked before swapping the cursor
        if (favoritesClicked){
            moviesAdapter.setMovieData(null, data);
            if (mPosition == RecyclerView.NO_POSITION) mPosition = 0;
            recyclerView.smoothScrollToPosition(mPosition);
            if (data.getCount() != 0) hideProgressBar();
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        moviesAdapter.setMovieData(null, null);
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
                moviesAdapter.setMovieData(movieData, null);

            }

        }

    }
}
