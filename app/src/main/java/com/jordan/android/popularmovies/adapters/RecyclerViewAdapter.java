package com.jordan.android.popularmovies.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jordan.android.popularmovies.R;
import com.jordan.android.popularmovies.holder.ReviewViewHolder;
import com.jordan.android.popularmovies.holder.TrailerViewHolder;
import com.jordan.android.popularmovies.models.Review;
import com.jordan.android.popularmovies.utilities.Constants;
import com.jordan.android.popularmovies.utilities.RoundedCornersTransformation;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private List<Object> items;

    private final int TRAILER = 0, REVIEW = 1;

    private final AdapterOnClickListener mClickListener;
    private final Context mContext;

    public interface AdapterOnClickListener{
        void onClick(String url);
    }

    public RecyclerViewAdapter(Context context, AdapterOnClickListener listener){
        mContext = context;
        mClickListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case TRAILER:
                View v1 = inflater.inflate(R.layout.trailer_list_item, parent, false);
                viewHolder = new TrailerViewHolder(v1);
                break;
            case REVIEW:
                View v2 = inflater.inflate(R.layout.review_list_item, parent, false);
                viewHolder = new ReviewViewHolder(v2);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case TRAILER:
                TrailerViewHolder vh1 = (TrailerViewHolder) holder;
                configureTrailerViewHolder(vh1, position);
                break;
            case REVIEW:
                ReviewViewHolder vh2 = (ReviewViewHolder) holder;
                configureReviewViewHolder(vh2, position);
                break;
        }
    }

    @Override
    public int getItemCount() {
        if(null == items) return 0;
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (items.get(position) instanceof String) {
            return TRAILER;
        } else if (items.get(position) instanceof Review) {
            return REVIEW;
        }
        return -1;
    }

    private void configureTrailerViewHolder(final TrailerViewHolder holder, int position) {
        String idTrailer = items.get(position).toString();
        if (!idTrailer.equals("")) {
            final int radius = 5;
            final int margin = 5;
            final Transformation transformation = new RoundedCornersTransformation(radius, margin);

            Picasso.with(mContext)
                    .load(String.format(Constants.URL_YOUTUBE_THUMBNAIL, idTrailer))
                    .resize(120, 80)
                    .centerCrop()
                    .transform(transformation)
                    .into(holder.mThumbTrailer);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = holder.getAdapterPosition();
                    String idTrailer = items.get(position).toString();
                    mClickListener.onClick(Constants.URL_YOUTUBE_VIDEO + idTrailer);
                }
            });
        }
    }

    private void configureReviewViewHolder(final ReviewViewHolder holder, int position) {
        Review review = (Review) items.get(position);

        String author = mContext.getString(R.string.dash) + review.getAuthor();
        holder.mAuthorTextView.setText(author);
        holder.mReviewContentTextView.setText(review.getContent());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                Review reviewSelected = (Review) items.get(position);
                String url = reviewSelected.getUrl();
                mClickListener.onClick(url);
            }
        });
    }

    public void setDataTrailer(List<String> list){
        ArrayList<Object> listObject = new ArrayList<>();

        listObject.addAll(list);

        this.items = listObject;
        notifyDataSetChanged();
    }

    public void setDataReview(List<Review> list){
        ArrayList<Object> listObject = new ArrayList<>();

        listObject.addAll(list);

        this.items = listObject;
        notifyDataSetChanged();
    }
}
