package com.example.appcovid.controller;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.appcovid.model.RssFeed;
import com.example.appcovid.model.RssItem;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

public class NewsViewModel extends ViewModel {

    private MutableLiveData<List<RssItem>> mData;
    private static final String URL_RSS = "https://www.rtve.es/";


    public LiveData<List<RssItem>> getmData() {
        if(mData == null) {
            mData = new MutableLiveData<List<RssItem>>();
            mData.setValue(new ArrayList<RssItem>());
            loadData();
        }

        return mData;
    }

    private void loadData() {
        // Se construye el retrofit
        Retrofit retrofit = new Retrofit
                .Builder()
                .baseUrl(URL_RSS)
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .build();
        RssService rssService = retrofit.create(RssService.class);

        // Se construye la llamada
        Call<RssFeed> callAsync = rssService.getFeed();

        // Se hace la llamada a la API
        callAsync.enqueue(new Callback<RssFeed>() {
            @Override
            public void onResponse(Call<RssFeed> call, Response<RssFeed> response) {
                if (response.isSuccessful()) {
                    // La API responde correctamente
                    mData.postValue(response.body().getmChannel().getItems());
                } else {
                    mData.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<RssFeed> call, Throwable t) {
                mData.postValue(null);
            }
        });
    }
}
