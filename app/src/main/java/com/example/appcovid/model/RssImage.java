package com.example.appcovid.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Attribute;

@Element
public class RssImage {

    @Attribute(name="url")
    private String mUrl;

    @Attribute(name="length")
    private String mLength;

    @Attribute(name="type")
    private String mType;


    public String getmUrl() {
        return mUrl;
    }
}
