package com.example.appcovid.controller;

import com.example.appcovid.model.RssFeed;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Interfaz servicio RSS
 * @author Iván Moriche Damas
 * @author Rodrigo Garcia
 * @author Iustin Mocanu
 * @version 28/05/2021/A
 * @see RssFeed
 */
public interface RssService
{
    @GET("api/tematicas/129647/noticias.rss")
    Call<RssFeed> getFeed();
}