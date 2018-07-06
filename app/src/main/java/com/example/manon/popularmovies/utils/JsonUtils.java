package com.example.manon.popularmovies.utils;

import com.example.manon.popularmovies.model.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by manon on 20/02/2018.
 */

public class JsonUtils {
    private static final String RESULTS = "results";

    // movie
    private static final String POSTER_PATH = "poster_path";
    private static final String ADULT = "adult";
    private static final String OVERVIEW = "overview";
    private static final String RELEASE_DATE = "release_date";
    private static final String GENRE_IDS = "genre_ids";
    private static final String ID = "id";
    private static final String ORIGINAL_TITLE = "original_title";
    private static final String ORIGINAL_LANGUAGE = "original_language";
    private static final String TITLE = "title";
    private static final String BACKDROP_PATH = "backdrop_path";
    private static final String POPULARITY = "popularity";
    private static final String VOTE_COUNT = "vote_count";
    private static final String VIDEO = "video";
    private static final String VOTE_AVERAGE = "vote_average";

    public static List<Movie> parseMovieJson (String json) {

        List<Movie> listMovies = new ArrayList<>();

        JSONObject resultsPageObject;
        JSONArray resultsArray;

        String posterPath;
        boolean adult;
        String overview;
        String releaseDate;
        JSONArray genreIdsArray;
        List<Integer> genreIdsList;
        Integer id;
        String originalTitle;
        String originalLanguage;
        String title;
        String backdropPath;
        Double popularity;
        Integer voteCount;
        boolean video;
        Double voteAverage;

        try {
            resultsPageObject = new JSONObject(json);
            resultsArray = resultsPageObject.optJSONArray(RESULTS);

            listMovies = new ArrayList<>();

            for (int i = 0; i < resultsArray.length(); i++) {
                JSONObject oneResult = resultsArray.optJSONObject(i);

                posterPath = oneResult.optString(POSTER_PATH);
                adult = oneResult.optBoolean(ADULT);
                overview = oneResult.optString(OVERVIEW);
                releaseDate = oneResult.optString(RELEASE_DATE);
                genreIdsArray = oneResult.optJSONArray(GENRE_IDS);
                genreIdsList = new ArrayList<>();
                for (int j = 0; j < genreIdsArray.length(); j++) {
                    genreIdsList.add(genreIdsArray.optInt(j));
                }
                id = oneResult.optInt(ID);
                originalTitle = oneResult.optString(ORIGINAL_TITLE);
                originalLanguage = oneResult.optString(ORIGINAL_LANGUAGE);
                title = oneResult.optString(TITLE);
                backdropPath = oneResult.optString(BACKDROP_PATH);
                popularity = oneResult.optDouble(POPULARITY);
                voteCount = oneResult.optInt(VOTE_COUNT);
                video = oneResult.optBoolean(VIDEO);
                voteAverage = oneResult.optDouble(VOTE_AVERAGE);

                listMovies.add(new Movie(posterPath, adult, overview, releaseDate, genreIdsList, id, originalTitle, originalLanguage, title, backdropPath, popularity, voteCount, video, voteAverage));
            }
        } catch (final JSONException e) {
            e.printStackTrace();
        }

        return listMovies;
    }

    // trailer
    private static final String KEY = "key";

    public static List<String> parseTrailerKeysJson (String json) {

        List<String> listKeys = new ArrayList<>();

        JSONObject resultsPageObject;
        JSONArray resultsArray;
        String key;

        try {
            resultsPageObject = new JSONObject(json);
            resultsArray = resultsPageObject.optJSONArray(RESULTS);

            for (int i = 0; i < resultsArray.length(); i++) {
                JSONObject oneResult = resultsArray.optJSONObject(i);
                key = oneResult.getString(KEY);
                listKeys.add(key);
            }

        } catch (JSONException e){
            e.printStackTrace();
        }

        return listKeys;
    }

}
