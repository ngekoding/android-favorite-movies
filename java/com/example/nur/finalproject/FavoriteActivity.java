package com.example.nur.finalproject;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.example.nur.finalproject.adapter.MovieAdapter;
import com.example.nur.finalproject.handler.DatabaseHandler;
import com.example.nur.finalproject.model.Movie;

import java.util.List;

public class FavoriteActivity extends AppCompatActivity implements MovieAdapter.OnItemClickListener {

    private Toolbar toolbar;
    private RecyclerView favoriteRecyclerView;
    private MovieAdapter favoriteAdapter;

    private List<Movie> movieList;
    private DatabaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        getInit();

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("My Favorites");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        movieList = db.getAllMovies();

        favoriteAdapter = new MovieAdapter(movieList, this);
        favoriteAdapter.setOnItemClickListener(this);

        useGridByOrientation(getResources().getConfiguration());
        favoriteRecyclerView.setAdapter(favoriteAdapter);

        registerForContextMenu(favoriteRecyclerView);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getInit() {
        favoriteRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_favorites);
        toolbar = (Toolbar) findViewById(R.id.toolbar_favorites);
        db = new DatabaseHandler(this);
    }

    private void useGridByOrientation(Configuration config) {
        if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            favoriteRecyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        } else if (config.orientation == Configuration.ORIENTATION_PORTRAIT) {
            favoriteRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        }
    }


    @Override
    public void onItemClick(int position) {
        if (movieList == null) return;

        Movie movie = movieList.get(position);

        Intent intent = new Intent(FavoriteActivity.this, DetailActivity.class);

        intent.putExtra("EXTRA_ID", movie.getId());
        intent.putExtra("EXTRA_IMAGE_PATH", movie.getImagePath());
        intent.putExtra("EXTRA_TITLE", movie.getTitle());
        intent.putExtra("EXTRA_DESCRIPTION", movie.getDescription());
        intent.putExtra("EXTRA_RELEASE_DATE", movie.getReleaseDate());
        intent.putExtra("EXTRA_RATING", movie.getRating());

        startActivity(intent);
    }

    @Override
    public void onLongItemClick(final int position) {
        final Movie movie = movieList.get(position);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(movie.getTitle());
        builder.setMessage("Are you sure to remove this movie from favorites?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                db.deleteMovie(movie.getId());
                movieList.remove(position);
                favoriteAdapter.notifyItemRemoved(position);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
