package com.example.appcovid.controller;

import androidx.annotation.NonNull;
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

/**
 * Clase que maneja los datos de cada noticia
 * @author Iván Moriche Damas
 * @author Rodrigo Garcia
 * @author Iustin Mocanu
 * @version 28/05/2021/A
 * @see ViewModel
 */
public class NewsViewModel extends ViewModel
{
    private MutableLiveData<List<RssItem>> mData;
    private static final String URL_RSS = "https://www.rtve.es/";

    /**
     * Método que devuelve la lista de las noticias
     * @return mData
     */
    public LiveData<List<RssItem>> getmData()
    {
        if(mData == null)
        {
            mData = new MutableLiveData<>();
            mData.setValue(new ArrayList<>());
            loadData();
        }

        return mData;
    }


    /**
     * Método que carga los datos y construye las llamada a la API
     * @deprecated SimpleXmlConverterFactory
     */
    private void loadData()
    {
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
        callAsync.enqueue(new Callback<RssFeed>()
        {
            @Override
            public void onResponse(@NonNull Call<RssFeed> call, @NonNull Response<RssFeed> response)
            {
                if (response.isSuccessful())
                {
                    // La API responde correctamente
                    assert response.body() != null;
                    mData.postValue(response.body().getmChannel().getItems());
                } else {
                    mData.postValue(null);
                }
            }

            @Override
            public void onFailure(@NonNull Call<RssFeed> call, @NonNull Throwable t)
            {
                mData.postValue(null);
            }
        });
    }
}