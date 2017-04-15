package com.android.example.moviesapp;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.example.moviesapp.utilities.MovieJsonUtils;
import com.android.example.moviesapp.utilities.Movies;
import com.android.example.moviesapp.utilities.NetworkUtils;
import com.bumptech.glide.Glide;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ememobong on 29/03/2017.
 */

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MoviesViewHolder>{

    private Movies[] mMovieData;

    private MovieAdapterOnClickHandler movieAdapterOnClickHandler;
    private Activity activity;

    public interface MovieAdapterOnClickHandler{
        void movieClicked(String movieData);
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

        String url = activity.getString(R.string.base_image_url) + mMovieData[position].getPosterPaths();
        Glide.with(activity).load(url).into(holder.moviePosterImage);

    }

    @Override
    public int getItemCount() {
        if (null == mMovieData) return 0;
        return mMovieData.length;
    }

    public void setMovieData(Movies[] movieData) {
        mMovieData = movieData;
        notifyDataSetChanged();
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
            movieAdapterOnClickHandler.movieClicked(String.valueOf(position));
        }
    }
}


//    we need an interface in the adpater that interacts with the mainActivity