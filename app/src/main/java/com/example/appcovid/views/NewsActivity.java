package com.example.appcovid.views;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.appcovid.R;
import com.example.appcovid.controller.OnRssResponse;
import com.example.appcovid.controller.RssController;
import com.example.appcovid.model.RssFeed;

import com.squareup.picasso.Picasso;

public class NewsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        RssController controller = new RssController();

        controller.start(new OnRssResponse() {
            @Override
            public void getRss(RssFeed rss) {
                pintarNoticias(rss);
            }
        });
    }


    public void pintarNoticias(RssFeed apiResponse) {
        if (apiResponse != null) {
            for(int i = 0; i < 3; i++) { // TODO Aumentar numero noticias
                TextView text = findViewById(getResources().getIdentifier("new" + (i + 1) + "_title", "id", getPackageName()));
                Button button = findViewById(getResources().getIdentifier("button_new" + (i + 1), "id", getPackageName()));
                ImageView image = findViewById(getResources().getIdentifier("image_new" + (i + 1), "id", getPackageName()));

                text.setText(apiResponse.getChannel().getItems().get(i).getTitle());
                // A cada boton se le pasa el link correspondiente para abrirlo con WebView
                abrirNoticia(button, apiResponse.getChannel().getItems().get(i).getLink());
                // Se pinta la imagen correspondiente
                Picasso.get().load(apiResponse.getChannel().getItems().get(i).getImage().getUrl()).fit().centerCrop().error(R.drawable.img_prueba).into(image);
            }
        } else {
            lanzarError("Ha surgido un problema llamando a la API");
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


    private void lanzarError(String mensajeError) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.error_title));
        builder.setMessage(mensajeError);
        builder.setPositiveButton(R.string.text_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });
        builder.create().show();
    }


    public void volverMainActivity(View v) {
        finish();
    }
}