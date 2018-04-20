package com.jordan.android.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.jordan.android.popularmovies.data.MovieContract.*;

public class MovieDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "movie.db";
    private static final int DATABASE_VERSION = 3;

    final String SQL_CREATE_MOST_POPULAR_TABLE = "CREATE TABLE " + MostPopularEntry.TABLE_NAME + "(" +
            MostPopularEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            MostPopularEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL," +
            MostPopularEntry.COLUMN_TITLE + " TEXT NOT NULL," +
            MostPopularEntry.COLUMN_POSTER+ " TEXT NOT NULL" +
            ");";

    final String SQL_CREATE_TOP_RATED_TABLE = "CREATE TABLE " + TopRatedEntry.TABLE_NAME + "(" +
            TopRatedEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            TopRatedEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL," +
            TopRatedEntry.COLUMN_TITLE + " TEXT NOT NULL," +
            TopRatedEntry.COLUMN_POSTER+ " TEXT NOT NULL" +
            ");";

    final String SQL_CREATE_FAVORITE_TABLE = "CREATE TABLE " + FavoriteEntry.TABLE_NAME + "(" +
            FavoriteEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            FavoriteEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL," +
            FavoriteEntry.COLUMN_TITLE + " TEXT NOT NULL," +
            FavoriteEntry.COLUMN_POSTER+ " TEXT NOT NULL" +
            ");";

    public MovieDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_FAVORITE_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_MOST_POPULAR_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_TOP_RATED_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) { }
}
