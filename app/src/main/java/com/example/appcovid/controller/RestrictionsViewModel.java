package com.example.appcovid.controller;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.appcovid.model.GPSFeed;
import com.example.appcovid.model.GPSLocation;
import com.example.appcovid.model.RestrictionFeed;
import com.example.appcovid.model.RestrictionsItems;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestrictionsViewModel extends ViewModel {
    private MutableLiveData<List<RestrictionsItems>> mData;
    private static final String URL_GPS = "https://geocode.xyz/";
    private static final String URL_RES = "https://api.quecovid.es/restriction/";
    private GPSLocation mGpsLocation;


    public LiveData<List<RestrictionsItems>> getmData(GPSLocation mGpsLocation) {
        if(mData == null) {
            mData = new MutableLiveData<List<RestrictionsItems>>();
            mData.setValue(new ArrayList<RestrictionsItems>());
            loadData(mGpsLocation);
        }
        return mData;
    }

    private void loadData(GPSLocation mGpsLocation) {
        if (mGpsLocation != null) {

            // Se construye el retrofit
            Retrofit retrofit = new Retrofit
                    .Builder()
                    .baseUrl(URL_GPS)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            GPSService gpsService = retrofit.create(GPSService.class);

            // Se construye la llamada
            Call<GPSFeed> callAsync = gpsService.getAddress(mGpsLocation.getmLatitude(), mGpsLocation.getmLongitude());

            // Se hace la llamada a la API
            callAsync.enqueue(new Callback<GPSFeed>() {
                @Override
                public void onResponse(Call<GPSFeed> call, Response<GPSFeed> response) {
                    if (response.isSuccessful()) {

                        // La API responde correctamente
                        Retrofit retrofit = new Retrofit
                                .Builder()
                                .baseUrl(URL_RES)
                                .addConverterFactory(GsonConverterFactory.create())
                                .build();
                        RestrictionsService resService = retrofit.create(RestrictionsService.class);

                        // TODO Devuelve la city vacio...
                        //String city = response.body().getmCity();

                        // TODO Añadir el resto de ciudades de España
                        /*if(city.equals("Rivas Vaciamadrid") || city.equalsIgnoreCase("Piñuecar Gandullas")) {
                            city = response.body().getmCity().replace(" ", "-");
                        } else {
                            city = response.body().getmCity().replace(" ", "+");
                        }*/

                        // TODO Consultar otra API que no limite a 500 la llamada o pagar por ella
                        // Se construye la llamada (OJO: En Android Studio se falsean las coordenas)
                        //Call<List<RestrictionFeed>> callAsync = resService.getRestrictions(city, "lR2I41RV8NhDuEkS51V8Z9NLJ");
                        Call<List<RestrictionFeed>> callAsync = resService.getRestrictions("madrid", "lR2I41RV8NhDuEkS51V8Z9NLJ");

                        // Se hace la llamada a la API
                        callAsync.enqueue(new Callback<List<RestrictionFeed>>() {
                            @Override
                            public void onResponse(Call<List<RestrictionFeed>> call, Response<List<RestrictionFeed>> response) {
                                if (response.isSuccessful()) {
                                    mData.postValue(response.body().get(0).getItems());
                                } else {
                                    mData.postValue(null);
                                }
                            }
                            @Override
                            public void onFailure(Call<List<RestrictionFeed>> call, Throwable t) {
                                mData.postValue(null);
                            }
                        });
                    } else {
                        mData.postValue(null);
                        //Log.d("ESTADO","Ha fallado :)");
                    }
                }

                @Override
                public void onFailure(Call<GPSFeed> call, Throwable t) {
                    mData.postValue(null);
                    //Log.d("ESTADO","Ha fallado :)");
                }
            });
        }
    }
}