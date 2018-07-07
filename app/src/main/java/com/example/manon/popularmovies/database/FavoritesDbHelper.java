package com.example.manon.popularmovies.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class FavoritesDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "favorites.db";
    public static final int DATABASE_VERSION = 1;

    public FavoritesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // CREATE TABLE user_favorites (id INTEGER PRIMARY KEY AUTOINCREMENT,movie_name TEXT NOT NULL,movie_id INTEGER NOT NULL);
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_FAVORITE_TABLE = "CREATE TABLE " +
                FavoritesContract.FavoritesEntry.TABLE_NAME + " (" +
                FavoritesContract.FavoritesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                FavoritesContract.FavoritesEntry.COLUMN_MOVIE_NAME + " TEXT NOT NULL," +
                FavoritesContract.FavoritesEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL" + ");";
        sqLiteDatabase.execSQL(SQL_CREATE_FAVORITE_TABLE);

    }

    // DROP TABLE IF EXISTS user_favorites
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " +
                FavoritesContract.FavoritesEntry.TABLE_NAME;
        sqLiteDatabase.execSQL(SQL_DELETE_ENTRIES);
        onCreate(sqLiteDatabase);
    }
}
