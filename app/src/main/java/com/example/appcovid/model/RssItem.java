package com.example.appcovid.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "item", strict = false)
public class RssItem {

    @Element
    private String title;

    @Element(name="enclosure")
    private RssImage image;

    @Element
    private String link;


    public String getTitle() {
        return title;
    }


    public RssImage getImage() {
        return image;
    }


    public String getLink() {
        return link;
    }
}