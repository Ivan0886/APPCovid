package com.example.appcovid.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "rss", strict = false)
public class RssFeed {

    @Element(name="channel")
    private RssChannel mChannel;


    public RssChannel getmChannel() {
        return mChannel;
    }
}
