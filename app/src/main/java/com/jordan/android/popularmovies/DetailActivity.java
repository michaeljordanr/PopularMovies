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

import java.net.SocketTimeoutException;
import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Michael on 25/02/2018.
 */

public class DetailActivity extends AppCompatActivity {

    @BindView(R.id.tv_movie_detail_title)
    TextView mTitleTextView;
    @BindView(R.id.iv_movie_poster_detail)
    ImageView mPosterImageView;
    @BindView(R.id.tv_ratings)
    TextView mRatingsTextView;
    @BindView(R.id.tv_release_date)
    TextView mReleaseDateTextView;
    @BindView(R.id.tv_movie_plot)
    TextView mPlotTextView;
    @BindView(R.id.pb_loading_indicator_detail)
    ProgressBar mLoadingProgress;
    @BindView(R.id.ll_detail)
    LinearLayout mDetailLayout;

    private Movie mMovie = new Movie();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

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
        boolean isNetworkAvailable = true;

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
            if(isNetworkAvailable) {
                mDetailLayout.setVisibility(View.VISIBLE);
                fillMovieDetail(movie);
            }else{
                NetworkUtils.showDialogErrorNetwork(mContext);
            }
            mLoadingProgress.setVisibility(View.INVISIBLE);
        }
    }


}
