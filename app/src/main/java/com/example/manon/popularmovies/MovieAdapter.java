package com.example.manon.popularmovies;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created by manon on 19/02/2018.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder>{

    //private List<Movie> listMovie;

    public MovieAdapter() {

    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view = inflater.inflate(R.layout.item_view, parent, false);

        MovieViewHolder viewHolder = new MovieViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        holder.posterView.setImageResource(R.drawable.poster);
    }

    @Override
    public int getItemCount() {
        //return listMovie.size();
        return 10;
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder {

        ImageView posterView;

        public MovieViewHolder(View itemView) {
            super(itemView);
            posterView = (ImageView) itemView.findViewById(R.id.posterView);
        }
    }

}
