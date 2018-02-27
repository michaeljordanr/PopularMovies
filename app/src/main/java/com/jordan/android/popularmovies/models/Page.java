package com.jordan.android.popularmovies.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Michael on 25/02/2018.
 */

public class Page implements Parcelable {

    @SerializedName("page")
    @Expose
    private int page;

    @SerializedName("total_results")
    @Expose
    private int totalResults;

    @SerializedName("total_pages")
    @Expose
    private int totalPages;

    @SerializedName("results")
    @Expose
    private List<Movie> results;

    public Page(){}

    public Page(Parcel input){
        page = input.readInt();
        totalResults = input.readInt();
        totalPages = input.readInt();
        results = input.readArrayList(List.class.getClassLoader());
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getTotal_results() {
        return totalResults;
    }

    public void setTotal_results(int total_results) {
        this.totalResults = total_results;
    }

    public int getTotal_pages() {
        return totalPages;
    }

    public void setTotal_pages(int total_pages) {
        this.totalPages = total_pages;
    }

    public List<Movie> getResults() {
        return results;
    }

    public void setResults(List<Movie> results) {
        this.results = results;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(page);
        parcel.writeInt(totalResults);
        parcel.writeInt(totalPages);
        parcel.writeList(results);
    }

    public static final Parcelable.Creator<Page> CREATOR =
            new Parcelable.Creator<Page>(){
                @Override
                public Page createFromParcel(Parcel parcel) {
                    return new Page(parcel);
                }

                @Override
                public Page[] newArray(int size) {
                    return new Page[size];
                }
            };
}
