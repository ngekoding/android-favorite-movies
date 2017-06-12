package com.example.nur.finalproject.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Nur on 5/28/2017.
 */

public class TrailerWrapper {

    @SerializedName("results")
    private List<Trailer> trailerList;

    public List<Trailer> getTrailerList() {
        return trailerList;
    }
}
