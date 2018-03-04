package com.jordan.android.popularmovies.interfaces;

/**
 * Created by Michael on 28/02/2018.
 */

public interface AsyncTaskCompleteListener<T> {
    void onTaskComplete(T result, boolean isNetworkAvailable);
}
