package com.jordan.android.popularmovies.handler;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;

public class FavoriteAsyncQueryHandler extends AsyncQueryHandler {

    private final FavoriteQueryHandlerCompleteListener mClickListerner;

    public interface FavoriteQueryHandlerCompleteListener{
        void isFavorite(Cursor cursor);
        void onInsertFavoriteComplete(boolean wasSucceeded);
        void onDeleteFavoriteComplete(boolean wasSucceeded);
    }

    public FavoriteAsyncQueryHandler(ContentResolver cr, FavoriteQueryHandlerCompleteListener listener) {
        super(cr);
        mClickListerner = listener;
    }

    @Override
    protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
        cursor.close();
        mClickListerner.isFavorite(cursor);
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