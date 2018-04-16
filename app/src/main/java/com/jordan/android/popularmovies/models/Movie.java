package com.jordan.android.popularmovies.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Michael on 25/02/2018.
 */

public class Movie implements Parcelable {

    @SerializedName("id")
    @Expose
    private int id;

    @SerializedName("title")
    @Expose
    private String title;

    @SerializedName("poster_path")
    @Expose
    private String imagePath;

    @SerializedName("vote_average")
    @Expose
    private float ratings;

    @SerializedName("original_title")
    @Expose
    private String originalTitle;

    @SerializedName("overview")
    @Expose
    private String plot;

    @SerializedName("release_date")
    @Expose
    private String releaseDate;

    @SerializedName("backdrop_path")
    @Expose
    private String backdropPathImg;

    private List<String> videos;

    private List<Review> reviews;

    public Movie(){}

    public Movie(Parcel input){
        id = input.readInt();
        title = input.readString();
        imagePath = input.readString();
        ratings = input.readFloat();
        originalTitle = input.readString();
        plot = input.readString();
        releaseDate = input.readString();
        backdropPathImg = input.readString();
        reviews = input.readArrayList(List.class.getClassLoader());
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public String getPlot() {
        return plot;
    }

    public void setPlot(String plot) {
        this.plot = plot;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public float getRating() {
        return ratings;
    }

    public void setRating(float voteAverage) {
        this.ratings = voteAverage;
    }

    public String getBackdropPathImg() { return backdropPathImg; }

    public void setBackdropPathImg(String backdropPathImg) {
        this.backdropPathImg = backdropPathImg;
    }

    public List<String> getVideos() {
        return videos;
    }

    public void setVideos(List<String> videos) {
        this.videos = videos;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(title);
        parcel.writeString(imagePath);
        parcel.writeFloat(ratings);
        parcel.writeString(originalTitle);
        parcel.writeString(plot);
        parcel.writeString(releaseDate);
        parcel.writeString(backdropPathImg);
        parcel.writeList(reviews);
    }

    public static final Parcelable.Creator<Movie> CREATOR =
            new Parcelable.Creator<Movie>(){
                @Override
                public Movie createFromParcel(Parcel parcel) {
                    return new Movie(parcel);
                }

                @Override
                public Movie[] newArray(int size) {
                    return new Movie[size];
                }
            };


}
