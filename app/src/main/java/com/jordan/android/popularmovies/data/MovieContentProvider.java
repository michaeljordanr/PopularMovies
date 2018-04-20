package com.jordan.android.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class MovieContentProvider extends ContentProvider {

    private static final int MOST_POPULAR = 100;

    private static final int TOP_RATED = 200;

    private static final int FAVORITES = 300;
    private static final int FAVORITES_WITH_ID = 301;

    private static final UriMatcher sUriMatcher = buildUriMatcher();



    private MovieDbHelper mMovieDbHelper;

    private static UriMatcher buildUriMatcher(){

        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(MovieContract.AUTHORITY, MovieContract.PATH_MOST_POPULAR, MOST_POPULAR);

        uriMatcher.addURI(MovieContract.AUTHORITY, MovieContract.PATH_TOP_RATED, TOP_RATED);

        uriMatcher.addURI(MovieContract.AUTHORITY, MovieContract.PATH_FAVORITE, FAVORITES);
        uriMatcher.addURI(MovieContract.AUTHORITY, MovieContract.PATH_FAVORITE + "/#", FAVORITES_WITH_ID);

        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        mMovieDbHelper = new MovieDbHelper(context);

        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final SQLiteDatabase db = mMovieDbHelper.getReadableDatabase();

        int match = sUriMatcher.match(uri);

        Cursor retCursor;

        switch (match){
            case MOST_POPULAR:
                retCursor = db.query(MovieContract.MostPopularEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case TOP_RATED:
                retCursor = db.query(MovieContract.TopRatedEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case FAVORITES:
                retCursor = db.query(MovieContract.FavoriteEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
                default:
                    throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);

        return retCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final SQLiteDatabase db = mMovieDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);

        Uri returnUri;
        long id;

        switch (match){
            case FAVORITES:
                id = db.insert(
                        MovieContract.FavoriteEntry.TABLE_NAME, null, contentValues);

                if(id > 0){
                    returnUri = ContentUris.withAppendedId(MovieContract.FavoriteEntry.CONTENT_URI, id);
                }else{
                    throw new SQLException("Failed to insert row into " + uri);
                }
                break;

             default:
                 throw new UnsupportedOperationException("Unknown uri:" + uri);

        }

        getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {

        final SQLiteDatabase db = mMovieDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);

        String table = "";

        switch (match) {
            case MOST_POPULAR:
                table = MovieContract.MostPopularEntry.TABLE_NAME;
                break;
            case TOP_RATED:
                table = MovieContract.TopRatedEntry.TABLE_NAME;
                break;
        }

        if (!table.equals("")) {
            db.beginTransaction();
            int rowsInserted = 0;
            try {
                for (ContentValues value : values) {

                    long _id = db.insert(table, null, value);
                    if (_id != -1) {
                        rowsInserted++;
                    }
                }
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }

            if (rowsInserted > 0) {
                getContext().getContentResolver().notifyChange(uri, null);
            }

            return rowsInserted;
        } else {
            return super.bulkInsert(uri, values);
        }

    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        final SQLiteDatabase db = mMovieDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);

        int rowsDeleted;

        switch (match){
            case MOST_POPULAR:
                rowsDeleted = db.delete(MovieContract.MostPopularEntry.TABLE_NAME,
                        null, null);
                break;
            case TOP_RATED:
                rowsDeleted = db.delete(MovieContract.TopRatedEntry.TABLE_NAME,
                        null, null);
                break;
            case FAVORITES_WITH_ID:
                String id = uri.getPathSegments().get(1);
                rowsDeleted = db.delete(MovieContract.FavoriteEntry.TABLE_NAME,
                        "movieId=?", new String[]{id});
                break;
                default:
                    throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if(rowsDeleted > 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }
}
