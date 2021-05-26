package com.example.appcovid.views;

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

public class WebNewsActivity extends BaseActivity {
    private WebView mWebNews;
    private WebSettings mWebNewsSettings;


    /**
     * method onCreate
     * @param savedInstanceState
     * Method that creates the WebView
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_news);

        Intent intent = getIntent();

        mWebNews = (WebView) findViewById(R.id.webNews);
        mWebNewsSettings = mWebNews.getSettings();
        mWebNewsSettings.setJavaScriptEnabled(true);
        mWebNewsSettings.setMediaPlaybackRequiresUserGesture(false);
        mWebNews.loadUrl(String.valueOf(intent.getData()));

        mWebNews.setWebChromeClient(new WebChromeClient(){
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onPermissionRequest(final PermissionRequest request) {
                request.grant(request.getResources());
            }
        });
    }


    /**
     * method onBackPressed
     * Method determines the behavior of the back button
     */
    @Override
    public void onBackPressed() {
        if (mWebNews.canGoBack()) {
            mWebNews.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
