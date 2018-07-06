package com.example.manon.popularmovies.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.example.manon.popularmovies.adapter.MovieAdapter;
import com.example.manon.popularmovies.R;
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

        adapter = new MovieAdapter(getListMovieFromURL(NetworkUtils.buildUrlByPopularSort()), this, this);
        recycler.setAdapter(adapter);
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
            adapter.setListMovies(getListMovieFromURL(NetworkUtils.buildUrlByPopularSort()));
            adapter.notifyDataSetChanged();
            layoutManager.scrollToPosition(0);
            currentMenu.findItem(R.id.action_sort_popular).setVisible(false);
            currentMenu.findItem(R.id.action_sort_top_rated).setVisible(true);
            setTitle("Popular Movies");
            return true;
        } else if (itemThatWasClickedId == R.id.action_sort_top_rated) {
            adapter.setListMovies(getListMovieFromURL(NetworkUtils.buildUrlByTopRated()));
            adapter.notifyDataSetChanged();
            layoutManager.scrollToPosition(0);
            currentMenu.findItem(R.id.action_sort_popular).setVisible(true);
            currentMenu.findItem(R.id.action_sort_top_rated).setVisible(false);
            setTitle("Top rated movies");
            return true;
        } else if (itemThatWasClickedId == R.id.action_show_favorites) {
            // action
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // retrieve movies list
    public List<Movie> getListMovieFromURL(URL url) {
        List<Movie> listMovies = null;
        AsyncTask<URL, Void, List<Movie>> async = new MoviesQueryTask().execute(url);
        try {
            listMovies = async.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return listMovies;
    }

    public class MoviesQueryTask extends AsyncTask<URL, Void, List<Movie>>{
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
                listMovies = JsonUtils.parseMovieJson(resultQuery);
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

    // set an action when we click on a RecylerView's item
    @Override
    public void onListItemClick(int clickedItemIndex) {
        Context context = MainActivity.this;
        Class destinationClass = DetailsActivity.class;

        Intent startDetailsActivity = new Intent(context, destinationClass);
        Movie movie = adapter.getListMovies().get(clickedItemIndex);
        startDetailsActivity.putExtra("movie_parcel", movie);

        startActivity(startDetailsActivity);
    }
}
