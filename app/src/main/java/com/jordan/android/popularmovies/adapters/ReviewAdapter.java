package com.jordan.android.popularmovies.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jordan.android.popularmovies.R;
import com.jordan.android.popularmovies.models.Review;

import java.util.List;

public class ReviewAdapter extends  RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>{
    private List<Review> mReviewList;

    private final ReviewAdapterOnClickListener mClickListener;
    private final Context mContext;

    public interface ReviewAdapterOnClickListener{
        void onClickReview(String url);
    }

    public ReviewAdapter(Context context,ReviewAdapterOnClickListener listener){
        mClickListener = listener;
        mContext = context;
    }

    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.review_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdForListItem, parent, false);

        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewViewHolder holder, int position) {
        Review review = mReviewList.get(position);

        String author = mContext.getString(R.string.dash) + review.getAuthor();
        holder.mAuthorTextView.setText(author);
        holder.mReviewContentTextView.setText(review.getContent());
    }

    @Override
    public int getItemCount() {
        if(null == mReviewList) return 0;
        return mReviewList.size();
    }

    public void setReviewData(List<Review> reviews){
        this.mReviewList = reviews;
        notifyDataSetChanged();
    }

    public class ReviewViewHolder extends  RecyclerView.ViewHolder implements View.OnClickListener{
        final TextView mAuthorTextView;
        final TextView mReviewContentTextView;

        public ReviewViewHolder(View itemView) {
            super(itemView);
            mAuthorTextView = itemView.findViewById(R.id.tv_author);
            mReviewContentTextView = itemView.findViewById(R.id.tv_review_content);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            Review reviewSelected = mReviewList.get(position);
            String url = reviewSelected.getUrl();
            mClickListener.onClickReview(url);
        }
    }
}
