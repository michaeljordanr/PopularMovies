package com.jordan.android.popularmovies.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;

import com.google.gson.Gson;
import com.jordan.android.popularmovies.interfaces.AsyncTaskCompleteListener;
import com.jordan.android.popularmovies.models.Movie;
import com.jordan.android.popularmovies.utilities.NetworkUtils;

import java.net.SocketTimeoutException;
import java.net.URL;

/**
 * Created by Michael on 28/02/2018.
 */

public class MovieTask extends AsyncTask<String, Void, Movie> {

    final Context mContext;
    boolean isNetworkAvailable = true;
    private AsyncTaskCompleteListener mListener;

    private Movie mMovie = new Movie();

    public MovieTask(Context context, AsyncTaskCompleteListener listener) {
        mContext = context;
        mListener = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Movie doInBackground(String... movieId) {
        try {
            String strMovieId = movieId[0];

            URL moviesRequestUrl = NetworkUtils.buildUrlMovie(strMovieId);
            String jsonResponse = NetworkUtils.run(moviesRequestUrl);
            mMovie = new Gson().fromJson(jsonResponse, mMovie.getClass());

            return mMovie;
        } catch (SocketTimeoutException e){
            isNetworkAvailable = false;
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(Movie movie) {
        super.onPostExecute(movie);
        mListener.onTaskComplete(movie, isNetworkAvailable);
    }
}
