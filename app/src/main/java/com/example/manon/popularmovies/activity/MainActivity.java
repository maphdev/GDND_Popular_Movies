package com.example.manon.popularmovies.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.manon.popularmovies.adapter.MovieAdapter;
import com.example.manon.popularmovies.R;
import com.example.manon.popularmovies.utils.NetworkUtils;

public class MainActivity extends AppCompatActivity {

    private MovieAdapter adapter;
    private RecyclerView recycler;
    GridLayoutManager layoutManager;
    Menu currentMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recycler = (RecyclerView) findViewById(R.id.recyclerview_posters);
        layoutManager = new GridLayoutManager(this, 2);
        recycler.setLayoutManager(layoutManager);
        recycler.setHasFixedSize(true);

        adapter = new MovieAdapter(getApplicationContext());
        recycler.setAdapter(adapter);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        currentMenu = menu;
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int itemThatWasClickedId = item.getItemId();
        if (itemThatWasClickedId == R.id.action_search) {
            Toast toast = Toast.makeText(getApplicationContext(), "search", Toast.LENGTH_SHORT);
            toast.show();
            return true;
        } else if (itemThatWasClickedId == R.id.action_sort_popular) {
            adapter.setListMovies(NetworkUtils.getListMovieFromURL(NetworkUtils.buildUrlByPopularSort()));
            adapter.notifyDataSetChanged();
            layoutManager.scrollToPosition(0);
            currentMenu.findItem(R.id.action_sort_popular).setVisible(false);
            currentMenu.findItem(R.id.action_sort_top_rated).setVisible(true);
            setTitle("Popular Movies");
            return true;
        } else if (itemThatWasClickedId == R.id.action_sort_top_rated) {
            adapter.setListMovies(NetworkUtils.getListMovieFromURL(NetworkUtils.buildUrlByTopRated()));
            adapter.notifyDataSetChanged();
            layoutManager.scrollToPosition(0);
            currentMenu.findItem(R.id.action_sort_popular).setVisible(true);
            currentMenu.findItem(R.id.action_sort_top_rated).setVisible(false);
            setTitle("Top rated movies");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
