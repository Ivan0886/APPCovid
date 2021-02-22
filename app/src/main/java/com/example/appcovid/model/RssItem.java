package com.example.appcovid.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "item", strict = false)
public class RssItem {

    @Element(name="title")
    private String mTitle;

    @Element(name="enclosure")
    private RssImage mImage;

    @Element(name="link")
    private String mLink;


    public String getmTitle() {
        return mTitle;
    }


    public RssImage getmImage() {
        return mImage;
    }


    public String getmLink() {
        return mLink;
    }
}