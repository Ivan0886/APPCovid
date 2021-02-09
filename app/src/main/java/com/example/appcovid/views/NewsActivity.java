package com.example.appcovid.views;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.net.http.HttpResponseCache;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.appcovid.R;
import com.example.appcovid.controller.RssService;
import com.example.appcovid.model.RssFeed;

import com.squareup.picasso.Picasso;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

public class NewsActivity extends AppCompatActivity {

    private static final String URL_RSS = "https://www.rtve.es/";
    private static Retrofit.Builder builder = new Retrofit.Builder().baseUrl(URL_RSS)
            .addConverterFactory(SimpleXmlConverterFactory.create());
    private static HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor()
            .setLevel(HttpLoggingInterceptor.Level.BODY);
    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        leerRSS();
    }


    private void leerRSS() {
        httpClient.addInterceptor(loggingInterceptor);
        builder.client(httpClient.build());

        Retrofit retrofit = builder.build();
        RssService rssService = retrofit.create(RssService.class);
        Call<RssFeed> callAsync = rssService.getFeed();

        callAsync.enqueue(new Callback<RssFeed>() {
            @Override
            public void onResponse(Call<RssFeed> call, Response<RssFeed> response) {
                if (response.isSuccessful()) {
                    // La API responde correctamente
                    RssFeed apiResponse = response.body();
                    pintarNoticias(apiResponse);
                } else {
                    // Si la llamada a la API es correcta pero la respuesta no
                    lanzarError(response.code() + "Request Error :: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<RssFeed> call, Throwable t) {
                if (call.isCanceled()) {
                    // Si la llamada a la API se cancela
                    lanzarError("La llamada a la API fue cancelada a la fuerza");
                } else {
                    // Si la llamada a la API falla
                    lanzarError("Network Error :: " + t.getLocalizedMessage());
                }
            }
        });
    }


    public void pintarNoticias(RssFeed apiResponse) {
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