package com.example.appcovid.controller;

import com.example.appcovid.model.GPSFeed;
import com.example.appcovid.model.RestrictionFeed;
import com.example.appcovid.model.RestrictionsItems;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RestrictionsService {

    @GET("restriction?")
    Call<List<RestrictionFeed>> getRestrictions(@Query("place") String city, @Query("token") String key);
}
