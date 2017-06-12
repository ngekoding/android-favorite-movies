package com.example.nur.finalproject;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.example.nur.finalproject.adapter.MovieAdapter;
import com.example.nur.finalproject.helper.ConnectivityReceiver;
import com.example.nur.finalproject.model.Movie;
import com.example.nur.finalproject.model.MovieWrapper;
import com.example.nur.finalproject.network.ApiClient;
import com.example.nur.finalproject.network.ApiService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements MovieAdapter.OnItemClickListener,
        ConnectivityReceiver.ConnectivityReceiverListener {

    private RecyclerView recyclerView;
    private MovieAdapter adapter;
    private ProgressBar progressBar;
    private LinearLayout progressWrapper;
    private Toolbar toolbar;

    private Snackbar snackbar;

    private List<Movie> movieList = null;

    private String orderBy = "popular";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.app_name));

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        progressWrapper = (LinearLayout) findViewById(R.id.progress_wrapper);
        // Layout manager
        useGridByOrientation(getResources().getConfiguration());

        // Load movies
        getMovies(orderBy);

        Log.e("CONTOH", "Bismillah, semoga berhasil.");
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyApplication.getInstance().setConnectivityListener(this);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        useGridByOrientation(newConfig);
    }

    private void useGridByOrientation(Configuration config) {
        if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        } else if (config.orientation == Configuration.ORIENTATION_PORTRAIT) {
            recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_favorites:
                startActivity(new Intent(MainActivity.this, FavoriteActivity.class));
                break;
            case R.id.action_order_popular:
                getMovies("popular");
                break;
            case R.id.action_order_top_rated:
                getMovies("top_rated");
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getMovies(String ordered) {

        orderBy = ordered;

        if (!isConnected()) {
            showSnackbar("No internet connection.");
            return;
        }

        ApiService service = ApiClient.getRetrofit().create(ApiService.class);
        Call<MovieWrapper> call = ordered.equals("top_rated") ? service.getTopRatedMovies() : service.getPopularMovies();

        // Show progress bar
        showProgressBar();

        call.enqueue(new Callback<MovieWrapper>() {
            @Override
            public void onResponse(Call<MovieWrapper> call, Response<MovieWrapper> response) {
                movieList = response.body().getMovieList();

                adapter = new MovieAdapter(movieList, getApplicationContext());
                adapter.setOnItemClickListener(MainActivity.this);
                recyclerView.setAdapter(adapter);

                // Hide progress bar
                hideProgressBar();
            }

            @Override
            public void onFailure(Call<MovieWrapper> call, Throwable t) {

            }
        });
    }

    @Override
    public void onItemClick(int position) {
        if (movieList == null) return;

        Movie movie = movieList.get(position);

        Intent intent = new Intent(MainActivity.this, DetailActivity.class);

        intent.putExtra("EXTRA_ID", movie.getId());
        intent.putExtra("EXTRA_IMAGE_PATH", movie.getImagePath());
        intent.putExtra("EXTRA_TITLE", movie.getTitle());
        intent.putExtra("EXTRA_DESCRIPTION", movie.getDescription());
        intent.putExtra("EXTRA_RELEASE_DATE", movie.getReleaseDate());
        intent.putExtra("EXTRA_RATING", movie.getRating());

        startActivity(intent);
    }

    @Override
    public void onLongItemClick(int position) {

    }

    private void showProgressBar() {
        recyclerView.setVisibility(View.INVISIBLE);
        progressWrapper.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        recyclerView.setVisibility(View.VISIBLE);
        progressWrapper.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (isConnected) {
            hideSnackbar();
            getMovies(orderBy);
        } else {
            showSnackbar("No internet connection.");
            hideProgressBar();
        }
    }

    private boolean isConnected() {
        return ConnectivityReceiver.isConnected();
    }

    private void showSnackbar(String msg) {
        snackbar = Snackbar.make(recyclerView, msg, Snackbar.LENGTH_INDEFINITE);
        snackbar.show();
    }

    private void hideSnackbar() {
        snackbar.dismiss();
    }
}
