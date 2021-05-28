package com.example.appcovid.views;

import android.app.NotificationChannel;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.appcovid.R;
import com.example.appcovid.model.BaseActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent i = getIntent();
        String mac = getMac().toUpperCase();

        //comprobar que la aplicacion se arranca desde muerta
        if (i.getStringExtra("ALERTACOVID") == null) {
            Log.d("HOLA", "onCreate: Estaba muerta chacho");

            getmRef().child(mac).child("CovidAlert").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    // TODO: Sacar notificacion de alerta covid

                    if (snapshot.getValue(Boolean.class) != null && snapshot.getValue(Boolean.class)) {
                        launchNotification();
                        getmRef().child(mac).child("CovidAlert").setValue(false);
                    } else {
                        getmRef().child(mac).child("CovidAlert").setValue(false);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } else {
            getmRef().child(mac).child("CovidAlert").setValue(false);
        }

    }

    public void onClickLanzarActivity(View v) {
        int id = v.getId();

        switch(id) {
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

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // TODO: Comprobar si al cerrar la app del todo se siguen registrando usuarios conectados
        //unregisterReceiver(mBluetoothReceiver);
    }

    public void launchNotification() {
        Intent intent = new Intent(MainActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("ALERTACOVID", "ALERTACOVID");
        PendingIntent pendingIntent = PendingIntent.getActivity(MainActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this, NotificationChannel.DEFAULT_CHANNEL_ID);
        builder.setSmallIcon(R.drawable.common_google_signin_btn_icon_dark);
        builder.setContentTitle(getString(R.string.text_notification_title));
        builder.setStyle(new NotificationCompat.BigTextStyle()
                .bigText(getString(R.string.text_notification_body)));
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(true);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(MainActivity.this);
        notificationManager.notify(1, builder.build());
    }
}