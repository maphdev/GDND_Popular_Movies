package com.example.manon.popularmovies.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.manon.popularmovies.adapter.MovieAdapter;
import com.example.manon.popularmovies.R;
import com.example.manon.popularmovies.database.FavoritesContract;
import com.example.manon.popularmovies.database.FavoritesDbHelper;
import com.example.manon.popularmovies.model.Movie;
import com.example.manon.popularmovies.utils.JsonUtils;
import com.example.manon.popularmovies.utils.NetworkUtils;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity implements MovieAdapter.ListItemClickListener {

    private MovieAdapter adapter;
    private RecyclerView recycler;
    private GridLayoutManager layoutManager;
    private Menu currentMenu;
    private ProgressBar loadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadingIndicator = findViewById(R.id.loading_indicator);

        recycler = findViewById(R.id.recyclerview_posters);
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            layoutManager = new GridLayoutManager(this, 2);
        } else {
            layoutManager = new GridLayoutManager(this, 4);
        }
        recycler.setLayoutManager(layoutManager);
        recycler.setHasFixedSize(true);

        adapter = new MovieAdapter(getListMoviesFromURL(NetworkUtils.buildUrlByPopularSort()), this, this);
        recycler.setAdapter(adapter);

        if(!NetworkUtils.isNetworkAvailable(getApplicationContext())){
            Toast toast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_connection_msg), Toast.LENGTH_LONG);
            toast.show();
        }
    }

    // so when we delete from favorites and press back button, it refreshes
    @Override
    public void onRestart()
    {
        super.onRestart();
        finish();
        startActivity(getIntent());
    }

    // inflate the menu
    public boolean onCreateOptionsMenu(Menu menu) {
        currentMenu = menu;
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    // set the actions for each menu item
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemThatWasClickedId = item.getItemId();
        if (itemThatWasClickedId == R.id.action_sort_popular) {
            adapter.setListMovies(getListMoviesFromURL(NetworkUtils.buildUrlByPopularSort()));
            adapter.notifyDataSetChanged();
            layoutManager.scrollToPosition(0);
            currentMenu.findItem(R.id.action_sort_popular).setVisible(false);
            currentMenu.findItem(R.id.action_sort_top_rated).setVisible(true);
            currentMenu.findItem(R.id.action_show_favorites).setVisible(true);
            setTitle(getResources().getString(R.string.popular_movies_title));
            return true;
        } else if (itemThatWasClickedId == R.id.action_sort_top_rated) {
            adapter.setListMovies(getListMoviesFromURL(NetworkUtils.buildUrlByTopRated()));
            adapter.notifyDataSetChanged();
            layoutManager.scrollToPosition(0);
            currentMenu.findItem(R.id.action_sort_popular).setVisible(true);
            currentMenu.findItem(R.id.action_sort_top_rated).setVisible(false);
            currentMenu.findItem(R.id.action_show_favorites).setVisible(true);
            setTitle(getResources().getString(R.string.top_rated_title));
            return true;
        } else if (itemThatWasClickedId == R.id.action_show_favorites) {
            adapter.setListMovies(getListMoviesFromDatabase());
            adapter.notifyDataSetChanged();
            layoutManager.scrollToPosition(0);
            currentMenu.findItem(R.id.action_sort_popular).setVisible(true);
            currentMenu.findItem(R.id.action_sort_top_rated).setVisible(true);
            currentMenu.findItem(R.id.action_show_favorites).setVisible(false);
            setTitle(getResources().getString(R.string.favorites_title));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // retrieve movies list from URL
    public List<Movie> getListMoviesFromURL(URL url) {
        List<Movie> listMovies = null;
        AsyncTask<URL, Void, List<Movie>> async = new MoviesEndpointsQueryTask().execute(url);
        try {
            listMovies = async.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return listMovies;
    }

    // retrieve movies list from database
    public List<Movie> getListMoviesFromDatabase(){
        Cursor listMoviesCursor = getContentResolver().query(FavoritesContract.FavoritesEntry.CONTENT_URI, new String[]{FavoritesContract.FavoritesEntry.COLUMN_MOVIE_ID}, null, null, FavoritesContract.FavoritesEntry.COLUMN_MOVIE_NAME);

        List<Integer> listMoviesId = new ArrayList<>();

        int movieIdCol = listMoviesCursor.getColumnIndex(FavoritesContract.FavoritesEntry.COLUMN_MOVIE_ID);
        while(listMoviesCursor.moveToNext()){
            int movieId = listMoviesCursor.getInt(movieIdCol);
            listMoviesId.add(movieId);
        }
        listMoviesCursor.close();

        List<Movie> listMovies = new ArrayList<>();
        for(int i =0; i < listMoviesId.size(); i++){
            URL urlMovie = NetworkUtils.buildURLMovieById(Integer.toString(listMoviesId.get(i)));
            AsyncTask<URL, Void, Movie> async = new MovieByIdQueryTask().execute(urlMovie);
            try {
                listMovies.add(async.get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        return listMovies;
    }
    // get movies from endpoints
    public class MoviesEndpointsQueryTask extends AsyncTask<URL, Void, List<Movie>>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<Movie> doInBackground(URL... params) {
            URL urlQuery = params[0];
            String resultQuery;
            List<Movie> listMovies = new ArrayList<>();
            try {
                resultQuery = NetworkUtils.getResponseFromHttpUrl(urlQuery);
                listMovies = JsonUtils.parseMoviesFromEndpointJson(resultQuery);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return listMovies;
        }

        @Override
        protected void onPostExecute(List<Movie> movies) {
            loadingIndicator.setVisibility(View.INVISIBLE);
        }
    }

    // get movie from id
    public class MovieByIdQueryTask extends AsyncTask<URL, Void, Movie>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected Movie doInBackground(URL... params) {
            URL urlQuery = params[0];
            String resultQuery;
            Movie movie = null;
            try {
                resultQuery = NetworkUtils.getResponseFromHttpUrl(urlQuery);
                movie = JsonUtils.parseMovieFromId(resultQuery);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return movie;
        }

        @Override
        protected void onPostExecute(Movie movie) {
            loadingIndicator.setVisibility(View.INVISIBLE);
        }
    }

    // set an action when we click on a RecylerView's item
    @Override
    public void onListItemClick(int clickedItemIndex) {
        Context context = MainActivity.this;
        Class destinationClass = DetailsActivity.class;

        Intent startDetailsActivity = new Intent(context, destinationClass);
        Movie movie = adapter.getListMovies().get(clickedItemIndex);
        startDetailsActivity.putExtra(DetailsActivity.PARCEL_NAME, movie);

        startActivity(startDetailsActivity);
    }
}
