package com.jordan.android.popularmovies.tasks;

import android.content.Context;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.jordan.android.popularmovies.interfaces.AsyncTaskCompleteListener;
import com.jordan.android.popularmovies.models.Movie;
import com.jordan.android.popularmovies.models.Page;
import com.jordan.android.popularmovies.utilities.Filter;
import com.jordan.android.popularmovies.utilities.NetworkUtils;

import java.net.URL;
import java.net.UnknownHostException;
import java.util.List;

/**
 * Created by Michael on 28/02/2018.
 */

public class PopularMoviesTask extends AsyncTask<Filter, Void, List<Movie>> {

    final Context mContext;
    boolean isNetworkAvailable = true;
    AsyncTaskCompleteListener mListener;

    private Page resultPage = new Page();

    public PopularMoviesTask(Context context, AsyncTaskCompleteListener listener) {
        mContext = context;
        mListener = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected List<Movie> doInBackground(Filter... filter) {
        try {
            Filter filterSelected = filter[0];

            URL moviesRequestUrl = null;
            if (filterSelected == Filter.POPULAR) {
                moviesRequestUrl = NetworkUtils.buildUrlPopular(1);
            } else if (filterSelected == Filter.TOP_RATED) {
                moviesRequestUrl = NetworkUtils.buildUrlTopRated(1);
            }

            String jsonResponse = NetworkUtils.run(moviesRequestUrl);

            resultPage = new Gson().fromJson(jsonResponse, resultPage.getClass());

            return resultPage.getResults();
        } catch (UnknownHostException e){
            isNetworkAvailable = false;
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(List<Movie> movies) {
        mListener.onTaskComplete(movies, isNetworkAvailable);
    }
}
