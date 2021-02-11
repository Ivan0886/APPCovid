package com.example.appcovid.controller;

import com.example.appcovid.model.RssFeed;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

public class RssController {

    private static final String URL_RSS = "https://www.rtve.es/";
    private Retrofit.Builder builder = new Retrofit
            .Builder()
            .baseUrl(URL_RSS)
            .addConverterFactory(SimpleXmlConverterFactory.create());


    public void start(OnRssResponse callback) {
        Retrofit retrofit = builder.build();
        RssService rssService = retrofit.create(RssService.class);
        Call<RssFeed> callAsync = rssService.getFeed();

        callAsync.enqueue(new Callback<RssFeed>() {
            @Override
            public void onResponse(Call<RssFeed> call, Response<RssFeed> response) {
                if (response.isSuccessful()) {
                    // La API responde correctamente
                    callback.getRss(response.body());
                } else {
                    callback.getRss(null);
                }
            }

            @Override
            public void onFailure(Call<RssFeed> call, Throwable t) {
                callback.getRss(null);
            }
        });
    }
}