package com.example.manon.popularmovies.utils;

import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by manon on 20/02/2018.
 */

public class NetworkUtils {
    private final static String MOVIES_BASE_URL = "https://api.themoviedb.org/3/";
    private final static String POPULAR_SORT = "movie/popular";
    private final static String TOP_RATED_SORT = "movie/top_rated";
    private final static String API_QUERY = "api_key";
    private final static String API_KEY = "9fc446524a3b5ebc71d910572efa87c8";

    private final static String IMAGES_BASE_URL = "http://image.tmdb.org/t/p/";
    private final static String POSTER_SIZE = "w342/";
    private final static String BACKGROUND_SIZE = "w500/";


    public static URL buildUrlByPopularSort() {
        Uri builtUri = Uri.parse(MOVIES_BASE_URL).buildUpon()
                .appendEncodedPath(POPULAR_SORT)
                .appendQueryParameter(API_QUERY, API_KEY)
                .build();

        return getURLfromUri(builtUri);
    }

    public static URL buildUrlByTopRated() {
        Uri builtUri = Uri.parse(MOVIES_BASE_URL).buildUpon()
                .appendEncodedPath(TOP_RATED_SORT)
                .appendQueryParameter(API_QUERY, API_KEY)
                .build();

        return getURLfromUri(builtUri);
    }

    public static URL buildPosterUrl(String poster_path) {
        Uri builtUri = Uri.parse(IMAGES_BASE_URL).buildUpon()
                .appendEncodedPath(POSTER_SIZE)
                .appendEncodedPath(poster_path)
                .build();

        return getURLfromUri(builtUri);
    }

    public static URL buildBackgroundUrl(String poster_path) {
        Uri builtUri = Uri.parse(IMAGES_BASE_URL).buildUpon()
                .appendEncodedPath(BACKGROUND_SIZE)
                .appendEncodedPath(poster_path)
                .build();

        return getURLfromUri(builtUri);
    }


    private static URL getURLfromUri(Uri uri) {
        URL url = null;
        try {
            url = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner sc = new Scanner(in);
            sc.useDelimiter("\\A");

            boolean hasInput = sc.hasNext();
            if (hasInput) {
                return sc.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}
