package com.example.appcovid.views;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.appcovid.R;
import com.example.appcovid.model.BaseActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.security.NoSuchAlgorithmException;

/**
 * Clase que contiene la pantalla inicial de la App
 * @author Iván Moriche Damas
 * @author Rodrigo Garcia
 * @author Iustin Mocanu
 * @version 28/05/2021/A
 * @see BaseActivity
 */
public class MainActivity extends BaseActivity
{
    /**
     * Método que se ejecuta al arrancar la App. Se comprueba si existe el valor "ALERTACOVID".
     * Si no existe, se introduce. Si existe, se comprueba si ha habido cambios para lanzar una notificación
     * si es necesario
     * @param savedInstanceState instancia de la actividad
     * @see DataSnapshot
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent i = getIntent();
        String mac = getMac();

        if(!mac.equals("")) {
            // Se comprueba que la App se arranca si esta muerta
            if (i.getStringExtra("ALERTACOVID") == null)
            {
                getmRef().child(mac).child("CovidAlert").addValueEventListener(new ValueEventListener()
                {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot)
                    {
                        if (snapshot.getValue(Boolean.class) != null && snapshot.getValue(Boolean.class))
                        {
                            launchNotification();
                        }

                        getmRef().child(mac).child("CovidAlert").setValue(false);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });
            } else {
                getmRef().child(mac).child("CovidAlert").setValue(false);
            }
        }
    }


    /**
     * Método que lanza una actividad distinta dependiendo del boton pulsado
     * @param v botón
     */
    @SuppressLint("NonConstantResourceId")
    public void onClickLanzarActivity(View v)
    {
        int id = v.getId();

        switch(id)
        {
            case R.id.button_news:
                startActivity(new Intent(this, NewsActivity.class));
                break;
            case R.id.button_restriction:
                startActivity(new Intent(this, RestrictionsActivity.class));
                break;
            default:
                startActivity(new Intent(this, StateActivity.class));
        }
    }


    /**
     * Método que lanza la notificación de la App
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void launchNotification()
    {
        Intent intent = new Intent(MainActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("ALERTACOVID", "ALERTACOVID");

        PendingIntent pendingIntent = PendingIntent.getActivity(
                MainActivity.this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                MainActivity.this,
                NotificationChannel.DEFAULT_CHANNEL_ID);

        builder.setSmallIcon(R.drawable.common_google_signin_btn_icon_dark);
        builder.setContentTitle(getString(R.string.text_notification_title));
        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(getString(R.string.text_notification_body)));
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(MainActivity.this);
        notificationManager.notify(1, builder.build());
    }
}