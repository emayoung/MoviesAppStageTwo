package com.android.example.moviesapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by ememobong on 09/05/2017.
 */

public class SynopsisFragment extends Fragment {

//    TODO: you should retrieve the arguments of the fragment to be used to in the textview
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.movie_synopsis_textview, container, false);
        Bundle synopsis = getArguments();
        TextView synopsisTextView = (TextView) view.findViewById(R.id.movie_synopsis_tv);
        synopsisTextView.setText(synopsis.getString(getString(R.string.bundle_args_synopsis)));

        return view;
    }
}
