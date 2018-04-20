package com.jordan.android.popularmovies.handler;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;

public class TopRatedAsyncQueryHandler extends AsyncQueryHandler {
    private final TopRatedQueryHandlerCompleteListener mClickListerner;

    public interface TopRatedQueryHandlerCompleteListener{
        void onQueryAllComplete(Cursor cursor);
        void onInsertFavoriteComplete(boolean wasSucceeded);
        void onDeleteFavoriteComplete(boolean wasSucceeded);
    }

    public TopRatedAsyncQueryHandler(ContentResolver cr,
                                     TopRatedQueryHandlerCompleteListener listener) {
        super(cr);
        mClickListerner = listener;
    }

    @Override
    protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
        cursor.close();
        mClickListerner.onQueryAllComplete(cursor);
    }

    @Override
    protected void onInsertComplete(int token, Object cookie, Uri uri) {
        mClickListerner.onInsertFavoriteComplete(uri !=null);
    }

    @Override
    protected void onUpdateComplete(int token, Object cookie, int result) {  }

    @Override
    protected void onDeleteComplete(int token, Object cookie, int result) {
        mClickListerner.onDeleteFavoriteComplete(result > 0);
    }
}
