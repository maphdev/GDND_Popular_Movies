package com.example.manon.popularmovies.activity;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.manon.popularmovies.R;
import com.example.manon.popularmovies.model.Movie;
import com.example.manon.popularmovies.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class DetailsActivity extends AppCompatActivity {

    private Boolean favorite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        
        Movie movie = getIntent().getParcelableExtra("movie_parcel");
        setTitle("Movie details");

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

        // display vote average stars
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

        // display favorite
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

    public static double roundToHalfPoint(double d) {
        return Math.round(d * 2) / 2.0;
    }
}
