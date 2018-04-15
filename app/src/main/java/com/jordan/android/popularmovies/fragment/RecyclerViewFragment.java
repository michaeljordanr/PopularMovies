package com.jordan.android.popularmovies.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.jordan.android.popularmovies.R;
import com.jordan.android.popularmovies.activities.DetailActivity;
import com.jordan.android.popularmovies.adapters.PopularMoviesAdapter;
import com.jordan.android.popularmovies.interfaces.AsyncTaskCompleteListener;
import com.jordan.android.popularmovies.models.Movie;
import com.jordan.android.popularmovies.tasks.PopularMoviesTask;
import com.jordan.android.popularmovies.utilities.Filter;
import com.jordan.android.popularmovies.utilities.Keys;
import com.jordan.android.popularmovies.utilities.NetworkUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class RecyclerViewFragment extends Fragment implements
        PopularMoviesAdapter.PopularMoviesAdapterOnClickListener, AsyncTaskCompleteListener<List<Movie>> {

    private PopularMoviesAdapter mPopularMoviesAdapter;

    @BindView(R.id.rv_popular_movies)
    RecyclerView mRecyclerView;
    @BindView(R.id.pb_loading_indicator)
    ProgressBar mLoadingIndicator;
    private Unbinder unbinder;

    private Filter filterSelected;

    public RecyclerViewFragment(){}


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recycler_view, container, false);
        unbinder = ButterKnife.bind(this, view);

        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(),2);

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mPopularMoviesAdapter = new PopularMoviesAdapter(this);
        mRecyclerView.setAdapter(mPopularMoviesAdapter);

        Bundle args = getArguments();
        filterSelected = Filter.fromValue(args.getInt(Keys.FILTER_KEY));

        loadPopularMovies(filterSelected);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onClick(int idMovie) {
        Context context = getActivity();
        if(NetworkUtils.isNetworkAvailable(context)) {
            Class destinationClass = DetailActivity.class;
            Intent intentToStartDetailActivity = new Intent(context, destinationClass);
            intentToStartDetailActivity.putExtra(Keys.ID_MOVIE, idMovie);
            startActivity(intentToStartDetailActivity);
        }
    }

    @Override
    public void onTaskComplete(List<Movie> movies, boolean isNetworkAvailable) {
        if(isNetworkAvailable) {
            mRecyclerView.setVisibility(View.VISIBLE);
            if (movies != null) {
                mPopularMoviesAdapter.setMovieData(movies);
            }
        }else{
            NetworkUtils.showDialogErrorNetwork(getActivity());
        }
        mLoadingIndicator.setVisibility(View.INVISIBLE);
    }

    private void loadPopularMovies(Filter filter){
        mLoadingIndicator.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.INVISIBLE);
        new PopularMoviesTask(getActivity(), this).execute(filter);
    }
}
