package com.example.nur.finalproject;

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.nur.finalproject.adapter.ReviewAdapter;
import com.example.nur.finalproject.adapter.TrailerAdapter;
import com.example.nur.finalproject.handler.DatabaseHandler;
import com.example.nur.finalproject.helper.ConnectivityReceiver;
import com.example.nur.finalproject.model.Movie;
import com.example.nur.finalproject.model.Review;
import com.example.nur.finalproject.model.ReviewWrapper;
import com.example.nur.finalproject.model.Trailer;
import com.example.nur.finalproject.model.TrailerWrapper;
import com.example.nur.finalproject.network.ApiClient;
import com.example.nur.finalproject.network.ApiService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailActivity extends AppCompatActivity implements TrailerAdapter.OnItemClickListener,
        ConnectivityReceiver.ConnectivityReceiverListener, View.OnClickListener {

    private TextView detailRating, detailYear, detailDescription;
    private ImageView detailImage;
    private ProgressBar trailerProgress, reviewProgress;
    private TextView noTrailer, noReview;

    private List<Trailer> trailerList = new ArrayList<>();
    private List<Review> reviewList = new ArrayList<>();

    private TrailerAdapter trailerAdapter;
    private ReviewAdapter reviewAdapter;
    private RecyclerView trailerRecyclerView;
    private RecyclerView reviewRecyclerView;
    private FloatingActionButton btnFavorite;

    private Snackbar snackbar;

    private Toolbar toolbar;

    // From intent
    private String id, imagePath, title, description, releaseDate;
    private double rating;

    private DatabaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        getInit();
        getIntentExtras();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Change activity title with movie title
        getSupportActionBar().setTitle(title);

        // Set value
        detailRating.setText(String.valueOf(rating));
        detailYear.setText(releaseDate.substring(0, 4));
        detailDescription.setText(description);

        Glide.with(this).load(getResources().getString(R.string.IMAGE_URL) + imagePath)
                .fitCenter()
                .crossFade()
                .into(detailImage);

        trailerRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        reviewRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Trying only
//        trailerList.add(new Trailer("123", "Best trailer in year"));
//        trailerList.add(new Trailer("123", "You are my best"));
//        trailerList.add(new Trailer("123", "Another trailer here"));
//
//        trailerAdapter = new TrailerAdapter(trailerList);
//        trailerAdapter.setOnItemClickListener(this);
//        trailerRecyclerView.setAdapter(trailerAdapter);

        // Get data
        getTrailers(id);
        getReviews(id);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyApplication.getInstance().setConnectivityListener(this);
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
        toolbar = (Toolbar) findViewById(R.id.toolbar_detail);
        detailImage = (ImageView) findViewById(R.id.iv_detail_image);
        detailRating = (TextView) findViewById(R.id.tv_detail_rating);
        detailYear = (TextView) findViewById(R.id.tv_detail_year);
        detailDescription = (TextView) findViewById(R.id.tv_detail_description);
        noTrailer = (TextView) findViewById(R.id.tv_no_trailer);
        noReview = (TextView) findViewById(R.id.tv_no_review);
        trailerProgress = (ProgressBar) findViewById(R.id.pb_trailer);
        reviewProgress = (ProgressBar) findViewById(R.id.pb_review);
        trailerRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_trailers);
        reviewRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_reviews);
        btnFavorite = (FloatingActionButton) findViewById(R.id.fab_add_favorite);

        btnFavorite.setOnClickListener(this);
        db = new DatabaseHandler(this);
    }

    private void getIntentExtras() {
        Intent intent = getIntent();
        id = intent.getStringExtra("EXTRA_ID");
        imagePath = intent.getStringExtra("EXTRA_IMAGE_PATH");
        title = intent.getStringExtra("EXTRA_TITLE");
        description = intent.getStringExtra("EXTRA_DESCRIPTION");
        releaseDate = intent.getStringExtra("EXTRA_RELEASE_DATE");
        rating = intent.getDoubleExtra("EXTRA_RATING", 0);
    }

    private void getTrailers(String id) {

        if (!isConnected()) {
            showSnackbar("No internet connection.");
            return;
        }

        ApiService service = ApiClient.getRetrofit().create(ApiService.class);
        Call<TrailerWrapper> call = service.getTrailers(id);

        // Show trailerProgress
        showProgressBar();

        call.enqueue(new Callback<TrailerWrapper>() {
            @Override
            public void onResponse(Call<TrailerWrapper> call, Response<TrailerWrapper> response) {
                trailerList = response.body().getTrailerList();

                trailerAdapter = new TrailerAdapter(trailerList);
                trailerAdapter.setOnItemClickListener(DetailActivity.this);
                trailerRecyclerView.setAdapter(trailerAdapter);

                if (trailerList.size() == 0) {
                    noTrailer.setText("No trailers yet.");
                }

                // Hide trailerProgress
                hideProgressBar();
            }

            @Override
            public void onFailure(Call<TrailerWrapper> call, Throwable t) {

            }
        });
    }

    private void getReviews(String id) {

        if (!isConnected()) {
            showSnackbar("No internet connection.");
            return;
        }

        ApiService service = ApiClient.getRetrofit().create(ApiService.class);
        Call<ReviewWrapper> call = service.getReviews(id);

        // Show trailerProgress
        showReviewProgressBar();

        call.enqueue(new Callback<ReviewWrapper>() {
            @Override
            public void onResponse(Call<ReviewWrapper> call, Response<ReviewWrapper> response) {
                reviewList = response.body().getReviewList();

                reviewAdapter = new ReviewAdapter(reviewList);
                reviewRecyclerView.setAdapter(reviewAdapter);

                if (reviewList.size() == 0) {
                    noReview.setText("No reviews yet.");
                }

                // Hide trailerProgress
                hideReviewProgressBar();
            }

            @Override
            public void onFailure(Call<ReviewWrapper> call, Throwable t) {

            }
        });
    }

    private void showProgressBar() {
        trailerProgress.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        trailerProgress.setVisibility(View.INVISIBLE);
    }

    private void showReviewProgressBar() {
        reviewProgress.setVisibility(View.VISIBLE);
    }

    private void hideReviewProgressBar() {
        reviewProgress.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onItemClick(int position) {
        Trailer trailer = trailerList.get(position);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.YOUTUBE_URL) + trailer.getKey()));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setPackage("com.google.android.youtube");
        startActivity(intent);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (isConnected) {
            hideSnackbar();
            getTrailers(id);
        } else {
            showSnackbar("No internet connection.");
            hideProgressBar();
        }
    }

    private boolean isConnected() {
        return ConnectivityReceiver.isConnected();
    }

    private void showSnackbar(String msg) {
        snackbar = Snackbar.make(trailerRecyclerView, msg, Snackbar.LENGTH_INDEFINITE);
        snackbar.show();
    }

    private void hideSnackbar() {
        snackbar.dismiss();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_add_favorite:
                // Add to database
                if (db.alreadyExists(id)) {
                    Toast.makeText(this, "Already exists!", Toast.LENGTH_SHORT).show();
                } else {
                    Movie movie = new Movie();
                    movie.setId(id);
                    movie.setImagePath(imagePath);
                    movie.setTitle(title);
                    movie.setDescription(description);
                    movie.setReleaseDate(releaseDate);
                    movie.setRating(rating);

                    db.addMovie(movie);

                    Toast.makeText(this, "Added to favorites.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
