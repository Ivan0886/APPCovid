package com.example.appcovid.model;

public class Item {

    private String title;
    private String link;
    private String image;


    public Item(String title, String link, String image) {
        this.title = title;
        this.image = image;
        this.link = link;
    }


    public String getTitle() {
        return title;
    }


    public String getImage() {
        return image;
    }


    public String getLink() {
        return link;
    }
}
