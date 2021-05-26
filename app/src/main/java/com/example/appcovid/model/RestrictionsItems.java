package com.example.appcovid.model;

import com.google.gson.annotations.SerializedName;

public class RestrictionsItems {

    @SerializedName(value="title")
    private String mTitle;

    @SerializedName(value="description")
    private String mDescription;


    public String getmTitle() {
        return mTitle;
    }


    public String getmDescription() {
        return mDescription;
    }
}
