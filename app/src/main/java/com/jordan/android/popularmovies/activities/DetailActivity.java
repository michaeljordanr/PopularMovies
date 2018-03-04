package com.jordan.android.popularmovies.activities;

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
import com.jordan.android.popularmovies.R;
import com.jordan.android.popularmovies.interfaces.AsyncTaskCompleteListener;
import com.jordan.android.popularmovies.models.Movie;
import com.jordan.android.popularmovies.tasks.MovieTask;
import com.jordan.android.popularmovies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Michael on 25/02/2018.
 */

public class DetailActivity extends AppCompatActivity implements AsyncTaskCompleteListener<Movie> {

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        Intent intentThatShareThisActivity = getIntent();

        if(intentThatShareThisActivity != null) {
            if (intentThatShareThisActivity.hasExtra("ID_MOVIE")) {
                mDetailLayout.setVisibility(View.INVISIBLE);
                mLoadingProgress.setVisibility(View.VISIBLE);

                int idMovie = intentThatShareThisActivity.getIntExtra("ID_MOVIE", 0);
                new MovieTask(this, this).execute(String.valueOf(idMovie));
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
                movie.getImagePath()).toString()).into(mPosterImageView);
    }


    @Override
    public void onTaskComplete(Movie result, boolean isNetworkAvailable) {
        if(isNetworkAvailable) {
            mDetailLayout.setVisibility(View.VISIBLE);
            fillMovieDetail(result);
        }else{
            NetworkUtils.showDialogErrorNetwork(this);
        }
        mLoadingProgress.setVisibility(View.INVISIBLE);
    }
}
