package com.jordan.android.popularmovies.tasks;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.ContextThemeWrapper;

import com.google.gson.Gson;
import com.jordan.android.popularmovies.R;
import com.jordan.android.popularmovies.data.MovieContract;
import com.jordan.android.popularmovies.handler.FavoriteAsyncQueryHandler;
import com.jordan.android.popularmovies.interfaces.AsyncTaskCompleteListener;
import com.jordan.android.popularmovies.models.Movie;
import com.jordan.android.popularmovies.models.Page;
import com.jordan.android.popularmovies.utilities.Constants;
import com.jordan.android.popularmovies.utilities.NetworkUtils;

import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Michael on 28/02/2018.
 */

public class PopularMoviesTask extends AsyncTask<String, Void, List<Movie>> {

    @SuppressLint("StaticFieldLeak")
    private final Context mContext;
    private boolean isNetworkAvailable = true;
    private final AsyncTaskCompleteListener mListener;

    private Page resultPage = new Page();
    private ProgressDialog progressDialog;

    public PopularMoviesTask(Context context, AsyncTaskCompleteListener listener) {
        mContext = context;
        mListener = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = new ProgressDialog(mContext, R.style.Progress_Dialog_Theme);
        progressDialog.setTitle(mContext.getString(R.string.loading));
        progressDialog.setMessage(mContext.getString(R.string.msg_loading));
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);
        progressDialog.show();
    }

    @Override
    protected List<Movie> doInBackground(String... filter) {
        List<Movie> result;
        URL moviesRequestUrl = null;
        String jsonResponse;

        try {
            int filterSelected = Integer.parseInt(filter[0]);
            boolean isNetworkAvailable = (filter[0].equals(filter[1]));

            if(filterSelected != Constants.FAVORITES) {
                if (isNetworkAvailable) {

                    if (filterSelected == Constants.POPULAR) {
                        moviesRequestUrl = NetworkUtils.buildUrlPopular(1);
                    } else if (filterSelected == Constants.TOP_RATED) {
                        moviesRequestUrl = NetworkUtils.buildUrlTopRated(1);
                    }

                    jsonResponse = NetworkUtils.run(moviesRequestUrl);
                    resultPage = new Gson().fromJson(jsonResponse, resultPage.getClass());
                    updateDatabase(resultPage.getResults(), filterSelected);

                }
            }

            result = getDataFromDatabase(filterSelected);


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
        if(progressDialog != null) {
            progressDialog.dismiss();
        }
        mListener.onTaskComplete(movies, isNetworkAvailable);
    }

    private List<Movie> getDataFromDatabase(int filter) {
        List<Movie> movies = new ArrayList<>();
        Cursor c = null;

        try {

            switch (filter) {
                case Constants.POPULAR:
                    c = mContext.getContentResolver().query(MovieContract.MostPopularEntry.CONTENT_URI,
                            null,
                            null,
                            null,
                            null);
                    break;
                case Constants.TOP_RATED:
                    c = mContext.getContentResolver().query(MovieContract.TopRatedEntry.CONTENT_URI,
                            null,
                            null,
                            null,
                            null);
                    break;
                case Constants.FAVORITES:
                    c = mContext.getContentResolver().query(MovieContract.FavoriteEntry.CONTENT_URI,
                            null,
                            MovieContract.FavoriteEntry.COLUMN_MOVIE_ID,
                            null,
                            null);
                    break;
            }


            if (c != null && c.getCount() > 0) {
                while (c.moveToNext()) {

                    Movie movie = new Movie();
                    movie.setId(c.getInt(MovieContract.FavoriteEntry.INDEX_MOVIE_ID));
                    movie.setTitle(c.getString(MovieContract.FavoriteEntry.INDEX_TITLE));
                    movie.setImagePath(c.getString(MovieContract.FavoriteEntry.INDEX_POSTER));
                    movies.add(movie);
                }

                c.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return movies;
    }

    private void updateDatabase(List<Movie> movies, int filter){
        ContentResolver contentResolver = mContext.getContentResolver();
        ContentValues[] contentValues = new ContentValues[movies.size()];

        Uri uri;

        try {
            switch (filter) {
                case Constants.POPULAR:
                    for (int i = 0; i < movies.size(); i++){
                        ContentValues value = new ContentValues();
                        value.put(MovieContract.MostPopularEntry.COLUMN_MOVIE_ID, movies.get(i).getId());
                        value.put(MovieContract.MostPopularEntry.COLUMN_TITLE, movies.get(i).getTitle());
                        value.put(MovieContract.MostPopularEntry.COLUMN_POSTER, movies.get(i).getImagePath());
                        contentValues[i] = value;
                    }

                    uri = MovieContract.MostPopularEntry.CONTENT_URI.buildUpon().build();
                    contentResolver.delete(uri, null, null);
                    contentResolver.bulkInsert(uri, contentValues);
                    break;
                case Constants.TOP_RATED:
                    for (int i = 0; i < movies.size(); i++){
                        ContentValues value = new ContentValues();
                        value.put(MovieContract.TopRatedEntry.COLUMN_MOVIE_ID, movies.get(i).getId());
                        value.put(MovieContract.TopRatedEntry.COLUMN_TITLE, movies.get(i).getTitle());
                        value.put(MovieContract.TopRatedEntry.COLUMN_POSTER, movies.get(i).getImagePath());
                        contentValues[i] = value;
                    }

                    uri = MovieContract.TopRatedEntry.CONTENT_URI.buildUpon().build();
                    contentResolver.delete(uri, null, null);
                    contentResolver.bulkInsert(uri, contentValues);
                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
