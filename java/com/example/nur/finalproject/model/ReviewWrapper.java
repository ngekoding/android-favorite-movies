package com.example.nur.finalproject.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Nur on 5/29/2017.
 */

public class ReviewWrapper {

    @SerializedName("results")
    private List<Review> reviewList;

    public List<Review> getReviewList() {
        return reviewList;
    }
}
