package com.example.appcovid.controller;

import com.example.appcovid.model.GPSFeed;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Interfaz servicio GPS
 * @author Iván Moriche Damas
 * @author Rodrigo Garcia
 * @author Iustin Mocanu
 * @version 28/05/2021/A
 * @see GPSFeed
 */
public interface GPSService
{
    @GET("{lat},{lon}?geoit=json&auth=113925367106775e15757233x1626")
    Call<GPSFeed> getAddress(@Path("lat") double lat, @Path("lon") double lon);
}