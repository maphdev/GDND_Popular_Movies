package com.example.manon.popularmovies.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.manon.popularmovies.R;
import com.example.manon.popularmovies.model.Movie;
import com.example.manon.popularmovies.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.List;


/**
 * Created by manon on 19/02/2018.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder>{

    private static List<Movie> listMovies;
    private Context context;
    final private ListItemClickListener mOnClickListener;

    public MovieAdapter(List<Movie> listMovies, Context context, ListItemClickListener listener) {
        this.listMovies = listMovies;
        this.context = context;
        mOnClickListener = listener;
        this.setHasStableIds(true);
    }

    public List<Movie> getListMovies(){
        return this.listMovies;
    }

    public void setListMovies(List<Movie> newListMovies) {
        listMovies = newListMovies;
    }

    @Override
    public long getItemId(int position) {
        return listMovies.get(position).getId();
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
        URL urlImage = NetworkUtils.buildImageUrl(listMovies.get(position).getPosterPath());
        Picasso.with(this.context).load(urlImage.toString()).into(holder.posterView);
    }

    @Override
    public int getItemCount() {
        return listMovies.size();
    }

    public interface ListItemClickListener {
        void onListItemClick(int clickedItemIndex);

    }

    public class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView posterView;

        public MovieViewHolder(View itemView) {
            super(itemView);
            posterView = (ImageView) itemView.findViewById(R.id.posterView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListItemClick(clickedPosition);
        }
    }


}
