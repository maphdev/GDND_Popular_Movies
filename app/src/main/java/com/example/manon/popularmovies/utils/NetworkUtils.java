package com.example.manon.popularmovies.utils;

import android.net.Uri;
import android.os.AsyncTask;

import com.example.manon.popularmovies.model.Movie;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

/**
 * Created by manon on 20/02/2018.
 */

public class NetworkUtils {
    final static String MOVIES_BASE_URL = "https://api.themoviedb.org/3/";
    final static String POPULAR_SORT = "movie/popular";
    final static String TOP_RATED_SORT = "movie/top_rated";
    final static String API_QUERY = "api_key";
    final static String API_KEY = "9fc446524a3b5ebc71d910572efa87c8";

    final static String IMAGES_BASE_URL = "http://image.tmdb.org/t/p/";
    final static String IMAGE_SIZE = "w342/";

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

    public static URL buildImageUrl(String poster_path) {
        Uri builtUri = Uri.parse(IMAGES_BASE_URL).buildUpon()
                .appendEncodedPath(IMAGE_SIZE)
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
