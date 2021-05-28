package com.example.appcovid.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Attribute;

/**
 * Clase Imagen de RSS
 * @author Iván Moriche Damas
 * @author Rodrigo Garcia
 * @author Iustin Mocanu
 * @version 28/05/2021/A
 * @see RssItem
 */
@Element public class RssImage
{
    @Attribute(name="url") private String mUrl;
    @Attribute(name="length") private String mLength;
    @Attribute(name="type") private String mType;

    /**
     * Método que devuelve el src de la imagen
     * @return mUrl
     */
    public String getmUrl()
    {
        return mUrl;
    }
}