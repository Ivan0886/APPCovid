package com.example.appcovid.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Clase de los items RSS
 * @author Iván Moriche Damas
 * @author Rodrigo Garcia
 * @author Iustin Mocanu
 * @version 28/05/2021/A
 * @see RssChannel
 * @see RssImage
 */
@Root(name = "item", strict = false) public class RssItem
{
    @Element(name="title") private String mTitle;
    @Element(name="enclosure") private RssImage mImage;
    @Element(name="link") private String mLink;

    /**
     * Método que devuelve el título del RSS
     * @return mTitle
     */
    public String getmTitle()
    {
        return mTitle;
    }


    /**
     * Método que devuelve la imagen del RSS
     * @return mImage
     */
    public RssImage getmImage()
    {
        return mImage;
    }


    /**
     * Método que devuelve URL del RSS
     * @return mLink
     */
    public String getmLink()
    {
        return mLink;
    }
}