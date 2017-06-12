package com.example.nur.finalproject.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Nur on 5/24/2017.
 */

public class MovieWrapper {

    @SerializedName("results")
    private List<Movie> movieList;

    public List<Movie> getMovieList() {
        return movieList;
    }
}
