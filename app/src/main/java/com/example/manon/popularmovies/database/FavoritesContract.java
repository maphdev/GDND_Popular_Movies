package com.example.manon.popularmovies.database;

import android.provider.BaseColumns;

public class FavoritesContract {
    public static final class FavoritesEntry implements BaseColumns{
        public static final String TABLE_NAME = "user_favorites";
        public static final String COLUMN_MOVIE_NAME = "movie_name";
        public static final String COLUMN_MOVIE_ID = "movie_id";
    }
}
