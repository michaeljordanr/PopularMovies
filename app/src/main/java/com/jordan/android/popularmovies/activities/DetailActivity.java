package com.jordan.android.popularmovies.activities;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.jordan.android.popularmovies.R;
import com.jordan.android.popularmovies.data.MovieContract;
import com.jordan.android.popularmovies.interfaces.AsyncTaskCompleteListener;
import com.jordan.android.popularmovies.models.Movie;
import com.jordan.android.popularmovies.tasks.MovieTask;
import com.jordan.android.popularmovies.utilities.Keys;
import com.jordan.android.popularmovies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Michael on 25/02/2018.
 */

public class DetailActivity extends AppCompatActivity implements AsyncTaskCompleteListener<Movie>,
        CompoundButton.OnCheckedChangeListener{

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
    @BindView(R.id.cl_detail)
    ConstraintLayout mDetailLayout;
    @BindView(R.id.bt_favorite)
    ToggleButton mFavoriteToggleButton;

    Toast mToast;

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

        mFavoriteToggleButton.setOnCheckedChangeListener(this);
        mToast = new Toast(this);
    }

    private void fillMovieDetail(Movie movie){
        final Context context = this;
        SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());

        Date date = new Date();
        try {
            date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    .parse(movie.getReleaseDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        mTitleTextView.setText(movie.getTitle());
        mRatingsTextView.setText(String.valueOf(movie.getRating()));
        mReleaseDateTextView.setText(formatter.format(date));
        mPlotTextView.setText(movie.getPlot());
        mFavoriteToggleButton.setTag(movie.getId());

        Picasso.with(context).load(NetworkUtils.buildUrlImg(
                movie.getImagePath()).toString()).into(mPosterImageView);

        if(checkFavorited(movie.getId())){
            mFavoriteToggleButton.setOnCheckedChangeListener(null);
            mFavoriteToggleButton.setChecked(true);
            mFavoriteToggleButton.setOnCheckedChangeListener(this);
        }

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


    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean on) {

        if(compoundButton.getTag() == null){
            return;
        }

        String msg;

        if(mToast != null){
            mToast.cancel();
        }

        if(on){
            if(addFavorite(Integer.parseInt(
                    compoundButton.getTag().toString()),
                    mTitleTextView.getText().toString())) {

                msg = getString(R.string.favorite_added);
            }else{
                msg = getString(R.string.error);
            }
        }else{
            if(removeFavorite(Integer.parseInt(compoundButton.getTag().toString()))){
                msg = getString(R.string.favorive_removed);
            }else{
                msg = getString(R.string.error);
            }
        }

        mToast = Toast.makeText(this, msg, Toast.LENGTH_LONG);

        mToast.show();
    }

    public boolean addFavorite(int id, String movieTitle){
        ContentValues contentValues = new ContentValues();

        contentValues.put(MovieContract.FavoriteEntry.COLUMN_MOVIE_ID, id);
        contentValues.put(MovieContract.FavoriteEntry.COLUMN_TITLE, movieTitle);

        Uri uri = getContentResolver().insert(MovieContract.FavoriteEntry.CONTENT_URI, contentValues);

        if(uri == null){
            return false;
        }

        return true;
    }

    public boolean removeFavorite(int id){
        ContentResolver contentResolver = getContentResolver();

        Uri uriToDelete = MovieContract.FavoriteEntry.CONTENT_URI.buildUpon()
                .appendPath(String.valueOf(id)).build();

        int favoritesDeleted = contentResolver.delete(uriToDelete, null, null);

        if(favoritesDeleted <= 0 ){
            return false;
        }

        return true;
    }

    public boolean checkFavorited(int id){
        Cursor c = getContentResolver().query(MovieContract.FavoriteEntry.CONTENT_URI,
                null,
                MovieContract.FavoriteEntry.COLUMN_MOVIE_ID + "=?",
                new String[]{String.valueOf(id)},
                null);

        if (c != null && c.getCount() > 0) {
            return true;
        }

        return false;

    }
}
