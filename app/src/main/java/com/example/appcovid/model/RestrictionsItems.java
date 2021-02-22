package com.example.appcovid.model;

import com.google.gson.annotations.SerializedName;

public class RestrictionsItems {

    @SerializedName(value="title")
    private String mTitulo;

    @SerializedName(value="description")
    private String mDescripcion;


    public String getmTitulo() {
        return mTitulo;
    }


    public String getmDescripcion() {
        return mDescripcion;
    }
}
