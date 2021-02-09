package com.example.appcovid.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Attribute;

@Element
public class RssImage {

    @Attribute
    private String url;

    @Attribute
    private String length;

    @Attribute
    private String type;


    public String getUrl() {
        return url;
    }
}
