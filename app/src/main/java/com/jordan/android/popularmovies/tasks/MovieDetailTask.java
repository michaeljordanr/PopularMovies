package com.jordan.android.popularmovies.tasks;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jordan.android.popularmovies.interfaces.AsyncTaskCompleteListener;
import com.jordan.android.popularmovies.models.Movie;
import com.jordan.android.popularmovies.models.Review;
import com.jordan.android.popularmovies.utilities.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Michael on 28/02/2018.
 */

public class MovieDetailTask extends AsyncTask<String, Void, Movie> {

    @SuppressLint("StaticFieldLeak")
    private final Context mContext;
    private boolean isNetworkAvailable = true;
    private final AsyncTaskCompleteListener mListener;

    private Movie mMovie = new Movie();

    public MovieDetailTask(Context context, AsyncTaskCompleteListener listener) {
        mContext = context;
        mListener = listener;
    }


    @Override
    protected Movie doInBackground(String... movieId) {
        try {
            String strMovieId = movieId[0];

            URL moviesRequestUrl = NetworkUtils.buildUrlMovie(strMovieId);
            String jsonResponse = NetworkUtils.run(moviesRequestUrl);
            mMovie = new Gson().fromJson(jsonResponse, mMovie.getClass());

            URL trailersRequestUrl = NetworkUtils.buildUrlMovieTrailers(strMovieId);
            String jsonResponseTrailers = NetworkUtils.run(trailersRequestUrl);
            mMovie.setVideos(getTrailers(jsonResponseTrailers));

            URL reviewsRequestUrl = NetworkUtils.buildUrlMovieReviews(strMovieId);
            String jsonResponseReviews = NetworkUtils.run(reviewsRequestUrl);
            String j = getReviewsJson(jsonResponseReviews);

            Type listType = new TypeToken<ArrayList<Review>>(){}.getType();
            List<Review> reviews = new Gson().fromJson(j, listType);
            mMovie.setReviews(reviews);

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

    private List<String> getTrailers(String json) {
        JSONObject jsonObject;
        List<String> videosArr = new ArrayList<>();

        try {
            jsonObject = new JSONObject(json);

            JSONArray jsonArray = jsonObject.getJSONArray("results");

            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObject = new JSONObject(jsonArray.getString(i));
                videosArr.add(jsonObject.getString("key"));
            }

            return videosArr;

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    private String getReviewsJson(String json) {
        JSONObject jsonObject;

        try {
            jsonObject = new JSONObject(json);

            JSONArray jsonArray = jsonObject.getJSONArray("results");

            return jsonArray.toString();

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }
}
