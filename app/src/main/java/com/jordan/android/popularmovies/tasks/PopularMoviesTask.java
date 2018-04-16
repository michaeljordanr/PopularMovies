package com.jordan.android.popularmovies.tasks;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.jordan.android.popularmovies.data.MovieContract;
import com.jordan.android.popularmovies.interfaces.AsyncTaskCompleteListener;
import com.jordan.android.popularmovies.models.Movie;
import com.jordan.android.popularmovies.models.Page;
import com.jordan.android.popularmovies.utilities.Filter;
import com.jordan.android.popularmovies.utilities.NetworkUtils;

import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
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
        List<Movie> result;

        try {
            Filter filterSelected = filter[0];

            if(filterSelected == Filter.FAVORITES){
                result = getFavorites();
            }else {

                URL moviesRequestUrl = null;
                if (filterSelected == Filter.POPULAR) {
                    moviesRequestUrl = NetworkUtils.buildUrlPopular(1);
                } else if (filterSelected == Filter.TOP_RATED) {
                    moviesRequestUrl = NetworkUtils.buildUrlTopRated(1);
                }

                String jsonResponse = NetworkUtils.run(moviesRequestUrl);

                resultPage = new Gson().fromJson(jsonResponse, resultPage.getClass());
                result = resultPage.getResults();
            }

            return result;
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

    private List<Movie> getFavorites(){
        List<Movie> favorites = new ArrayList<>();

        try {
            Cursor c = mContext.getContentResolver().query(MovieContract.FavoriteEntry.CONTENT_URI,
                    null,
                    MovieContract.FavoriteEntry.COLUMN_MOVIE_ID,
                    null,
                    null);

            if (c != null && c.getCount() > 0) {
                while (c.moveToNext()){
                    Movie movie = new Movie();
                    movie.setId(c.getInt(MovieContract.FavoriteEntry.INDEX_MOVIE_ID));
                    movie.setTitle(c.getString(MovieContract.FavoriteEntry.INDEX_TITLE));
                    movie.setImagePath(c.getString(MovieContract.FavoriteEntry.INDEX_POSTER));
                    favorites.add(movie);
                }
                c.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return favorites;
    }
}
