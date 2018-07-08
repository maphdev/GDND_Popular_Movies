package com.example.manon.popularmovies.utils;

import android.content.ContentUris;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

    // Base
    private final static String MOVIES_BASE_URL = "https://api.themoviedb.org/3/movie";

    // MainActivity
    private final static String POPULAR_SORT = "popular";
    private final static String TOP_RATED_SORT = "top_rated";
    private final static String API_QUERY = "api_key";
    private final static String API_KEY = "9fc446524a3b5ebc71d910572efa87c8";

    // DetailsActivity images
    private final static String IMAGES_BASE_URL = "http://image.tmdb.org/t/p/";
    private final static String POSTER_SIZE = "w342/";
    private final static String BACKGROUND_SIZE = "w500/";

    // DetailsActivity trailer thumbnails
    private final static String THUMBNAILS_BASE_URL = "http://img.youtube.com/vi/";
    private final static String THUMBNAILS_END_URL = "0.jpg";

    // DetailsActivity Trailers
    private final static String TRAILER_ENDPOINT = "videos";

    // DetailsActivity Reviews
    private final static String REVIEWS_ENDPOINT = "reviews";

    // build URL by popular sort
    public static URL buildUrlByPopularSort() {
        Uri builtUri = Uri.parse(MOVIES_BASE_URL).buildUpon()
                .appendEncodedPath(POPULAR_SORT)
                .appendQueryParameter(API_QUERY, API_KEY)
                .build();

        return getURLfromUri(builtUri);
    }

    // build URL by top rated sort
    public static URL buildUrlByTopRated() {
        Uri builtUri = Uri.parse(MOVIES_BASE_URL).buildUpon()
                .appendEncodedPath(TOP_RATED_SORT)
                .appendQueryParameter(API_QUERY, API_KEY)
                .build();

        return getURLfromUri(builtUri);
    }

    // build poster URL
    public static URL buildPosterUrl(String poster_path) {
        Uri builtUri = Uri.parse(IMAGES_BASE_URL).buildUpon()
                .appendEncodedPath(POSTER_SIZE)
                .appendEncodedPath(poster_path)
                .build();

        return getURLfromUri(builtUri);
    }

    // build background URL
    public static URL buildBackgroundUrl(String poster_path) {
        Uri builtUri = Uri.parse(IMAGES_BASE_URL).buildUpon()
                .appendEncodedPath(BACKGROUND_SIZE)
                .appendEncodedPath(poster_path)
                .build();

        return getURLfromUri(builtUri);
    }

    // build trailer thumbnail URL
    public static URL buildTrailerThumbnailUrl(String trailerPath){
        Uri builtUri = Uri.parse(THUMBNAILS_BASE_URL).buildUpon()
                .appendEncodedPath(trailerPath)
                .appendEncodedPath(THUMBNAILS_END_URL)
                .build();
        return getURLfromUri(builtUri);
    }

    // build trailer URL
    public static URL buildURLTrailers(String id) {
        Uri builtUri = Uri.parse(MOVIES_BASE_URL).buildUpon()
                .appendEncodedPath(id)
                .appendEncodedPath(TRAILER_ENDPOINT)
                .appendQueryParameter(API_QUERY, API_KEY)
                .build();
        return getURLfromUri(builtUri);
    }

    // build reviews URL
    public static URL buildURLReviews(String id) {
        Uri builtUri = Uri.parse(MOVIES_BASE_URL).buildUpon()
                .appendEncodedPath(id)
                .appendEncodedPath(REVIEWS_ENDPOINT)
                .appendQueryParameter(API_QUERY, API_KEY)
                .build();
        return getURLfromUri(builtUri);
    }

    // build single movie URL
    public static URL buildURLMovieById(String id){
        Uri builtUri = Uri.parse(MOVIES_BASE_URL).buildUpon()
                .appendEncodedPath(id)
                .appendQueryParameter(API_QUERY, API_KEY)
                .build();
        return getURLfromUri(builtUri);
    }

    // helper to transform Uri to URL
    private static URL getURLfromUri(Uri uri) {
        URL url = null;
        try {
            url = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    // get URL response
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

    // check if the Network is available
    public static boolean isNetworkAvailable(Context context){
        int[] networkTypes = {ConnectivityManager.TYPE_MOBILE, ConnectivityManager.TYPE_WIFI};
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            for (int networkType : networkTypes) {
                NetworkInfo networkInfo = cm.getNetworkInfo(networkType);
                if (networkInfo != null && networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }
}
