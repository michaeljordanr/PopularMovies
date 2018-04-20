package com.jordan.android.popularmovies.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.jordan.android.popularmovies.R;

public class ReviewViewHolder extends  RecyclerView.ViewHolder{
    public TextView mAuthorTextView;
    public TextView mReviewContentTextView;

    public ReviewViewHolder(View itemView) {
        super(itemView);
        mAuthorTextView = itemView.findViewById(R.id.tv_author);
        mReviewContentTextView = itemView.findViewById(R.id.tv_review_content);
    }

}