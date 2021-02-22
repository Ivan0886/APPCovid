package com.example.appcovid.model;

import com.google.gson.annotations.SerializedName;

public class GPSFeed {

    @SerializedName(value="city")
    private String mCity;


    public String getmCity() {
        return mCity;
    }
}