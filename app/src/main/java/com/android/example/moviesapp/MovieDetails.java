package com.android.example.moviesapp;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.example.moviesapp.data.MovieContract;
import com.android.example.moviesapp.databaseSync.FavoritesMoviesIntentService;
import com.android.example.moviesapp.utilities.MovieJsonUtils;
import com.android.example.moviesapp.utilities.Movies;
import com.android.example.moviesapp.utilities.NetworkUtils;
import com.bumptech.glide.Glide;

import org.json.JSONException;

import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.R.attr.button;

public class MovieDetails extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<HashMap<String, String>>, FetchTrailersTask.NotifyTrailersReady {

    private long COLUMN_MOVIE_ID;
    private String COLUMN_MOVIE_TITLE;
    private String COLUMN_MOVIE_POSTER;
    private String COLUMN_MOVIE_SYNOPSIS;
    private String COLUMN_USER_RATING;
    private String COLUMN_RELEASE_YEAR;
    private String COLUMN_BACKDROP;

    Bundle synopsisArgs = new Bundle();
    Bundle reviewArgs = new Bundle();
    SynopsisFragment synopsisFragment;
    ReviewsFragment reviewsFragment;


    @BindView(R.id.details_movie_poster) ImageView moviePoster;
    @BindView(R.id.release_year_tv) TextView moviewYearTV;
    @BindView(R.id.user_rating_tv) TextView movieRating;
//
    @BindString(R.string.base_image_url) String baseImageUrl;

    @BindView(R.id.trailer_viewpager) ViewPager trailerViewPager;
    @BindView(R.id.synopsis_reviews_viewpager) ViewPager synopsisAndReviewPager;
    @BindView(R.id.mark_as_fav_but) ImageButton markAsFavoriteBut;
    TrailerPagerAdapter trailerPagerAdapter;

