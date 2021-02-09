package com.example.appcovid.model;

import java.util.List;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Root(name = "channel", strict = false)
public class RssChannel {

    @Element
    private String title;

    @ElementList(inline = true, required = false)
    private List<RssItem> items;


    public List<RssItem> getItems() {
        return items;
    }
}