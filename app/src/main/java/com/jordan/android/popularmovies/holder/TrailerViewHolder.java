package com.jordan.android.popularmovies.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.jordan.android.popularmovies.R;

public class TrailerViewHolder extends RecyclerView.ViewHolder {
    public final ImageView mThumbTrailer;

    public TrailerViewHolder(View itemView) {
        super(itemView);
        mThumbTrailer = itemView.findViewById(R.id.iv_videos_thumb);
    }


}