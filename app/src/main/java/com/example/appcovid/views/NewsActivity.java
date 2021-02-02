package com.example.appcovid.views;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import com.example.appcovid.R;
import com.example.appcovid.controllers.RssAlarmReciever;

public class NewsActivity extends AppCompatActivity {

        Button bNoticia1, bNoticia2, bNoticia3;
        String noticia1, noticia2, noticia3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        //Método que arranca la alarma que actualizará la página de Noticias
        colocarAlarma();

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

    public void colocarAlarma() {
        //Intent que representa el handler que ejecutará el código para actualizar las noticias
        Intent i = new Intent(this.getApplicationContext(), RssAlarmReciever.class);
        // Intent para pedirle a android que nos lo ejecute en otro segundo
        final PendingIntent intentPendiente = PendingIntent.getBroadcast(this, RssAlarmReciever.CODIGO_PEDIDO, i, PendingIntent.FLAG_UPDATE_CURRENT);
        // Se empieza a contar desde que se ejecuta la actividad de Noticias
        long milis = System.currentTimeMillis();
        //Creación de la alarma
        AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        // Colocar la alarma en el sistema para que se repita cada día y se ejecute el intent
        alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, milis, AlarmManager.INTERVAL_DAY, intentPendiente);
        Log.i("RssService", "Servicio en marcha");
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