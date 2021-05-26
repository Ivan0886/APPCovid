package com.example.appcovid.views;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.example.appcovid.R;
import com.example.appcovid.controller.NewsViewModel;
import com.example.appcovid.controller.RssAdapter;
import com.example.appcovid.model.BaseActivity;
import com.example.appcovid.model.RssItem;

import java.util.ArrayList;
import java.util.List;

public class NewsActivity extends BaseActivity {

    private RssAdapter mAdapter;
    private NewsViewModel mDataNews;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        // Se construye el RecyclerView
        RecyclerView recyclerView = findViewById(R.id.list_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Se construye el adaptador y se añade al RecyclerView
        mAdapter = new RssAdapter(this);
        recyclerView.setAdapter(mAdapter);

        // Se construye el ViewModel
        mDataNews = new ViewModelProvider(this).get(NewsViewModel.class);

        // Se comprueba si los datos han cambiado
        mDataNews.getmData().observe(this, new Observer<List<RssItem>>() {
            @Override
            public void onChanged(List<RssItem> rssItems) {
                // Si la llamada ha ido bien
                if(rssItems != null) {
                    mAdapter.addData(new ArrayList(rssItems));

                    // A cada item se le da su propio link de la noticia
                    mAdapter.setClickListener(new RssAdapter.ItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            Intent i = new Intent(NewsActivity.this, WebNewsActivity.class);
                            i.setData(Uri.parse(mAdapter.getItem(position).getmLink()));
                            startActivity(i);

                            /*Intent i = new Intent();
                            i.setAction(Intent.ACTION_VIEW);
                            i.setData(Uri.parse(mAdapter.getItem(position).getmLink()));
                            startActivity(i);*/
                        }
                    });
                } else {
                    launchError("Ha surgido un problema llamando a la API");
                }
            }
        });
    }


    private void launchError(String messageError) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.error_title));
        builder.setMessage(messageError);
        builder.setPositiveButton(R.string.text_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });
        builder.create().show();
    }


    public void backMainActivity(View v) {
        finish();
    }
}