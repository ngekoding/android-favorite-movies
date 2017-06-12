package com.example.nur.finalproject.network;

import com.example.nur.finalproject.model.Movie;
import com.example.nur.finalproject.model.MovieWrapper;
import com.example.nur.finalproject.model.ReviewWrapper;
import com.example.nur.finalproject.model.TrailerWrapper;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by Nur on 5/24/2017.
 */

public interface ApiService {

    String API_KEY = "YOUR_API_KEY";

    @GET("movie/popular?api_key=" + API_KEY)
    Call<MovieWrapper> getPopularMovies();

    @GET("movie/top_rated?api_key=" + API_KEY)
    Call<MovieWrapper> getTopRatedMovies();

    @GET("movie/{id}?api_key=" + API_KEY)
    Call<Movie> getDetailMovie(@Path("id") String id);

    @GET("movie/{id}/videos?api_key=" + API_KEY)
    Call<TrailerWrapper> getTrailers(@Path("id") String id);

    @GET("movie/{id}/reviews?api_key=" + API_KEY)
    Call<ReviewWrapper> getReviews(@Path("id") String id);
}
