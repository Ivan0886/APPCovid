package com.example.appcovid.views;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import com.example.appcovid.R;
import com.example.appcovid.controller.BluetoothReceiver;
import com.example.appcovid.model.DeviceList;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.List;
import java.util.prefs.PreferenceChangeEvent;

public class MainActivity extends AppCompatActivity {
    private DeviceList mDeviceList;

    private boolean mConnection;
    private MessageListener mMessageListener;
    private Message mMessage;
    private static final String ANDROID_ID_KEY = "ANDROID_KEY";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mConnection = false;


        //TODO: Comprobar si el sistema de Nearby funciona entre actividades y si la aplicación está minimizada

        // Se comprueba si la ID del dispositivo ya se ha guardado
        if (!PreferenceManager.getDefaultSharedPreferences(this).contains(ANDROID_ID_KEY)) {
            // TODO: Lanzar alert para advertir al usuario que se guardara su ID de Android
            //  lanzarAlert(mTituloID, mTextoID);
            String androidID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
            PreferenceManager.getDefaultSharedPreferences(this).edit().putString(ANDROID_ID_KEY, androidID).apply();
        }


        mMessageListener = new MessageListener() {
            @Override
            public void onFound(Message message) {
                super.onFound(message);
                Log.d("onFound", "Encontrado mensaje: " + new String(message.getContent()));
                mConnection = true;
                // Contador = 900 : 15 minutos
                new Thread() {
                    int contador = 1;
                    @Override
                    public void run() {
                        super.run();
                        while (contador <= 10 && mConnection ) {
                            try {
                                this.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            contador++;
                        }

                        if (contador >= 10) {
                            Log.d("Registrado", "ID registrada: " + new String(message.getContent())); // Registrar mensaje (ID) en BBDD
                        }
                    }
                }.run();
            }

            @Override
            public void onLost(Message message) {
                super.onLost(message);
                Log.d("onLost", "Perdido mensaje: " + new String(message.getContent()));
                if (mConnection) {
                    mConnection = !mConnection;
                }

            }
        };

        mMessage = new Message(PreferenceManager.getDefaultSharedPreferences(this).getString(ANDROID_ID_KEY, "Dispositivo CovidRecord").getBytes());

    }

    @Override
    protected void onStart() {
        super.onStart();
        Nearby.getMessagesClient(this).publish(mMessage);
        Nearby.getMessagesClient(this).subscribe(mMessageListener);
    }
    @Override
    public void onStop() {

        //TODO: Considerar si la aplicación no debería dejar de publicar mensajes si está parada
        Nearby.getMessagesClient(this).unpublish(mMessage);
        Nearby.getMessagesClient(this).unsubscribe(mMessageListener);
        super.onStop();
    }

  /*  private void lanzarAlert(int titulo, int texto) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(titulo));
        builder.setMessage(getString(texto));
        builder.setNegativeButton(R.string.text_no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });


        builder.setPositiveButton(R.string.text_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(texto == ) {
                    dialog.dismiss();
                    finish();
                } else {
                    Intent enableBT = new Intent(mBluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBT, REQUEST_BLUETOOTH);
                    dialog.dismiss();
                }
            }
        });
        builder.create().show();
    }
*/

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
}