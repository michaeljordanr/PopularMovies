package com.jordan.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jordan.android.popularmovies.models.Movie;
import com.jordan.android.popularmovies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.net.URL;

/**
 * Created by Michael on 25/02/2018.
 */

public class DetailActivity extends AppCompatActivity {

    private TextView mTitleTextView;
    private ImageView mPosterImageView;
    private TextView mRatingsTextView;
    private TextView mReleaseDateTextView;
    private TextView mPlotTextView;
    private ProgressBar mLoadingProgress;
    private LinearLayout mDetailLayout;

    private Movie mMovie = new Movie();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mTitleTextView= findViewById(R.id.tv_movie_detail_title);
        mPosterImageView = findViewById(R.id.iv_movie_poster_detail);
        mRatingsTextView = findViewById(R.id.tv_ratings);
        mReleaseDateTextView = findViewById(R.id.tv_release_date);
        mPlotTextView = findViewById(R.id.tv_movie_plot);
        mLoadingProgress = findViewById(R.id.pb_loading_indicator_detail);
        mDetailLayout = findViewById(R.id.ll_detail);

        Intent intentThatShareThisActivity = getIntent();

        if(intentThatShareThisActivity != null) {
            if (intentThatShareThisActivity.hasExtra("ID_MOVIE")) {
                int idMovie = intentThatShareThisActivity.getIntExtra("ID_MOVIE", 0);
                new MovieTask(this).execute(String.valueOf(idMovie));
            }
        }
    }

    private void fillMovieDetail(Movie movie){
        final Context context = this;
        mTitleTextView.setText(movie.getTitle());
        mRatingsTextView.setText(String.valueOf(movie.getRating()));
        mReleaseDateTextView.setText(movie.getReleaseDate());
        mPlotTextView.setText(movie.getPlot());

        Picasso.with(context).load(NetworkUtils.buildUrlImg(
                mMovie.getImagePath()).toString()).into(mPosterImageView);
    }

    public class MovieTask extends AsyncTask<String, Void, Movie> {

        final Context mContext;

        MovieTask(Context context) {
            mContext = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDetailLayout.setVisibility(View.INVISIBLE);
            mLoadingProgress.setVisibility(View.VISIBLE);
        }

        @Override
        protected Movie doInBackground(String... movieId) {
            try {
                String strMovieId = movieId[0];

                URL moviesRequestUrl = NetworkUtils.buildUrlMovie(mContext, strMovieId);

                String jsonResponse = NetworkUtils
                        .getResponseFromHttpUrl(moviesRequestUrl);

                mMovie = new Gson().fromJson(jsonResponse, mMovie.getClass());

                return mMovie;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Movie movie) {
            super.onPostExecute(movie);
            mDetailLayout.setVisibility(View.VISIBLE);
            mLoadingProgress.setVisibility(View.INVISIBLE);
            fillMovieDetail(movie);
        }
    }


}
