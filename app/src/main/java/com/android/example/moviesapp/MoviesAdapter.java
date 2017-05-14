package com.android.example.moviesapp;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Point;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.example.moviesapp.utilities.MovieJsonUtils;
import com.android.example.moviesapp.utilities.Movies;
import com.android.example.moviesapp.utilities.NetworkUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ememobong on 29/03/2017.
 */

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MoviesViewHolder>{

    private Movies[] mMovieData;
    private Cursor favoritesCursor;

    private MovieAdapterOnClickHandler movieAdapterOnClickHandler;
    private Activity activity;

    public interface MovieAdapterOnClickHandler{
        void movieClicked(String movieData, Cursor cursor);
    }

    MoviesAdapter(MovieAdapterOnClickHandler listener, Activity activity){
//            this creates the interface with the mainActivity by passing in the context of the mainActivity
        movieAdapterOnClickHandler = listener;
        this.activity = activity;
    }

    @Override
    public void onBindViewHolder(MoviesViewHolder holder, int position) {
//            get half the dimensions of the user screen

        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int height = size.y/2;

//            trying to modify the linearlayouot of the image view so four items can be accomodated on the screen
        LinearLayout linearLayout = (LinearLayout) holder.moviePosterImage.getParent();
        ViewGroup.LayoutParams params = linearLayout.getLayoutParams();
        params.height = height;
        linearLayout.setLayoutParams(params);


        holder.setIsRecyclable(true);

        String url = activity.getString(R.string.base_image_url);

        if (MainActivity.favoritesClicked) {
            if(null == favoritesCursor){

            }
            favoritesCursor.moveToPosition(position);

            url = url + favoritesCursor.getString(MainActivity.INDEX_MOVIE_POSTER);
            Glide.with(activity).load(url)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.moviePosterImage);

        }
        else {
            url = url + mMovieData[position].getPosterPaths();
            Glide.with(activity).load(url).into(holder.moviePosterImage);
        }


    }

    @Override
    public int getItemCount() {
//        check if favorites is clicked
        if (MainActivity.favoritesClicked) {
            if (null == favoritesCursor) return 0;
            return favoritesCursor.getCount();
        }
        else if (null == mMovieData) return 0;
        return mMovieData.length;
    }

    public void setMovieData(Movies[] movieData, Cursor cursor) {
//        check for validity of data either from api or from local database
        if (MainActivity.favoritesClicked){
//            use the cursor to populate the recyclerview
            favoritesCursor = cursor;
            notifyDataSetChanged();
        }else{
            mMovieData = movieData;
            notifyDataSetChanged();
        }

    }

    @Override
    public MoviesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//            try inflating the layout here and return the viewholder object to the adapter
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        boolean bindToParentOnStart = false;

        View view =  layoutInflater.inflate(R.layout.movie_poster_grid, parent, bindToParentOnStart);
        MoviesViewHolder moviesViewHolder = new MoviesViewHolder(view);
        return moviesViewHolder;

    }


    public class MoviesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @BindView(R.id.movies_poster_imageview) ImageView moviePosterImage;

//        using butter knife made viewholder to recycle views faster, the initial nag disappears and scrolling was smooth

        private MoviesViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void onClick(View view) {
//                get the position that was clicked and notify the mainactivity
            int position = getAdapterPosition();
            if (MainActivity.favoritesClicked){
                if (favoritesCursor.moveToPosition(position)){
                    Cursor currentMovieCursor = favoritesCursor;
                    movieAdapterOnClickHandler.movieClicked(String.valueOf(position), currentMovieCursor);
                }

            }else {
                movieAdapterOnClickHandler.movieClicked(String.valueOf(position), null);
            }

        }
    }
}


//    we need an interface in the adpater that interacts with the mainActivity