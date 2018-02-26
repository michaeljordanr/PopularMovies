package com.jordan.android.popularmovies.utilities;

import android.content.Context;
import android.net.Uri;

import com.jordan.android.popularmovies.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by Michael on 25/02/2018.
 */

public final class NetworkUtils {
    private static final String POPULAR_MOVIES_URL =
            "https://api.themoviedb.org/3/movie/popular";
    private static final String TOP_RATED__MOVIES_URL =
            "https://api.themoviedb.org/3/movie/top_rated";
    private static final String MOVIE_URL =
            "https://api.themoviedb.org/3/movie/";
    private static final String IMG_URL =
            "http://image.tmdb.org/t/p/";

    private static final String LANGUAGE = "en-US";


    private final static String API_KEY_PARAM = "api_key";
    private final static String LANGUAGE_PARAM = "language";
    private final static String PAGE_PARAM = "page";
    private final static String IMG_SIZE_PARAM = "w185";

    public static URL buildUrlPopular(Context context, int page) {
        Uri builtUri = Uri.parse(POPULAR_MOVIES_URL).buildUpon()
                .appendQueryParameter(API_KEY_PARAM, context.getString(R.string.themoviedb_api_key))
                .appendQueryParameter(LANGUAGE_PARAM, LANGUAGE)
                .appendQueryParameter(PAGE_PARAM, String.valueOf(page))
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    public static URL buildUrlTopRated(Context context, int page) {
        Uri builtUri = Uri.parse(TOP_RATED__MOVIES_URL).buildUpon()
                .appendQueryParameter(API_KEY_PARAM, context.getString(R.string.themoviedb_api_key))
                .appendQueryParameter(LANGUAGE_PARAM, LANGUAGE)
                .appendQueryParameter(PAGE_PARAM, String.valueOf(page))
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    public static URL buildUrlMovie(Context context, String movieId) {
        Uri builtUri = Uri.parse(MOVIE_URL + movieId).buildUpon()
                .appendQueryParameter(API_KEY_PARAM, context.getString(R.string.themoviedb_api_key))
                .appendQueryParameter(LANGUAGE_PARAM, LANGUAGE)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    public static URL buildUrlImg(String img) {
        Uri builtUri = Uri.parse(IMG_URL + "/" + IMG_SIZE_PARAM + "/" + img).buildUpon()
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}
