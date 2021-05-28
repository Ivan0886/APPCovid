package com.example.appcovid.controller;

import com.example.appcovid.model.RestrictionFeed;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Interfaz servicio de restricciones
 * @author Iván Moriche Damas
 * @author Rodrigo Garcia
 * @author Iustin Mocanu
 * @version 28/05/2021/A
 * @see RestrictionFeed
 */
public interface RestrictionsService
{
    @GET("restriction?")
    Call<List<RestrictionFeed>> getRestrictions(@Query("zipcode") String postalCode, @Query("token") String key);
}