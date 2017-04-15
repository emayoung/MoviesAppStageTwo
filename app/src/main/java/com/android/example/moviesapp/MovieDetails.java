package com.android.example.moviesapp;

import android.content.Intent;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.example.moviesapp.utilities.MovieJsonUtils;
import com.android.example.moviesapp.utilities.Movies;
import com.bumptech.glide.Glide;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.R.attr.button;

public class MovieDetails extends AppCompatActivity {

    @BindView(R.id.movie_image_poster) ImageView moviePoster;
    @BindView(R.id.movie_year) TextView moviewYearTV;
    @BindView(R.id.movieDuration) TextView movieDuration;
    @BindView(R.id.user_rating) TextView movieRating;
    @BindView(R.id.movie_details_tv) TextView movieOverview;
    @BindView(R.id.movie_title) TextView movieTitle;
    @BindView(R.id.favorite_but) Button favBut;

    @BindString(R.string.base_image_url) String baseImageUrl;
    @BindString(R.string.fav_button_toast_msg) String toastMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        ButterKnife.bind(this);

        Intent intent = getIntent();

        if (intent.hasExtra(MainActivity.EXTRA_MOVIES)){
            Movies movies = intent.getParcelableExtra(MainActivity.EXTRA_MOVIES);

            //        using glide to input fill the details screen

            String url = baseImageUrl + movies.getPosterPaths();
            Glide.with(this).load(url).into(moviePoster);

            movieTitle.setText(movies.getOriginalTitles());
            moviewYearTV.setText(getFormattedReleaseDate(movies.getReleaseDates()));
            movieDuration.setText(getResources().getString(R.string.default_movie_duration));
            movieRating.setText(movies.getVoteAverages() + "/10");
            movieOverview.setText(movies.getOverviews());

        }

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

    @OnClick(R.id.favorite_but)
    public void addFavorite(Button button) {
                Toast.makeText(MovieDetails.this, toastMsg, Toast.LENGTH_SHORT).show();

    }
}
