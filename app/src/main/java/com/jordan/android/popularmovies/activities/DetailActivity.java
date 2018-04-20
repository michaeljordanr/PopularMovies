package com.jordan.android.popularmovies.activities;

import android.content.AsyncQueryHandler;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.jordan.android.popularmovies.R;
import com.jordan.android.popularmovies.adapters.RecyclerViewAdapter;
import com.jordan.android.popularmovies.data.MovieContract;
import com.jordan.android.popularmovies.handler.FavoriteAsyncQueryHandler;
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
        CompoundButton.OnCheckedChangeListener, RecyclerViewAdapter.AdapterOnClickListener,
        FavoriteAsyncQueryHandler.FavoriteQueryHandlerCompleteListener{

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

    @BindView(R.id.rv_trailer)
    RecyclerView mTrailerRecyclerView;
    @BindView(R.id.ll_trailer)
    LinearLayout mTrailerConstraintLayout;
    @BindView(R.id.rv_review)
    RecyclerView mReviewRecyclerView;
    @BindView(R.id.ll_review)
    LinearLayout mReviewConstraintLayout;


    private Toast mToast;
    private Movie mMovie;
    private RecyclerViewAdapter mTrailerAdapter;
    private RecyclerViewAdapter mReviewAdapter;
    private CollapsingToolbarLayout collapsingToolbar;
    private String mTitleForToolbar;

    private String msg;
    private static AsyncQueryHandler queryHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        Toolbar mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(android.R.color.transparent));
        }

        initCollapsingToolbar();

        Intent intentThatShareThisActivity = getIntent();
        int idMovie = 0;

        if(intentThatShareThisActivity != null) {
            if (intentThatShareThisActivity.hasExtra(Constants.ID_MOVIE)) {
                mDetailLayout.setVisibility(View.INVISIBLE);
                mTrailerConstraintLayout.setVisibility(View.INVISIBLE);
                mLoadingProgress.setVisibility(View.VISIBLE);

                idMovie = intentThatShareThisActivity.getIntExtra(Constants.ID_MOVIE, 0);
                new MovieDetailTask( this).execute(String.valueOf(idMovie));
            }
        }

        mFavoriteToggleButton.setOnCheckedChangeListener(this);
        mToast = new Toast(this);

        LinearLayoutManager layoutManagerTrailer
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mTrailerRecyclerView.setLayoutManager(layoutManagerTrailer);
        mTrailerAdapter = new RecyclerViewAdapter(this, this);
        mTrailerRecyclerView.setAdapter(mTrailerAdapter);


        LinearLayoutManager layoutManagerReview
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mReviewRecyclerView.setLayoutManager(layoutManagerReview);
        mReviewAdapter= new RecyclerViewAdapter(this, this);
        mReviewRecyclerView.setAdapter(mReviewAdapter);

        queryHandler = new FavoriteAsyncQueryHandler(getContentResolver(), this);
        checkIfIsFavorite(idMovie);
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
                .placeholder(R.drawable.icon_movie) // can also be a drawable
                .error(R.drawable.icon_movie)
                .into(mPosterImageView);

        Picasso.with(context).load(NetworkUtils.buildUrlImg(
                movie.getBackdropPathImg(), Constants.IMG_SIZE_PARAM_BACKGROUND).toString())
                .placeholder(R.drawable.icon_movie) // can also be a drawable
                .error(R.drawable.icon_movie)
                .into((ImageView) findViewById(R.id.backdrop));

        mTrailerAdapter.setDataTrailer(movie.getVideos());
        if(movie.getReviews().size() > 0) {
            mReviewConstraintLayout.setVisibility(View.VISIBLE);
            mReviewAdapter.setDataReview(movie.getReviews());
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

        if(on){
            addFavorite(Integer.parseInt(compoundButton.getTag().toString()),
                    mMovie.getTitle(),mMovie.getImagePath());
        }else{
           removeFavorite(Integer.parseInt(compoundButton.getTag().toString()));
           msg = getString(R.string.favorive_removed);
        }
    }

    private void addFavorite(int id, String movieTitle, String img){
        ContentValues contentValues = new ContentValues();

        contentValues.put(MovieContract.FavoriteEntry.COLUMN_MOVIE_ID, id);
        contentValues.put(MovieContract.FavoriteEntry.COLUMN_TITLE, movieTitle);
        contentValues.put(MovieContract.FavoriteEntry.COLUMN_POSTER, img);

        try {
            queryHandler.startInsert(
                    Constants.INSERT,
                    null,
                    MovieContract.FavoriteEntry.CONTENT_URI,
                    contentValues);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void removeFavorite(int id){
        try {
            Uri uriToDelete = MovieContract.FavoriteEntry.CONTENT_URI.buildUpon()
                    .appendPath(String.valueOf(id)).build();

        queryHandler.startDelete(
                Constants.DELETE,
                null,
                uriToDelete,
                null,
                null
        );
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void checkIfIsFavorite(int id){
        try {
            queryHandler.startQuery(
                    Constants.QUERY,
                    null,
                    MovieContract.FavoriteEntry.CONTENT_URI,
                    null,
                    MovieContract.FavoriteEntry.COLUMN_MOVIE_ID + "=?",
                    new String[]{String.valueOf(id)},
                    null
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showLongToast(String msg){
        if(mToast != null){
            mToast.cancel();
        }

        mToast = Toast.makeText(this, msg, Toast.LENGTH_LONG);
        mToast.show();
    }

    @Override
    public void isFavorite(Cursor cursor) {
        if (cursor != null && cursor.getCount() > 0) {
            cursor.close();

            mFavoriteToggleButton.setOnCheckedChangeListener(null);
            mFavoriteToggleButton.setChecked(true);
            mFavoriteToggleButton.setOnCheckedChangeListener(this);
        }
    }

    @Override
    public void onInsertFavoriteComplete(boolean wasSucceeded) {
        if(wasSucceeded) {
            msg = this.getString(R.string.favorite_added);
            mToast = Toast.makeText(this, msg, Toast.LENGTH_LONG);
            mToast.show();
        }
    }

    @Override
    public void onDeleteFavoriteComplete(boolean wasSucceeded) {
        if(wasSucceeded) {
            msg = this.getString(R.string.favorive_removed);
            showLongToast(msg);
        }
    }

    private void openWebPage(String url) {
        Uri webpage = Uri.parse(url);

        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    @Override
    public void onClick(String url) {
        openWebPage(url);
    }
}
