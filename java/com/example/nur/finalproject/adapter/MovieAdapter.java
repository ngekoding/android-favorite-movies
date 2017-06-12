package com.example.nur.finalproject.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.nur.finalproject.R;
import com.example.nur.finalproject.model.Movie;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by Nur on 5/24/2017.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MyViewHolder> {

    private List<Movie> movieList;
    private Context context;

    private OnItemClickListener onItemClickListener;

    public MovieAdapter(List<Movie> movieList, Context context) {
        this.movieList = movieList;
        this.context = context;
    }

    public void setOnItemClickListener(final OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        if (movieList == null) return;
        Movie movie = movieList.get(position);
        holder.mRating.setText(String.valueOf(movie.getRating()));
        Glide.with(context).load(context.getResources().getString(R.string.IMAGE_URL) + movie.getImagePath())
                .centerCrop()
                .crossFade()
                .into(holder.mImage);
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        public ImageView mImage;
        public TextView mRating;

        public MyViewHolder(View itemView) {
            super(itemView);
            mImage = (ImageView) itemView.findViewById(R.id.iv_item_image);
            mRating = (TextView) itemView.findViewById(R.id.tv_item_rating);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onItemClickListener.onItemClick(getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View v) {
            onItemClickListener.onLongItemClick(getAdapterPosition());
            return true;
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);

        void onLongItemClick(int position);
    }
}
