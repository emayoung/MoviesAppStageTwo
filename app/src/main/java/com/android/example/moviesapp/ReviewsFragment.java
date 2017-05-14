package com.android.example.moviesapp;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ememobong on 09/05/2017.
 */

public class ReviewsFragment extends Fragment {


    String reviewerNames[];
    String reviewerComments[];
    private ContentAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        RecyclerView recyclerView = (RecyclerView) inflater.inflate(
                R.layout.movie_reviews_recycler_view, container, false);
        Bundle bundle = getArguments();
        reviewerNames = bundle.getStringArray("Name");
        reviewerComments= bundle.getStringArray("Comment");
        adapter = new ContentAdapter(recyclerView.getContext(), reviewerNames, reviewerComments);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return recyclerView;
    }

    public class ReviewsViewHolder extends RecyclerView.ViewHolder{

        @Nullable @BindView(R.id.reviewer_profile_pics) ImageView reviewerImage;
        @Nullable @BindView(R.id.revier_name) TextView reviewerName;
        @Nullable @BindView(R.id.reviewer_comment) TextView reviewerComment;

//        using butter knife made viewholder to recycle views faster, the initial nag disappears and scrolling was smooth

        private ReviewsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }

    public class ContentAdapter extends RecyclerView.Adapter<ReviewsViewHolder> {
        // Set numbers of Card in RecyclerView.
        private int LENGTH;
        private Context context;
        private String reviewerName[];
        private String reviewerComment[];


        public ContentAdapter(Context context, String[] reviewerName, String[] reviewerComment) {
            this.context = context;
            this.reviewerName = reviewerName;
            this.reviewerComment = reviewerComment;
            LENGTH = this.reviewerName.length;
        }

        @Override
        public ReviewsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            //            try inflating the layout here and return the viewholder object to the adapter
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            boolean bindToParentOnStart = false;

            View view =  layoutInflater.inflate(R.layout.user_reviews_cardview, parent, bindToParentOnStart);
            ReviewsViewHolder reviewsViewHolder = new ReviewsViewHolder(view);
            return reviewsViewHolder;
        }

        @Override
        public void onBindViewHolder(ReviewsViewHolder holder, int position) {
// TODO: get individuals views and display what you wish to display for now display a highly manipulative list
            holder.reviewerName.setText(reviewerName[position]);
            holder.reviewerComment.setText(reviewerComment[position]);

        }

        @Override
        public int getItemCount() {
            return LENGTH;
        }
    }
}
