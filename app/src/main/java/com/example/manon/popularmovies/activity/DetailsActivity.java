package com.example.manon.popularmovies.activity;

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.manon.popularmovies.R;
import com.example.manon.popularmovies.adapter.TrailerAdapter;
import com.example.manon.popularmovies.model.Movie;
import com.example.manon.popularmovies.utils.JsonUtils;
import com.example.manon.popularmovies.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DetailsActivity extends AppCompatActivity implements TrailerAdapter.ListItemClickListener, LoaderManager.LoaderCallbacks<Bundle> {

    private static final int DETAILS_SEARCH_LOADER = 9031996;

    private Boolean favorite;
    TrailerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Movie movie = getIntent().getParcelableExtra("movie_parcel");
        setTitle("Movie details");

        // display movie components
        displayMovieComponents(movie);

        // display vote average stars
        displayRateStars(movie);

        // display favorite
        displayIsFavorite();

        // setTrailerAdapter
        setTrailerAdapter();

        Bundle queryBundle = new Bundle();
        queryBundle.putString("MOVIE_ID", movie.getId().toString());

        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<Bundle> trailersAndReviewsLoader = loaderManager.getLoader(DETAILS_SEARCH_LOADER);

        if (trailersAndReviewsLoader == null){
            loaderManager.initLoader(DETAILS_SEARCH_LOADER, queryBundle, this);
        } else {
            loaderManager.restartLoader(DETAILS_SEARCH_LOADER, queryBundle, this);
        }
    }

    // display movie components
    public void displayMovieComponents(Movie movie){
        ImageView background = findViewById(R.id.background);
        URL urlImageBackground = NetworkUtils.buildBackgroundUrl(movie.getBackdropPath());
        Picasso.with(this).load(urlImageBackground.toString()).into(background);

        ImageView poster = findViewById(R.id.poster);
        URL urlImagePoster = NetworkUtils.buildPosterUrl(movie.getPosterPath());
        Picasso.with(this).load(urlImagePoster.toString()).into(poster);

        TextView title = findViewById(R.id.title);
        title.setText(movie.getTitle());

        TextView overview = findViewById(R.id.synopsis_text);
        overview.setText(movie.getOverview());

        TextView releaseDate = findViewById(R.id.release_date);
        releaseDate.setText(movie.getReleaseDate());
    }

    // display favorite
    public void displayIsFavorite(){
        favorite = false;
        final FloatingActionButton floatingActionButton = findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                favorite = !favorite;
                if (favorite)
                    floatingActionButton.setImageResource(R.drawable.ic_favorite_black_24dp);
                else
                    floatingActionButton.setImageResource(R.drawable.ic_favorite_border_black_24dp);
            }
        });
    }

    // display vote average stars
    public void displayRateStars(Movie movie){
        List<ImageView> listStars = new ArrayList<>();
        listStars.add((ImageView)findViewById(R.id.star1));
        listStars.add((ImageView)findViewById(R.id.star2));
        listStars.add((ImageView)findViewById(R.id.star3));
        listStars.add((ImageView)findViewById(R.id.star4));
        listStars.add((ImageView)findViewById(R.id.star5));
        double voteAverage = Math.round(movie.getVoteAverage());
        int starsIndice = 0;
        while(voteAverage >=2) {
            voteAverage-=2;
            listStars.get(starsIndice).setBackground(getResources().getDrawable(R.drawable.ic_star_black_24dp));
            starsIndice += 1;
        }
        if (voteAverage >= 1) {
            listStars.get(starsIndice).setBackground(getResources().getDrawable(R.drawable.ic_star_half_black_24dp));
        }
    }

    public static double roundToHalfPoint(double d) {
        return Math.round(d * 2) / 2.0;
    }

    // set adapter for trailers
    public void setTrailerAdapter(){

        RecyclerView recyclerView;

        recyclerView = (RecyclerView) findViewById(R.id.trailerRecyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setHasFixedSize(true);

        adapter = new TrailerAdapter(Arrays.asList(""), this, this);
        recyclerView.setAdapter(adapter);
    }

    // set the trailer adapter listener's action
    @Override
    public void onListItemClicked(int clickedItemIndex, String key) {
        Log.v("TRY", Integer.toString(clickedItemIndex));

        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority("www.youtube.com")
                .appendEncodedPath("watch")
                .appendQueryParameter("v", key);

        Uri builtUri = builder.build();

        Intent intent = new Intent(Intent.ACTION_VIEW, builtUri);

        if (intent.resolveActivity(getPackageManager()) != null){
            startActivity(intent);
        }
    }

    // AsyncTaskLoader for trailers and reviews
    @Override
    public Loader<Bundle> onCreateLoader(int i, final Bundle bundle) {
        return new AsyncTaskLoader<Bundle>(this) {
            Bundle myStrings;

            @Override
            protected void onStartLoading() {
                if (bundle == null){
                    return;
                }

                if (myStrings != null){
                    deliverResult(myStrings);
                } else {
                    forceLoad();
                }
            }

            @Override
            public Bundle loadInBackground() {
                String movieId = bundle.getString("MOVIE_ID");

                Bundle myStrings = new Bundle();
                try {
                    URL trailersURL = NetworkUtils.buildURLTrailers(movieId);
                    String trailerResult = NetworkUtils.getResponseFromHttpUrl(trailersURL);
                    myStrings.putString("TRAILER_RESULT", trailerResult);

                    URL reviewsURL = NetworkUtils.buildURLReviews(movieId);
                    String reviewsResult = NetworkUtils.getResponseFromHttpUrl(reviewsURL);
                    myStrings.putString("REVIEWS_RESULT", reviewsResult);
                } catch (IOException e){
                    e.printStackTrace();
                }
                return myStrings;
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Bundle> loader, Bundle bundle) {
        if(bundle == null){
            Log.v("TRY", "no data");
        } else {
            List<String> listKeys = JsonUtils.parseTrailerKeysJson(bundle.getString("TRAILER_RESULT"));
            adapter.setTrailerList(listKeys);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onLoaderReset(Loader<Bundle> loader) {

    }
}
