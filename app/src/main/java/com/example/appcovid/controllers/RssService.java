package com.example.appcovid.controllers;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.Nullable;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import java.io.IOException;

public class RssService extends IntentService {
    public static final String RSS_URL = "https://www.rtve.es/api/tematicas/129647/noticias.rss";
    private static final String TAG = "RssService";
    public RssService() {
        super("RssService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        // codigo para actualizar la pagina de noticias
        try {
          Document doc = Jsoup.connect(RSS_URL).parser(Parser.xmlParser()).get();
          Elements elements = doc.getAllElements();
            for (Element element : elements) {
                Log.i(TAG, "onHandleIntent: " + element.ownText());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
