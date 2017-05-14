package com.android.example.moviesapp.utilities;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ememobong on 14/04/2017.
 */

public class Movies implements Parcelable{

    private long ids;
    private String posterPaths;
    private String overviews;
    private String releaseDates;
    private String originalTitles;
    private double voteAverages;
    private String backdropPaths;

    public Movies(long ids, String posterPaths, String overviews, String releaseDates,
                  String originalTitles, double voteAverages, String backdropPaths){
        this.ids = ids;
        this.posterPaths = posterPaths;
        this.overviews = overviews;
        this.releaseDates = releaseDates;
        this.originalTitles = originalTitles;
        this.voteAverages = voteAverages;
        this.backdropPaths = backdropPaths;
    }
    public String getPosterPaths() {
        return posterPaths;
    }

    public void setPosterPaths(String posterPaths) {
        this.posterPaths = posterPaths;
    }

    public String getOverviews() {
        return overviews;
    }

    public void setOverviews(String overviews) {
        this.overviews = overviews;
    }

    public String getReleaseDates() {
        return releaseDates;
    }

    public void setReleaseDates(String releaseDates) {
        this.releaseDates = releaseDates;
    }

    public String getOriginalTitles() {
        return originalTitles;
    }

    public void setOriginalTitles(String originalTitles) {
        this.originalTitles = originalTitles;
    }

    public double getVoteAverages() {
        return voteAverages;
    }

    public void setVoteAverages(double voteAverages) {
        this.voteAverages = voteAverages;
    }

    public String getBackdropPaths() {
        return backdropPaths;
    }

    public void setBackdropPaths(String backdropPaths) {
        this.backdropPaths = backdropPaths;
    }


    public long getIds() {
        return ids;
    }

    public void setIds(long ids) {
        this.ids = ids;
    }

    public static Creator<Movies> getCREATOR() {
        return CREATOR;
    }

    // we use a private constructor because we would like to use only one object of Movies to be created per time, this is a personal preference
    private Movies(Parcel in) {
        ids = in.readLong();
        posterPaths = in.readString();
        overviews = in.readString();
        releaseDates = in.readString();
        originalTitles = in.readString();
        voteAverages = in.readDouble();
        backdropPaths = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {

        parcel.writeLong(ids);
        parcel.writeString(posterPaths);
        parcel.writeString(overviews);
        parcel.writeString(releaseDates);
        parcel.writeString(originalTitles);
        parcel.writeDouble(voteAverages);
        parcel.writeString(backdropPaths);

    }

    public static final Creator<Movies> CREATOR = new Creator<Movies>() {
        @Override
        public Movies createFromParcel(Parcel in) {
            return new Movies(in);
        }

        @Override
        public Movies[] newArray(int size) {
            return new Movies[size];
        }
    };
}
