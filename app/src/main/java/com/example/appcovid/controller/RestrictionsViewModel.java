package com.example.appcovid.controller;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.appcovid.model.BaseActivity;
import com.example.appcovid.model.GPSFeed;
import com.example.appcovid.model.GPSLocation;
import com.example.appcovid.model.RestrictionFeed;
import com.example.appcovid.model.RestrictionsItems;
import com.example.appcovid.model.RssFeed;
import com.example.appcovid.model.RssItem;
import com.example.appcovid.views.RestrictionsActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Clase que maneja los datos de cada restricción
 * @author Iván Moriche Damas
 * @author Rodrigo Garcia
 * @author Iustin Mocanu
 * @version 28/05/2021/A
 * @see ViewModel
 */
public class RestrictionsViewModel extends ViewModel
{
    private MutableLiveData<List<RestrictionsItems>> mData;
    private static final String URL_RES = "https://api.quecovid.es/restriction/";
    private boolean gps_enable = false;
    private boolean network_enable = false;

    /**
     * Método que devuelve la lista de las noticias
     * @return mData
     */
    public LiveData<List<RestrictionsItems>> getmData()
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
     */
    private void loadData()
    {
        // Se construye el retrofit
        Retrofit retrofit = new Retrofit
                .Builder()
                .baseUrl(URL_RES)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RestrictionsService restrictionsService = retrofit.create(RestrictionsService.class);

        //Log.d("POSTALCODE2", gpsLocation.getmPostalCode());

        // Se construye la llamada
        Call<List<RestrictionFeed>> callAsync = restrictionsService.getRestrictions("28032", "lR2I41RV8NhDuEkS51V8Z9NLJ");

        // Se hace la llamada a la API
        callAsync.enqueue(new Callback<List<RestrictionFeed>>()
        {
            @Override
            public void onResponse(@NonNull Call<List<RestrictionFeed>> call, @NonNull Response<List<RestrictionFeed>> response)
            {
                if (response.isSuccessful())
                {
                    // La API responde correctamente
                    assert response.body() != null;

                    List<RestrictionsItems> list = response.body().get(0).getItems();
                    list.addAll(response.body().get(1).getItems());

                    mData.postValue(list);
                } else {
                    mData.postValue(null);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<RestrictionFeed>> call, @NonNull Throwable t)
            {
                mData.postValue(null);
            }
        });
    }
}