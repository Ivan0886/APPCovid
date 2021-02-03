package com.example.appcovid.views;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.example.appcovid.R;

public class NewsActivity extends AppCompatActivity {

        Button bNoticia1, bNoticia2, bNoticia3;
        String noticia1, noticia2, noticia3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        bNoticia1 = (Button) findViewById(R.id.button_new1);
        bNoticia2 = (Button) findViewById(R.id.button_new2);
        bNoticia3 = (Button) findViewById(R.id.button_new3);

        //TODO Coger cada noticia de su link correspondiente
        noticia1 = "http://developer.android.com";
        noticia2 = "http://developer.android.com";
        noticia3 = "http://developer.android.com";

        abrirNoticia(bNoticia1,noticia1);
        abrirNoticia(bNoticia2,noticia2);
        abrirNoticia(bNoticia3,noticia3);
    }

    public void abrirNoticia(Button b, final String s){
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                i.setAction(Intent.ACTION_VIEW);
                i.setData(Uri.parse(s));
                startActivity(i);
            }
        });
    }

    public void volverMainActivity(View v){
        finish();
    }
}