    HashMap<String, String> reviewer;
    String[] reviewerNames;
    String[] reviewerComments;
    ReviewsFragment.ContentAdapter contentAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_layout_details);
        ButterKnife.bind(this);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);

        Intent intent = getIntent();
        Bundle bundleForLoader = new Bundle();
        if (intent.hasExtra(MainActivity.EXTRA_MOVIES)){
            Movies movies = intent.getParcelableExtra(MainActivity.EXTRA_MOVIES);
            bundleForLoader.putParcelable(getString(R.string.bundle_loader_parceable_movie_details_key),
                    movies);

            COLUMN_MOVIE_ID = movies.getIds();
            COLUMN_MOVIE_TITLE = movies.getOriginalTitles();
            COLUMN_MOVIE_POSTER = movies.getPosterPaths();
            COLUMN_MOVIE_SYNOPSIS = movies.getOverviews();
            COLUMN_USER_RATING = String.valueOf(movies.getVoteAverages());
            COLUMN_RELEASE_YEAR = movies.getReleaseDates();
            COLUMN_BACKDROP = movies.getBackdropPaths();


            //        using glide to input fill the details screen
            String url = baseImageUrl + movies.getPosterPaths();
            Glide.with(this).load(url).into(moviePoster);

            collapsingToolbar.setTitle(COLUMN_MOVIE_TITLE);
            moviewYearTV.setText(getFormattedReleaseDate(movies.getReleaseDates()));
            movieRating.setText(movies.getVoteAverages()+"/");

//            bundle the synopsis and the reviews into fragments for them to display

            synopsisArgs.putString(getString(R.string.bundle_args_synopsis), COLUMN_MOVIE_SYNOPSIS);
            synopsisFragment = new SynopsisFragment();
            synopsisFragment.setArguments(synopsisArgs);

            reviewsFragment = new ReviewsFragment();

            LoaderManager.LoaderCallbacks<HashMap<String, String>> callback = MovieDetails.this;

//            TODO: this is a possible todo but not that neccessary
//                  we need to restore the string array in the bundle and could be reused later

            getSupportLoaderManager().initLoader(FORECAST_LOADER_ID, bundleForLoader, callback);

            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo= connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()){

//            network is available continue as usual
                fetchTrailers();

            }
            trailerPagerAdapter = new TrailerPagerAdapter(MovieDetails.this, movies.getBackdropPaths());

            if (isMovieInDatabase(this)){
                setColorOnFavoritesBut(this);
            }

        }

        trailerViewPager.setAdapter(trailerPagerAdapter);
        TabLayout tabs = (TabLayout) findViewById(R.id.tabs);
        tabs.setupWithViewPager(synopsisAndReviewPager);
        setupViewPager(synopsisAndReviewPager);


    }
    public void fetchTrailers(){
        FetchTrailersTask fetchTrailersTask = new FetchTrailersTask(this, this);
        fetchTrailersTask.execute(String.valueOf(COLUMN_MOVIE_ID));
        Log.d("DEBUGGING", "ID of movie to fetch trailer" + COLUMN_MOVIE_ID);
    }
    public  String getFormattedReleaseDate(String dateString){

//        this is a crude way of formating, this was a quick code but could be optimised more TODO
        String oldstring = dateString;
        Date date = null;
        try {
            date = new SimpleDateFormat("yyyy").parse(oldstring);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy");
        return (DATE_FORMAT.format(date)).toString();
    }

    Adapter adapter = new Adapter(getSupportFragmentManager());
    private void setupViewPager(ViewPager viewPager) {
        adapter.addFragment(synopsisFragment, "Synopsis");
        viewPager.setAdapter(adapter);
    }

//    choosed a rational number to track the loader used 45 since i used 44 for the main activity but it could have been any oother number
    private static final int FORECAST_LOADER_ID = 45;
    @Override
    public Loader<HashMap<String, String>> onCreateLoader(int id, Bundle args) {
//        TODO: create an asyncloader task that loads the trailers and the reviews
        return new AsyncTaskLoader<HashMap<String, String> >(this) {

            /* This String array will hold and help cache our reviewer data */
            HashMap<String, String> reviewer = null;

            @Override
            protected void onStartLoading() {
                if (reviewer != null) {
                    deliverResult(reviewer);
                } else {
                    forceLoad();
                }
            }

            @Override
            public HashMap<String, String> loadInBackground() {

                URL reviewersUrl = NetworkUtils.buildReviewsUrl(String.valueOf(COLUMN_MOVIE_ID));

                try {
                    String jsonReviewsResponse = NetworkUtils
                            .getNetworkResponseFromServer(reviewersUrl);

                    HashMap<String, String> hmap = MovieJsonUtils.getReviews(MovieDetails.this, jsonReviewsResponse);
                    return hmap;
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }

            }

            /**
             * Sends the result of the load to the registered listener.
             *
             * @param hmap The result of the load
             */
            public void deliverResult(HashMap<String, String> hmap) {
                reviewer = hmap;
                super.deliverResult(reviewer);
            }
        };    }

    @Override
    public void onLoadFinished(Loader<HashMap<String, String>> loader, HashMap<String, String> data) {

//       TODO: transfer the data to the reviewerfragment
        if(data != null){
            reviewer = data;
            reviewerNames = new String[reviewer.size()];
            reviewerComments = new String[reviewer.size()];
            Set set = reviewer.entrySet();
            Iterator iterator = set.iterator();
            int count = 0;
            while(iterator.hasNext()) {
                Map.Entry mentry = (Map.Entry)iterator.next();
                reviewerNames[count] = (String) mentry.getKey();
                reviewerComments[count] = (String) mentry.getValue();
                count++;
            }
            reviewArgs.putStringArray("Name", reviewerNames);
            reviewArgs.putStringArray("Comment", reviewerComments);
            reviewsFragment.setArguments(reviewArgs);
            adapter.addFragment(reviewsFragment, "Reviews");
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onLoaderReset(Loader<HashMap<String, String>> loader) {
//doing nothing in this method
    }



    @OnClick(R.id.mark_as_fav_but)
    public void addFavorite(ImageButton button) {

        if(isMovieInDatabase(this)){
            deleteFavFromDatabase(this);
            removeColorOnFavBut(this);
        }else {
            //        start service to add movie to database
            setColorOnFavoritesBut(this);

            Intent intent = new Intent(MovieDetails.this, FavoritesMoviesIntentService.class);

            intent.putExtra(this.getString(R.string.movie_id), COLUMN_MOVIE_ID);
            intent.putExtra(this.getString(R.string.movie_id_backup), String.valueOf(COLUMN_MOVIE_ID));
            intent.putExtra(this.getString(R.string.movie_title), COLUMN_MOVIE_TITLE);
            intent.putExtra(this.getString(R.string.movie_poster), COLUMN_MOVIE_POSTER);
            intent.putExtra(this.getString(R.string.movie_synopsis), COLUMN_MOVIE_SYNOPSIS);
            intent.putExtra(this.getString(R.string.user_rating), COLUMN_USER_RATING);
            intent.putExtra(this.getString(R.string.release_year), getFormattedReleaseDate(COLUMN_RELEASE_YEAR));
            intent.putExtra(this.getString(R.string.backdrop), COLUMN_BACKDROP);
            startService(intent);

        }

    }

    public void removeColorOnFavBut(Context context){
        markAsFavoriteBut.setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary));
    }

    @Override
    public void taskFinished(String trailersJson) {
//        TODO: use glide here to display the trailers
        Log.d("DEBUGGING", "entered task finished of movie details");
        try {
            String[] trialers = MovieJsonUtils.getTrailers(MovieDetails.this, trailersJson);
            trailerPagerAdapter.loadTrailersData(trialers);
            Log.d("DEBUGGING", "notified adapters of loaded trialers");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setColorOnFavoritesBut(Context context){
//        query the id in the database
        markAsFavoriteBut.setColorFilter(ContextCompat.getColor(this, R.color.colorAccent));

    }

    public boolean isMovieInDatabase(Context context){

        Cursor movieIsFavoriteCursor = getContentResolver().query(MovieContract.FavoritesMovieEntry.CONTENT_URI,
                null,
                MovieContract.FavoritesMovieEntry.COLUMN_MOVIE_ID + " = " + COLUMN_MOVIE_ID,
                null,
                null);

        if(movieIsFavoriteCursor.moveToFirst()){
            return  true;
        }
        else {
            return false;
        }
    }

    public void deleteFavFromDatabase(Context context){

                int numberOfRowDeleted = -1;
        numberOfRowDeleted = getContentResolver().delete(
                MovieContract.FavoritesMovieEntry.CONTENT_URI,
                MovieContract.FavoritesMovieEntry.COLUMN_MOVIE_ID + " = " + COLUMN_MOVIE_ID,
                null
        );

        if(numberOfRowDeleted != -1){
        }

    }

    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public Adapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
