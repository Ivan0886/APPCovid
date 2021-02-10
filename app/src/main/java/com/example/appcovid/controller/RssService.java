package com.example.appcovid.controller;

import com.example.appcovid.model.RssFeed;

import retrofit2.Call;
import retrofit2.http.GET;

public interface RssService {

    @GET("api/tematicas/129647/noticias.rss")
    Call<RssFeed> getFeed();
}
