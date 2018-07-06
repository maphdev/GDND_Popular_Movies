package com.example.manon.popularmovies.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.manon.popularmovies.R;
import com.example.manon.popularmovies.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.List;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerViewHolder>{

    private List<String> listTrailerKeysThumbnails;
    Context context;
    final private ListItemClickListener clickListener;

   public TrailerAdapter(List<String> listTrailerThumbnails, Context context, ListItemClickListener clickListener) {
       this.listTrailerKeysThumbnails = listTrailerThumbnails;
       this.context = context;
       this.clickListener = clickListener;
   }

   public void setTrailerList(List<String> listTrailers){
       this.listTrailerKeysThumbnails = listTrailers;
   }

    @Override
    public TrailerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view = inflater.inflate(R.layout.trailer_item_view, parent, false);

        TrailerViewHolder viewHolder = new TrailerViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(TrailerViewHolder holder, int position) {
        URL urlImage = NetworkUtils.buildTrailerThumbnailUrl(listTrailerKeysThumbnails.get(position));
        Picasso.with(this.context).load(urlImage.toString()).into(holder.trailerThumbnailView);
    }

    @Override
    public int getItemCount() {
        return listTrailerKeysThumbnails.size();
    }

    public class TrailerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView trailerThumbnailView;

        public TrailerViewHolder(View itemView) {
            super(itemView);
            trailerThumbnailView = (ImageView) itemView.findViewById(R.id.trailerThumbnail);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int clickedPosition = getAdapterPosition();
            String key = listTrailerKeysThumbnails.get(clickedPosition);
            clickListener.onListItemClicked(clickedPosition, key);
        }
    }

    public interface ListItemClickListener{
       void onListItemClicked(int clickedItemIndex, String key);
    }
}
