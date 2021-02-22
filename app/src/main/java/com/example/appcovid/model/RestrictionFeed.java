package com.example.appcovid.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RestrictionFeed {

    @SerializedName("restrictions")
    private List<RestrictionsItems> items;


    public List<RestrictionsItems> getItems() {
        return items;
    }
}
