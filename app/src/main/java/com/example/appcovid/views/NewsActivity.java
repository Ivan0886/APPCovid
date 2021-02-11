package com.example.appcovid.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.example.appcovid.R;
import com.example.appcovid.controller.OnRssResponse;
import com.example.appcovid.controller.RssAdapter;
import com.example.appcovid.controller.RssController;
import com.example.appcovid.model.RssFeed;

public class NewsActivity extends AppCompatActivity {

    private RssAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        RssController controller = new RssController();

        // Se construye el RecyclerView
        RecyclerView recyclerView = findViewById(R.id.list_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new RssAdapter(this);

        // Se añaden los datos al adaptador mediante el RssController
        controller.start(new OnRssResponse() {
            @Override
            public void getRss(RssFeed rss) {
                if(rss != null) {
                    adapter.addData(rss.getChannel().getItems());
                } else {
                    lanzarError("Ha surgido un problema llamando a la API");
                }
            }
        });

        // A cada item se le da su propio link de la noticia
        adapter.setClickListener(new RssAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent i = new Intent();
                i.setAction(Intent.ACTION_VIEW);
                i.setData(Uri.parse(adapter.getItem(position).getLink()));
                startActivity(i);
            }
        });

        recyclerView.setAdapter(adapter);
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