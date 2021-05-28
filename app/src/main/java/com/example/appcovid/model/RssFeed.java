package com.example.appcovid.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Clase raiz de RSS
 * @author Iván Moriche Damas
 * @author Rodrigo Garcia
 * @author Iustin Mocanu
 * @version 28/05/2021/A
 * @see RssFeed
 * @see RssItem
 */
@Root(name = "rss", strict = false) public class RssFeed
{
    @Element(name="channel") private RssChannel mChannel;

    /**
     * Método que devuelve channel del RSS
     * @return mChannel
     */
    public RssChannel getmChannel()
    {
        return mChannel;
    }
}