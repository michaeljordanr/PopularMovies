package com.jordan.android.popularmovies.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
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
import com.jordan.android.popularmovies.utilities.Constants;
import com.jordan.android.popularmovies.utilities.NetworkUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class RecyclerViewFragment extends Fragment implements
        PopularMoviesAdapter.PopularMoviesAdapterOnClickListener, AsyncTaskCompleteListener<List<Movie>> {

    private PopularMoviesAdapter mPopularMoviesAdapter;

    @BindView(R.id.rv_popular_movies)
    RecyclerView mRecyclerView;
    private Unbinder unbinder;

    private int filterSelected;
    private  GridLayoutManager mLayoutManager;

    public RecyclerViewFragment(){}

    public static RecyclerViewFragment newInstance(int filter) {
        RecyclerViewFragment fragment = new RecyclerViewFragment();
        Bundle args = new Bundle();
        args.putSerializable(Constants.FILTER_KEY, filter);
        fragment.setArguments(args);

        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recycler_view, container, false);
        unbinder = ButterKnife.bind(this, view);
        setRetainInstance(true);

        mLayoutManager = new GridLayoutManager(getActivity(),numberOfColumns());

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mPopularMoviesAdapter = new PopularMoviesAdapter(this);
        mRecyclerView.setAdapter(mPopularMoviesAdapter);

        Bundle args = getArguments();
        filterSelected = args.getInt(Constants.FILTER_KEY);

        loadPopularMovies(filterSelected);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        if(filterSelected == Constants.FAVORITES &&
                getActivity().getIntent().getBooleanExtra(Constants.FAVORITES_UPDATED, false)) {
            loadPopularMovies(filterSelected);
        }
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
            intentToStartDetailActivity.putExtra(Constants.ID_MOVIE, idMovie);
            intentToStartDetailActivity.putExtra(Constants.FILTER_KEY, filterSelected);
            getActivity().getIntent().putExtra(Constants.FAVORITES_UPDATED, true);
            startActivity(intentToStartDetailActivity);
        }else{
            NetworkUtils.showDialogErrorNetwork(context);
        }
    }

    @Override
    public void onTaskComplete(List<Movie> movies, boolean isNetworkAvailable) {
        if(isNetworkAvailable) {
            if (movies != null) {
                mPopularMoviesAdapter.setMovieData(movies);
            }
        }
    }

    private void loadPopularMovies(int filter){
        String[] filters = new String[2];
        filters[0] = String.valueOf(filter);
        filters[1] = (NetworkUtils.isNetworkAvailable(getContext())) ? String.valueOf(filter) : String.valueOf(Constants.NONE);

        new PopularMoviesTask(getActivity(), this).execute(filters);
    }

    private int numberOfColumns() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        // You can change this divider to adjust the size of the poster
        int widthDivider = 400;
        int width = displayMetrics.widthPixels;
        int nColumns = width / widthDivider;
        if (nColumns < 2) return 2; //to keep the grid aspect
        return nColumns;
    }


}
