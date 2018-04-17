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
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.jordan.android.popularmovies.R;
import com.jordan.android.popularmovies.adapters.ReviewAdapter;
import com.jordan.android.popularmovies.adapters.TrailerAdapter;
import com.jordan.android.popularmovies.data.MovieContract;
import com.jordan.android.popularmovies.interfaces.AsyncTaskCompleteListener;
import com.jordan.android.popularmovies.models.Movie;
import com.jordan.android.popularmovies.tasks.MovieDetailTask;
import com.jordan.android.popularmovies.utilities.Constants;
import com.jordan.android.popularmovies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Michael on 25/02/2018.
 */

public class DetailActivity extends AppCompatActivity implements AsyncTaskCompleteListener<Movie>,
        CompoundButton.OnCheckedChangeListener, TrailerAdapter.TrailerAdapterOnClickListener,
        ReviewAdapter.ReviewAdapterOnClickListener{

    @BindView(R.id.tv_movie_detail_title)
    private TextView mTitleTextView;
    @BindView(R.id.iv_movie_poster_detail)
    private ImageView mPosterImageView;
    @BindView(R.id.tv_ratings)
    private TextView mRatingsTextView;
    @BindView(R.id.tv_release_date)
    private TextView mReleaseDateTextView;
    @BindView(R.id.tv_movie_plot)
    private TextView mPlotTextView;
    @BindView(R.id.pb_loading_indicator_detail)
    private ProgressBar mLoadingProgress;
    @BindView(R.id.cl_detail)
    private ConstraintLayout mDetailLayout;
    @BindView(R.id.bt_favorite)
    private ToggleButton mFavoriteToggleButton;

    @BindView(R.id.rv_trailer)
    private RecyclerView mTrailerRecyclerView;
    @BindView(R.id.cl_trailer)
    private ConstraintLayout mTrailerConstraintLayout;
    @BindView(R.id.rv_review)
    private RecyclerView mReviewRecyclerView;
    @BindView(R.id.cl_review)
    private ConstraintLayout mReviewConstraintLayout;


    private Toast mToast;
    private Movie mMovie;
    private TrailerAdapter mTrailerAdapter;
    private ReviewAdapter mReviewAdapter;
    private CollapsingToolbarLayout collapsingToolbar;
    private String mTitleForToolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        Toolbar mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(android.R.color.transparent));

        initCollapsingToolbar();

        Intent intentThatShareThisActivity = getIntent();

        if(intentThatShareThisActivity != null) {
            if (intentThatShareThisActivity.hasExtra(Constants.ID_MOVIE)) {
                mDetailLayout.setVisibility(View.INVISIBLE);
                mTrailerConstraintLayout.setVisibility(View.INVISIBLE);
                mLoadingProgress.setVisibility(View.VISIBLE);

                int idMovie = intentThatShareThisActivity.getIntExtra(Constants.ID_MOVIE, 0);
                new MovieDetailTask(this, this).execute(String.valueOf(idMovie));
            }
        }

        mFavoriteToggleButton.setOnCheckedChangeListener(this);
        mToast = new Toast(this);

        LinearLayoutManager layoutManagerTrailer
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mTrailerRecyclerView.setLayoutManager(layoutManagerTrailer);
        mTrailerAdapter = new TrailerAdapter(this, this);
        mTrailerRecyclerView.setAdapter(mTrailerAdapter);


        LinearLayoutManager layoutManagerReview
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mReviewRecyclerView.setLayoutManager(layoutManagerReview);
        mReviewAdapter = new ReviewAdapter(this, this);
        mReviewRecyclerView.setAdapter(mReviewAdapter);
    }

    /**
     * Initializing collapsing toolbar
     * Will show and hide the toolbar title on scroll
     */
    private void initCollapsingToolbar() {
        collapsingToolbar = findViewById(R.id.collapsing_toolbar);

        collapsingToolbar.setScrimsShown(false);

        AppBarLayout mAppBarLayout = findViewById(R.id.appbar);

        // hiding & showing the title when toolbar expanded & collapsed
        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = true;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }

                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbar.setTitle(mTitleForToolbar);
                    isShow = true;
                } else if (isShow) {
                    isShow = false;
                    collapsingToolbar.setTitle(" ");
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // this takes the user 'back', as if they pressed the left-facing triangle icon on the main android toolbar.
                // if this doesn't work as desired, another possibility is to call `finish()` here.
                onBackPressed();
                return true;
        }
        return false;
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

        mTitleForToolbar = movie.getTitle();
        mTitleTextView.setText(movie.getTitle());
        mRatingsTextView.setText(String.valueOf(movie.getRating()));
        mReleaseDateTextView.setText(formatter.format(date));
        mPlotTextView.setText(movie.getPlot());
        mFavoriteToggleButton.setTag(movie.getId());

        Picasso.with(context)
                .load(NetworkUtils.buildUrlImg(movie.getImagePath(), Constants.IMG_SIZE_PARAM).toString())
                .into(mPosterImageView);

        Picasso.with(context).load(NetworkUtils.buildUrlImg(
                movie.getBackdropPathImg(), Constants.IMG_SIZE_PARAM_BACKGROUND).toString()).into((ImageView) findViewById(R.id.backdrop));


        if(isFavorite(movie.getId())){
            mFavoriteToggleButton.setOnCheckedChangeListener(null);
            mFavoriteToggleButton.setChecked(true);
            mFavoriteToggleButton.setOnCheckedChangeListener(this);
        }

        mTrailerAdapter.setTrailerData(movie.getVideos());
        if(movie.getReviews().size() > 0) {
            mReviewConstraintLayout.setVisibility(View.VISIBLE);
            mReviewAdapter.setReviewData(movie.getReviews());
        }else{
            mReviewConstraintLayout.setVisibility(View.GONE);
        }
    }


    @Override
    public void onTaskComplete(Movie result, boolean isNetworkAvailable) {
        mMovie = result;
        if(isNetworkAvailable) {
            mDetailLayout.setVisibility(View.VISIBLE);
            mTrailerConstraintLayout.setVisibility(View.VISIBLE);
            fillMovieDetail(mMovie);
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
                    mMovie.getTitle(),mMovie.getImagePath())) {

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

    private boolean addFavorite(int id, String movieTitle, String img){
        ContentValues contentValues = new ContentValues();

        contentValues.put(MovieContract.FavoriteEntry.COLUMN_MOVIE_ID, id);
        contentValues.put(MovieContract.FavoriteEntry.COLUMN_TITLE, movieTitle);
        contentValues.put(MovieContract.FavoriteEntry.COLUMN_POSTER, img);

        Uri uri = getContentResolver().insert(MovieContract.FavoriteEntry.CONTENT_URI, contentValues);

        return uri != null;
    }

    private boolean removeFavorite(int id){
        ContentResolver contentResolver = getContentResolver();

        Uri uriToDelete = MovieContract.FavoriteEntry.CONTENT_URI.buildUpon()
                .appendPath(String.valueOf(id)).build();

        int favoritesDeleted = contentResolver.delete(uriToDelete, null, null);

        return favoritesDeleted > 0;
    }

    private boolean isFavorite(int id){
        try {
            Cursor c = getContentResolver().query(MovieContract.FavoriteEntry.CONTENT_URI,
                    null,
                    MovieContract.FavoriteEntry.COLUMN_MOVIE_ID + "=?",
                    new String[]{String.valueOf(id)},
                    null);

            if (c != null && c.getCount() > 0) {
                c.close();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void onClickTrailer(String url) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
    }

    @Override
    public void onClickReview(String url) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
    }
}
