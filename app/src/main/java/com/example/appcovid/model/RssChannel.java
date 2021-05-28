package com.example.appcovid.model;

import java.util.List;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

/**
 * Clase channel de RSS
 * @author Iván Moriche Damas
 * @author Rodrigo Garcia
 * @author Iustin Mocanu
 * @version 28/05/2021/A
 * @see RssFeed
 * @see RssItem
 */
@Root(name = "channel", strict = false) public class RssChannel
{
    @ElementList(inline = true, required = false) private List<RssItem> items;

    /**
     * Método que devuelve una lista de RSS
     * @return items
     */
    public List<RssItem> getItems()
    {
        return items;
    }
}