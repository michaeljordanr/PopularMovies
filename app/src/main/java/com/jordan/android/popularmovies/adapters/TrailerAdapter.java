package com.jordan.android.popularmovies.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.jordan.android.popularmovies.R;
import com.jordan.android.popularmovies.utilities.Constants;
import com.jordan.android.popularmovies.utilities.RoundedCornersTransformation;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.List;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerViewHolder> {
    private List<String> mTrailerArr;

    private final TrailerAdapterOnClickListener mClickListerner;
    private final Context mContext;

    public interface TrailerAdapterOnClickListener{
        void onClickTrailer(String url);
    }

    public TrailerAdapter(Context context, TrailerAdapterOnClickListener listener){
        mClickListerner = listener;
        mContext = context;
    }

    @Override
    public TrailerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.trailer_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdForListItem, parent, false);

        return new TrailerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrailerViewHolder holder, int position) {
        String idTrailer = mTrailerArr.get(position);

        final int radius = 5;
        final int margin = 5;
        final Transformation transformation = new RoundedCornersTransformation(radius, margin);

        Picasso.with(mContext)
                .load(String.format(Constants.URL_YOUTUBE_THUMBNAIL, idTrailer))
                .resize(120, 80)
                .centerCrop()
                .transform(transformation)
                .into(holder.mThumbTrailer);
    }

    @Override
    public int getItemCount() {
        if (null == mTrailerArr) return 0;
        return mTrailerArr.size();
    }

    public void setTrailerData(List<String> trailers){
        this.mTrailerArr = trailers;
        notifyDataSetChanged();
    }

    public class TrailerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public final ImageView mThumbTrailer;

        public TrailerViewHolder(View itemView) {
            super(itemView);
            mThumbTrailer = itemView.findViewById(R.id.iv_videos_thumb);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            String idTrailer = mTrailerArr.get(position);
            mClickListerner.onClickTrailer(Constants.URL_YOUTUBE_VIDEO + idTrailer);
        }
    }
}
