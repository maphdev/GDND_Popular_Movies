package com.example.manon.popularmovies.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.manon.popularmovies.R;
import com.example.manon.popularmovies.model.Movie;
import com.example.manon.popularmovies.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.net.URL;

public class DetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Movie movie = (Movie) getIntent().getParcelableExtra("movie_parcel");

        ImageView background = (ImageView) findViewById(R.id.background);
        URL urlImageBackground = NetworkUtils.buildBackgroundUrl(movie.getBackdropPath());
        Picasso.with(this).load(urlImageBackground.toString()).into(background);

        ImageView poster = (ImageView) findViewById(R.id.poster);
        URL urlImagePoster = NetworkUtils.buildPosterUrl(movie.getPosterPath());
        Picasso.with(this).load(urlImagePoster.toString()).into(poster);

        TextView title = (TextView) findViewById(R.id.title);
        title.setText(movie.getTitle());

        TextView overview = (TextView) findViewById(R.id.synopsis_text) ;
        overview.setText(movie.getOverview());

        TextView releaseDate = (TextView) findViewById(R.id.release_date);
        releaseDate.setText(movie.getReleaseDate());

        TextView voteAverage = (TextView) findViewById(R.id.vote_average);
        voteAverage.setText(Double.toString(movie.getVoteAverage())+"/10");
    }
}
