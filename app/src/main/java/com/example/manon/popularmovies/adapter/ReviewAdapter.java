package com.example.manon.popularmovies.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.manon.popularmovies.R;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>{

    List<String> listAuthors;
    List<String> listReviews;

    public ReviewAdapter(List<String> listAuthors, List<String> listReviews){
        this.listAuthors = listAuthors;
        this.listReviews = listReviews;
    }

    public void setListAuthors(List<String> listAuthors){
        this.listAuthors = listAuthors;
    }

    public void setListReviews(List<String> listReviews){
        this.listReviews = listReviews;
    }

    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view = inflater.inflate(R.layout.review_item_view, parent, false);

        ReviewViewHolder viewHolder = new ReviewViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ReviewViewHolder holder, int position) {
         String author = listAuthors.get(position);
         String review = listReviews.get(position);
         holder.authorTextView.setText(author);
         holder.reviewTextView.setText(review);
    }

    @Override
    public int getItemCount() {
        return listReviews.size();
    }

    public class ReviewViewHolder extends RecyclerView.ViewHolder {

        TextView authorTextView;
        TextView reviewTextView;

        public ReviewViewHolder(View itemView) {
            super(itemView);
            authorTextView = (TextView) itemView.findViewById(R.id.author);
            reviewTextView = (TextView) itemView.findViewById(R.id.review);
        }
    }
}
