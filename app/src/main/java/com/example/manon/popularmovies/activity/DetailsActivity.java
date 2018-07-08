package com.example.manon.popularmovies.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.ConnectivityManager;
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
import android.widget.TextView;
import android.widget.Toast;

import com.example.manon.popularmovies.R;
import com.example.manon.popularmovies.adapter.ReviewAdapter;
import com.example.manon.popularmovies.adapter.TrailerAdapter;
import com.example.manon.popularmovies.database.FavoritesContract;
import com.example.manon.popularmovies.database.FavoritesDbHelper;
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

    // LoaderAsyncTask
    private static final int DETAILS_SEARCH_LOADER = 9031996;

    // adapters
    private TrailerAdapter trailerAdapter;
    private ReviewAdapter reviewAdapter;

    // database
    private SQLiteDatabase mDb;
    private FavoritesDbHelper dbHelper = new FavoritesDbHelper(this);

    public static final String PARCEL_NAME = "movie_parcel";
    public static final String MOVIE_ID = "movie_id";
    public static final String TRAILER_RESULT = "TRAILER_RESULT";
    public static final String REVIEW_RESULT = "REVIEW_RESULT";


    // YOUTUBE query
    public static final String SCHEME = "https";
    public static final String AUTHORITY = "www.youtube.com";
    public static final String PATH = "watch";
    public static final String QUERY_PARAM = "v";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Movie movie = getIntent().getParcelableExtra(PARCEL_NAME);
        setTitle(getResources().getString(R.string.details_activity_title));

        // display movie components
        displayMovieComponents(movie);

        // display vote average stars
        displayRateStars(movie);

        // display favorite
        displayIsFavorite(movie);

        // setTrailerAdapter
        setTrailerAdapter();

        // setReviewAdapter
        setReviewAdapter();

        // start AsyncTaskLoader : query trailers & reviews
        Bundle queryBundle = new Bundle();
        queryBundle.putString(MOVIE_ID, movie.getId().toString());

        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<Bundle> trailersAndReviewsLoader = loaderManager.getLoader(DETAILS_SEARCH_LOADER);

        if (trailersAndReviewsLoader == null){
            loaderManager.initLoader(DETAILS_SEARCH_LOADER, queryBundle, this);
        } else {
            loaderManager.restartLoader(DETAILS_SEARCH_LOADER, queryBundle, this);
        }
    }

    // display movie components
    private void displayMovieComponents(Movie movie){
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
    private void displayIsFavorite(final Movie movie){

        final FloatingActionButton floatingActionButton = findViewById(R.id.floatingActionButton);

        if (isInUserFavorites(movie)){
            floatingActionButton.setImageResource(R.drawable.ic_favorite_black_24dp);
        } else {
            floatingActionButton.setImageResource(R.drawable.ic_favorite_border_black_24dp);
        }

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isInUserFavorites(movie)){
                    removeFavorite(movie);
                    floatingActionButton.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                } else {
                    addFavorite(movie);
                    floatingActionButton.setImageResource(R.drawable.ic_favorite_black_24dp);
                }
            }
        });
    }

    // display vote average stars
    private void displayRateStars(Movie movie){
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

    private static double roundToHalfPoint(double d) {
        return Math.round(d * 2) / 2.0;
    }

    // set trailerAdapter for trailers
    private void setTrailerAdapter(){

        RecyclerView recyclerView;

        recyclerView = (RecyclerView) findViewById(R.id.trailerRecyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setHasFixedSize(true);

        trailerAdapter = new TrailerAdapter(Arrays.asList(""), this, this);
        recyclerView.setAdapter(trailerAdapter);
    }

    // set trailerAdapter for reviews
    private void setReviewAdapter(){

        RecyclerView recyclerView;

        recyclerView = (RecyclerView) findViewById(R.id.reviewRecyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setHasFixedSize(true);

        reviewAdapter = new ReviewAdapter(Arrays.asList(""), Arrays.asList(""));
        recyclerView.setAdapter(reviewAdapter);
    }

    // set the trailerAdapter listener's action = youtube intent
    @Override
    public void onListItemClicked(int clickedItemIndex, String key) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(SCHEME)
                .authority(AUTHORITY)
                .appendEncodedPath(PATH)
                .appendQueryParameter(QUERY_PARAM, key);

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
                String movieId = bundle.getString(MOVIE_ID);

                if (!NetworkUtils.isNetworkAvailable(getContext())){
                    return null;
                }

                Bundle myStrings = new Bundle();
                try {
                    URL trailersURL = NetworkUtils.buildURLTrailers(movieId);
                    String trailerResult = NetworkUtils.getResponseFromHttpUrl(trailersURL);
                    myStrings.putString(TRAILER_RESULT, trailerResult);

                    URL reviewsURL = NetworkUtils.buildURLReviews(movieId);
                    String reviewsResult = NetworkUtils.getResponseFromHttpUrl(reviewsURL);
                    myStrings.putString(REVIEW_RESULT, reviewsResult);
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
            Toast toast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_connection_msg2), Toast.LENGTH_LONG);
            toast.show();
            TextView trailerText = (TextView) findViewById(R.id.trailersText);
            RecyclerView recyclerTrailer = (RecyclerView) findViewById(R.id.trailerRecyclerView);
            TextView reviewText = (TextView) findViewById(R.id.reviewsText);
            RecyclerView recyclerReview = (RecyclerView) findViewById(R.id.reviewRecyclerView);
            trailerText.setVisibility(View.GONE);
            recyclerTrailer.setVisibility(View.GONE);
            reviewText.setVisibility(View.GONE);
            recyclerReview.setVisibility(View.GONE);
        } else {
            List<String> listKeys = JsonUtils.parseTrailerKeysJson(bundle.getString(TRAILER_RESULT));
            trailerAdapter.setTrailerList(listKeys);
            trailerAdapter.notifyDataSetChanged();

            List<String> listAuthors = JsonUtils.parseAuthorKeysJson(bundle.getString(REVIEW_RESULT));
            reviewAdapter.setListAuthors(listAuthors);

            List<String> listReviews = JsonUtils.parseReviewKeysJson(bundle.getString(REVIEW_RESULT));
            reviewAdapter.setListReviews(listReviews);

            reviewAdapter.notifyDataSetChanged();

            if (listReviews.size() == 0){
                TextView noReviewTextView = (TextView) findViewById(R.id.noReviewTxtView);
                noReviewTextView.setText(getResources().getString(R.string.no_review));
                noReviewTextView.setVisibility(View.VISIBLE);
            }

        }
    }

    @Override
    public void onLoaderReset(Loader<Bundle> loader) {

    }

    // add movie in favorite database
    private void addFavorite(Movie movie){
        ContentValues cv = new ContentValues();
        cv.put(FavoritesContract.FavoritesEntry.COLUMN_MOVIE_NAME, movie.getTitle());
        cv.put(FavoritesContract.FavoritesEntry.COLUMN_MOVIE_ID, movie.getId());
        Uri uri = getContentResolver().insert(FavoritesContract.FavoritesEntry.CONTENT_URI, cv);
     }

    // remove movie in favorite database
    private void removeFavorite(Movie movie){
        Uri uri = FavoritesContract.FavoritesEntry.CONTENT_URI.buildUpon().appendEncodedPath(Integer.toString(movie.getId())).build();
        getContentResolver().delete(uri, null, null);
    }

    // is that movie in the user's favorites ?
    private boolean isInUserFavorites(Movie movie){
        mDb = dbHelper.getReadableDatabase();
        String query = "SELECT * FROM " + FavoritesContract.FavoritesEntry.TABLE_NAME + " WHERE " + FavoritesContract.FavoritesEntry.COLUMN_MOVIE_ID + " = " + movie.getId();

        Cursor cursor = mDb.rawQuery(query, null);
        if (cursor.getCount() <= 0){
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }
}
