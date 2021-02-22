package com.example.appcovid.controller;

import com.example.appcovid.model.GPSFeed;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface GPSService {

    @GET("{lat},{lon}?geoit=json&auth=113925367106775e15757233x1626")
    Call<GPSFeed> getAddress(@Path("lat") double lat, @Path("lon") double lon);
}