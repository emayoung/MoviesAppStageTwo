package com.android.example.moviesapp.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;

import com.android.example.moviesapp.R;

import butterknife.BindString;

/**
 * Created by ememobong on 14/04/2017.
 */

public class MoviePreferenceUtils {

    public static String getUserMoviePreference(Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
       return sharedPreferences.getString(context.getString(R.string.pref_movie_key),
               context.getString(R.string.pref_movie_default));
    }
}
