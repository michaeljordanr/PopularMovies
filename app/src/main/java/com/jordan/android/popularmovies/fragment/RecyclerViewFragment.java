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
import com.jordan.android.popularmovies.utilities.Constants;
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
    private boolean isNetworkAvailable;

    public RecyclerViewFragment(){}

    public static RecyclerViewFragment newInstance(int filter, boolean isNetworkAvailable) {
        RecyclerViewFragment fragment = new RecyclerViewFragment();
        Bundle args = new Bundle();
        args.putSerializable(Constants.FILTER_KEY, filter);
        args.putSerializable(Constants.NETWORK_KEY, isNetworkAvailable);
        fragment.setArguments(args);
        return fragment;
    }




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
        filterSelected = Filter.fromValue(args.getInt(Constants.FILTER_KEY));
        isNetworkAvailable = args.getBoolean(Constants.NETWORK_KEY);

        loadPopularMovies(filterSelected);


        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle bundle = getArguments();
        if(bundle != null) {
            filterSelected = Filter.fromValue(bundle.getInt(Constants.FILTER_KEY));

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
            intentToStartDetailActivity.putExtra(Constants.FILTER_KEY, filterSelected.getValue());
            startActivity(intentToStartDetailActivity);
        }else{
            NetworkUtils.showDialogErrorNetwork(context);
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
        Filter[] filters = new Filter[2];
        filters[0] = filter;
        filters[1] = (isNetworkAvailable) ? filter : Filter.NONE ;

        new PopularMoviesTask(getActivity(), this).execute(filters);
    }

    @Override
    public void onResume() {
        super.onResume();

        isNetworkAvailable = NetworkUtils.isNetworkAvailable(getContext());
        if(!isNetworkAvailable){
            NetworkUtils.showDialogErrorNetwork(getContext());
        }

        loadPopularMovies(filterSelected);
    }
}
