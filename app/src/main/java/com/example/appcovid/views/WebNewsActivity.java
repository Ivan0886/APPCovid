package com.example.appcovid.views;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.annotation.RequiresApi;

import com.example.appcovid.R;
import com.example.appcovid.model.BaseActivity;

/**
 * Clase que contiene la web de la noticia
 * @author Iván Moriche Damas
 * @author Rodrigo Garcia
 * @author Iustin Mocanu
 * @version 28/05/2021/A
 * @see BaseActivity
 * @see WebView
 */
public class WebNewsActivity extends BaseActivity
{
    private WebView mWebNews;

    /**
     * Método que se ejecuta al arrancar la actividad.
     * @param savedInstanceState instancia de la actividad
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_news);
    }


    /**
     * Método que se ejecuta al iniciar la actividad. Se comprueba si
     * el internet está activado y se configura el WebView.
     */
    @SuppressLint("SetJavaScriptEnabled") @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1) @Override
    protected void onStart()
    {
        super.onStart();
        if (haveNetworkConnection())
        {
            Intent intent = getIntent();

            mWebNews = findViewById(R.id.webNews);

            WebSettings webNewsSettings = mWebNews.getSettings();
            webNewsSettings.setJavaScriptEnabled(true);
            webNewsSettings.setMediaPlaybackRequiresUserGesture(false);

            mWebNews.loadUrl(String.valueOf(intent.getData()));

            mWebNews.setWebChromeClient(new WebChromeClient()
            {
                @TargetApi(Build.VERSION_CODES.LOLLIPOP) @Override
                public void onPermissionRequest(final PermissionRequest request)
                {
                    request.grant(request.getResources());
                }
            });
        }else {
            launchAlert(R.string.error_title, R.string.error_text_service, WebNewsActivity.this);
        }
    }


    /**
     * Método que determina el comportamiento del botón "<-"
     */
    @Override
    public void onBackPressed()
    {
        if (mWebNews.canGoBack())
        {
            mWebNews.goBack();
        } else {
            super.onBackPressed();
        }
    }
}