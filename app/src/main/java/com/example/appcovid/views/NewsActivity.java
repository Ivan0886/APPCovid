package com.example.appcovid.views;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.appcovid.R;
import com.example.appcovid.model.Item;
import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NewsActivity extends AppCompatActivity {

    private final String URL_RSS = "https://www.rtve.es/api/tematicas/129647/noticias.rss";
    private List<Item> items;
    private Handler mHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        mHandler = new Handler();
        items = new ArrayList<>();

        leerRSS();
    }


    public void leerRSS() {
        // Se abre un hilo para guardar una lista de objetos noticias (clase Item)
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Document doc = Jsoup.connect(URL_RSS).get();
                    for(Element e : doc.select("item")) {
                        items.add(new Item(e.select("title").text(), e.select("link").text(), e.select("enclosure").attr("url")));
                    }
                } catch(IOException e) {
                    e.printStackTrace();
                }

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        pintarNoticias();
                    }
                });
            }
        }).start();
    }


    public void pintarNoticias() {
        for(int i = 0; i < 3; i++) { // TODO Aumentar numero noticias
            TextView text = findViewById(getResources().getIdentifier("new" + (i + 1) + "_title", "id", getPackageName()));
            Button button = findViewById(getResources().getIdentifier("button_new" + (i + 1), "id", getPackageName()));
            ImageView image = findViewById(getResources().getIdentifier("image_new" + (i + 1), "id", getPackageName()));

            text.setText(items.get(i).getTitle());
            // A cada boton se le pasa el link correspondiente para abrirlo con WebView
            abrirNoticia(button, items.get(i).getLink());
            // Se pinta la imagen correspondiente
            Picasso.get().load(items.get(i).getImage()).fit().centerCrop().error(R.drawable.img_prueba).into(image);
        }
    }


    public void abrirNoticia(Button b, final String s) {
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


    public void volverMainActivity(View v) {
        finish();
    }
}