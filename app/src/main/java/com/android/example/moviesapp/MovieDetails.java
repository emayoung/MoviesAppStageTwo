package com.android.example.moviesapp;

import android.content.Intent;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.example.moviesapp.utilities.MovieJsonUtils;
import com.bumptech.glide.Glide;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MovieDetails extends AppCompatActivity {

    ImageView moviePoster;
    TextView moviewYearTV;
    TextView movieDuration;
    TextView movieRating;
    TextView movieOverview;
    TextView movieTitle;
    Button favBut;

    int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        moviePoster = (ImageView) findViewById(R.id.movie_image_poster);
        moviewYearTV = (TextView) findViewById(R.id.movie_year);
        movieDuration = (TextView) findViewById(R.id.movieDuration);
        movieRating = (TextView) findViewById(R.id.user_rating);
        movieOverview= (TextView) findViewById(R.id.movie_details_tv);
        movieTitle= (TextView) findViewById(R.id.movie_title);
        favBut = (Button) findViewById(R.id.favorite_but) ;

        Intent intent = getIntent();

        if (intent.hasExtra(MainActivity.EXTRA_POSITION)){
            String str = intent.getStringExtra(MainActivity.EXTRA_POSITION);
            position = Integer.valueOf(str);
        }
//        using glide to input fill the details screen


        String url = "http://image.tmdb.org/t/p/w185" + MovieJsonUtils.posterPaths[position];
        Glide.with(this).load(url).into(moviePoster);

        movieTitle.setText(MovieJsonUtils.originalTitles[position]);
        moviewYearTV.setText(getFormattedReleaseDate(MovieJsonUtils.releaseDates[position]));
        movieDuration.setText(getResources().getString(R.string.default_movie_duration));
        movieRating.setText(MovieJsonUtils.voteAverages[position] + "/10");
        movieOverview.setText(MovieJsonUtils.overviews[position]);

        favBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MovieDetails.this, "You have added this movie as favorite", Toast.LENGTH_SHORT).show();
            }
        });


    }
    public  String getFormattedReleaseDate(String dateString){

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
}
