package com.example.appcovid.views;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appcovid.R;
import com.example.appcovid.controller.NewsViewModel;
import com.example.appcovid.controller.RssAdapter;
import com.example.appcovid.model.BaseActivity;

import java.util.ArrayList;

/**
 * Clase que contiene NewsViewModel
 * @author Iván Moriche Damas
 * @author Rodrigo Garcia
 * @author Iustin Mocanu
 * @version 28/05/2021/A
 * @see BaseActivity
 * @see NewsViewModel
 */
public class NewsActivity extends BaseActivity
{
    private RssAdapter mAdapter;

    /**
     * Método que se ejecuta al arrancar la actividad. Se construye el RecylerView
     * @param savedInstanceState instancia de la actividad
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        // Se construye el RecyclerView
        RecyclerView recyclerView = findViewById(R.id.list_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Se construye el adaptador y se añade al RecyclerView
        mAdapter = new RssAdapter(this);
        recyclerView.setAdapter(mAdapter);

        // Se construye el ViewModel
        NewsViewModel dataNews = new ViewModelProvider(this).get(NewsViewModel.class);

        // Se comprueba si los datos han cambiado
        dataNews.getmData().observe(this, rssItems -> {
            // Si la llamada ha ido bien
            if(rssItems != null)
            {
                mAdapter.addData(new ArrayList<>(rssItems));

                // A cada item se le da su propio link de la noticia
                mAdapter.setClickListener((view, position) -> {
                    Intent i = new Intent(NewsActivity.this, WebNewsActivity.class);
                    i.setData(Uri.parse(mAdapter.getItem(position).getmLink()));
                    startActivity(i);
                });
            } else {
                launchAlert(R.string.text_ok, R.string.error_text_service);
            }
        });
    }
}