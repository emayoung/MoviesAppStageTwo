package com.android.example.moviesapp;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ShareCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.HashMap;

/**
 * Created by ememobong on 09/05/2017.
 */

public class TrailerPagerAdapter extends PagerAdapter {

    Activity context;
    private String[] trailers;
    LayoutInflater layoutInflater;
    private String backdrop;
    HashMap<String, String> reviews = new HashMap<>();

    public TrailerPagerAdapter(Activity context, String backdrop) {
        this.context = context;
        this.backdrop = backdrop;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == (object);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return context.getString(R.string.trailer_title);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View itemView = layoutInflater.inflate(R.layout.swipe_trailer_images, container, false);

        final ImageView imageView = (ImageView) itemView.findViewById(R.id.imageView);
        final TextView textView = (TextView) itemView.findViewById(R.id.hint_textview);
        final Button  button = (Button) itemView.findViewById(R.id.go_it_hint_but);
        final ImageButton imageButton = (ImageButton) itemView.findViewById(R.id.circle_play_trailer_but);
        final FloatingActionButton shareFab = (FloatingActionButton) itemView.findViewById(R.id.share_fab);
        final TextView trailersTV = (TextView) itemView.findViewById(R.id.trailer_video_number);
        trailersTV.setText(context.getString(R.string.trialer) + (position + 1));

        final boolean showHint = userHasNeverClickedGotItOrSwiped();
        if (showHint){
            textView.setVisibility(View.VISIBLE);
            button.setVisibility(View.VISIBLE);
        }else if (position > 1){
//            the user has swiped more than once hide the hint
            updateHintPreference();
            textView.setVisibility(View.INVISIBLE);
            button.setVisibility(View.INVISIBLE);
        }

            Glide.with(context)
                    .load(context.getString(R.string.backdrop_image_url) + backdrop)
                    .into(imageView);

            container.addView(itemView);

        //listening to image click
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                TODO: play trailer
                Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(context.getString(R.string.youtube_app_itemtype)
                        + trailers[position]));
                Intent webIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(context.getString(R.string.youtube_web) + trailers[position]));
                try {
                    context.startActivity(appIntent);
                } catch (ActivityNotFoundException ex) {
                    context.startActivity(webIntent);
                }
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateHintPreference();
                textView.setVisibility(View.INVISIBLE);
                button.setVisibility(View.INVISIBLE);
            }
        });
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // play trailer
                Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(context.getString(R.string.youtube_app_itemtype)
                        + trailers[position]));
                Intent webIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(context.getString(R.string.youtube_web) + trailers[position]));
                try {
                    context.startActivity(appIntent);
                } catch (ActivityNotFoundException ex) {
                    context.startActivity(webIntent);
                }
            }
        });

        shareFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                TODO: share the youtube url here with friends
                String mimeType = "text/plain";

                String textToShare = context.getString(R.string.share_message_two)
                        + context.getString(R.string.youtube_web)
                        + trailers[position];
                ShareCompat.IntentBuilder
                /* The from method specifies the Context from which this share is coming from */
                        .from(context)
                        .setType(mimeType)
                        .setChooserTitle(context.getString(R.string.share_message))
                        .setText(textToShare)
                        .startChooser();
            }
        });

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(container.getFocusedChild());
    }

    @Override
    public int getCount() {

        if (null == trailers)return 0;
        return trailers.length;
    }

    public void loadTrailersData(String[] trailers){
        this.trailers = trailers;
        notifyDataSetChanged();
        Log.d("DEBUGGING", "trailers adapter and notified of change");
    }

    public boolean userHasNeverClickedGotItOrSwiped(){
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.hint_pref_key),
                Context.MODE_PRIVATE);
        boolean userHasNeverClickedGoItOrSwiped = sharedPreferences.getBoolean(context.getString(R.string.hint_pref_key),
                context.getResources().getBoolean(R.bool.hint_defaul_value)
        );
        if (userHasNeverClickedGoItOrSwiped){
            return true;
        }
        return false;
    }

    public void updateHintPreference(){
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.hint_pref_key),
                Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(context.getString(R.string.hint_pref_key),false);
        editor.apply();
    }

}
