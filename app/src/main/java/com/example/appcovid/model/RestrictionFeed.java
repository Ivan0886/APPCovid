package com.example.appcovid.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Clase raiz de restriciones
 * @author Iván Moriche Damas
 * @author Rodrigo Garcia
 * @author Iustin Mocanu
 * @version 28/05/2021/A
 * @see RestrictionsItems
 */
public class RestrictionFeed
{
    @SerializedName("restrictions") private List<RestrictionsItems> items;

    /**
     * Método que devuelve una lista de restriciones
     * @return items
     */
    public List<RestrictionsItems> getItems()
    {
        return items;
    }
}