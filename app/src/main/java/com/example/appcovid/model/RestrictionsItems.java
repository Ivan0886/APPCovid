package com.example.appcovid.model;

import com.google.gson.annotations.SerializedName;

/**
 * Clase restrición
 * @author Iván Moriche Damas
 * @author Rodrigo Garcia
 * @author Iustin Mocanu
 * @version 28/05/2021/A
 */
public class RestrictionsItems
{
    @SerializedName(value="title") private String mTitle;
    @SerializedName(value="description") private String mDescription;

    /**
     * Método que devuelve el título de la restrición
     * @return mTitle
     */
    public String getmTitle()
    {
        return mTitle;
    }


    /**
     * Método que devuelve el texto de la restrición
     * @return mDescription
     */
    public String getmDescription()
    {
        return mDescription;
    }
}