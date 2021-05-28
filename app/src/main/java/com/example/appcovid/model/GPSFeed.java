package com.example.appcovid.model;

import com.google.gson.annotations.SerializedName;

/**
 * Clase raiz de GPS
 * @author Iván Moriche Damas
 * @author Rodrigo Garcia
 * @author Iustin Mocanu
 * @version 28/05/2021/A
 */
public class GPSFeed
{
    @SerializedName(value="city") private String mCity;

    /**
     * Método que devuelve la ciudad
     * @return mCity
     */
    public String getmCity()
    {
        return mCity;
    }
}