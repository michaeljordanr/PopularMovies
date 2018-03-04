package com.jordan.android.popularmovies.utilities;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.AlertDialog;

import com.jordan.android.popularmovies.BuildConfig;
import com.jordan.android.popularmovies.R;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Michael on 25/02/2018.
 */

public final class NetworkUtils {
    private static final String THEMOVIEDB_API_KEY = "7b6b5dbb7725da9d89a79a75d7df097e";
    private static final String POPULAR_MOVIES_URL =
            "https://api.themoviedb.org/3/movie/popular";
    private static final String TOP_RATED__MOVIES_URL =
            "https://api.themoviedb.org/3/movie/top_rated";
    private static final String MOVIE_URL =
            "https://api.themoviedb.org/3/movie/";
    private static final String IMG_URL =
            "http://image.tmdb.org/t/p/";

    private static final String LANGUAGE = "en-US";


    private final static String API_KEY_PARAM = BuildConfig.API_KEY;
    private final static String LANGUAGE_PARAM = "language";
    private final static String PAGE_PARAM = "page";
    private final static String IMG_SIZE_PARAM = "w185";

    private static OkHttpClient client = new OkHttpClient();

    public static URL buildUrlPopular(int page) {
        Uri builtUri = Uri.parse(POPULAR_MOVIES_URL).buildUpon()
                .appendQueryParameter(API_KEY_PARAM, THEMOVIEDB_API_KEY)
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

    public static URL buildUrlTopRated(int page) {
        Uri builtUri = Uri.parse(TOP_RATED__MOVIES_URL).buildUpon()
                .appendQueryParameter(API_KEY_PARAM, THEMOVIEDB_API_KEY)
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

    public static URL buildUrlMovie(String movieId) {
        Uri builtUri = Uri.parse(MOVIE_URL + movieId).buildUpon()
                .appendQueryParameter(API_KEY_PARAM, THEMOVIEDB_API_KEY)
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

    public static String run(URL url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager != null ? connectivityManager.getActiveNetworkInfo() : null;

        boolean isAvailable = activeNetworkInfo != null && activeNetworkInfo.isConnected();

        if(!isAvailable) {
            showDialogErrorNetwork(context);
        }

        return isAvailable;
    }

    public static void showDialogErrorNetwork(Context context){
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert);
        builder.setTitle(R.string.info)
                .setMessage(R.string.error_internet)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}
