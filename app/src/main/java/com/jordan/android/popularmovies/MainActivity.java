package com.jordan.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.google.gson.Gson;
import com.jordan.android.popularmovies.adapters.PopularMoviesAdapter;
import com.jordan.android.popularmovies.models.Movie;
import com.jordan.android.popularmovies.models.Page;
import com.jordan.android.popularmovies.utilities.NetworkUtils;

import java.net.URL;
import java.util.List;

public class MainActivity extends AppCompatActivity implements PopularMoviesAdapter.PopularMoviesAdapterOnClickListener{

    private PopularMoviesAdapter mPopularMoviesAdapter;
    private RecyclerView mRecyclerView;

    private ProgressBar mLoadingIndicator;

    private Page resultPage = new Page();

    public enum Filter {
        POPULAR, TOP_RATED
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = findViewById(R.id.rv_popular_movies);
        mLoadingIndicator = findViewById(R.id.pb_loading_indicator);

        GridLayoutManager layoutManager = new GridLayoutManager(this,2);

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mPopularMoviesAdapter = new PopularMoviesAdapter(this);
        mRecyclerView.setAdapter(mPopularMoviesAdapter);

        loadPopularMovies(Filter.POPULAR);
    }

    private void loadPopularMovies(Filter filter){
        new PopularMoviesTask(this).execute(filter);
    }

    public class PopularMoviesTask extends AsyncTask<Filter, Void, List<Movie>> {

        final Context mContext;

        public PopularMoviesTask(Context context) {
            mContext = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.INVISIBLE);
        }

        @Override
        protected List<Movie> doInBackground(Filter... filter) {
            try {
                Filter filterSelected = filter[0];

                URL moviesRequestUrl = null;
                if (filterSelected == Filter.POPULAR) {
                    moviesRequestUrl = NetworkUtils.buildUrlPopular(mContext, 1);
                } else if (filterSelected == Filter.TOP_RATED) {
                    moviesRequestUrl = NetworkUtils.buildUrlTopRated(mContext, 1);
                }

                String jsonResponse = NetworkUtils
                        .getResponseFromHttpUrl(moviesRequestUrl);

                resultPage = new Gson().fromJson(jsonResponse, resultPage.getClass());

                return resultPage.getResults();

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<Movie> movies) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            mRecyclerView.setVisibility(View.VISIBLE);

            if (movies != null) {
                mPopularMoviesAdapter.setMovieData(movies);
            }
        }
    }

    @Override
    public void onClick(int idMovie) {
        Context context = this;
        Class destinationClass = DetailActivity.class;
        Intent intentToStartDetailActivity = new Intent(context, destinationClass);
        intentToStartDetailActivity.putExtra("ID_MOVIE", idMovie);
        startActivity(intentToStartDetailActivity);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

         switch (id){
             case R.id.action_popular:
                mPopularMoviesAdapter.setMovieData(null);
                 loadPopularMovies(Filter.POPULAR);
                break;
             case R.id.action_top_rated:
                 mPopularMoviesAdapter.setMovieData(null);
                 loadPopularMovies(Filter.TOP_RATED);
                 break;
         }

        return super.onOptionsItemSelected(item);
    }
}
