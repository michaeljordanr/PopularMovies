package com.jordan.android.popularmovies.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.jordan.android.popularmovies.R;
import com.jordan.android.popularmovies.models.Movie;
import com.jordan.android.popularmovies.utilities.Constants;
import com.jordan.android.popularmovies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Michael on 25/02/2018.
 */

public class PopularMoviesAdapter extends RecyclerView.Adapter<PopularMoviesAdapter.PopularMoviesViewHolder> {
    private List<Movie> mPopularMovies;

    private final PopularMoviesAdapterOnClickListener mClickListener;

    public interface PopularMoviesAdapterOnClickListener{
        void onClick(int idMovie);
    }

    public PopularMoviesAdapter(PopularMoviesAdapterOnClickListener listener){
        mClickListener = listener;
    }

    @Override
    public PopularMoviesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.popular_movies_item;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdForListItem, parent, false);
        return new PopularMoviesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PopularMoviesViewHolder holder, int position) {
        Movie movie = mPopularMovies.get(position);

        Picasso.with(holder.mMoviePoster.getContext())
                .load(NetworkUtils.buildUrlImg(movie.getImagePath(),
                        Constants.IMG_SIZE_PARAM).toString())
                .placeholder(R.drawable.icon_movie) // can also be a drawable
                .error(R.drawable.icon_movie)
                .into(holder.mMoviePoster);
    }

    @Override
    public int getItemCount() {
        if (null == mPopularMovies) return 0;
        return mPopularMovies.size();
    }

    public void setMovieData(List<Movie> movies) {
        mPopularMovies = movies;
        notifyDataSetChanged();
    }

    public class PopularMoviesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        final ImageView mMoviePoster;

        public PopularMoviesViewHolder(View itemView){
            super(itemView);
            mMoviePoster = itemView.findViewById(R.id.iv_movie_poster);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            Movie movieSelected = mPopularMovies.get(position);
            int idMovie = movieSelected.getId();
            mClickListener.onClick(idMovie);
        }
    }

}
