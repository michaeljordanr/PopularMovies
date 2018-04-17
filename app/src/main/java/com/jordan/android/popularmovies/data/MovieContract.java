package com.jordan.android.popularmovies.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class MovieContract {

    public static final String AUTHORITY = "com.jordan.android.popularmovies";
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final String PATH_FAVORITE = "favorites";

    public static final class FavoriteEntry implements BaseColumns{

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVORITE).build();

        public static final String TABLE_NAME = "favorite";
        public static final String COLUMN_MOVIE_ID = "movieId";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_POSTER = "poster";

        public static final int INDEX_MOVIE_ID = 1;
        public static final int INDEX_TITLE = 2;
        public static final int INDEX_POSTER = 3;
    }
}
