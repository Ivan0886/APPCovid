package com.example.appcovid.model;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.appcovid.views.MainActivity;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public abstract class BaseActivity extends AppCompatActivity {
    protected static final String TAG = BaseActivity.class.getName();
    public static boolean isAppWentToBg = true;
    public static boolean isWindowFocused = false;
    public static boolean isMenuOpened = false;
    public static boolean isBackPressed = false;
    private boolean mConnection;
    private MessageListener mMessageListener;
    private Message mMessage;
    private static final String ANDROID_ID_KEY = "ANDROID_KEY";


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mConnection = false;


        //TODO: Comprobar si el sistema de Nearby funciona entre actividades y si la aplicación está minimizada

        // Se comprueba si la ID del dispositivo ya se ha guardado
        if (!PreferenceManager.getDefaultSharedPreferences(this).contains(ANDROID_ID_KEY)) {
            // TODO: Lanzar alert para advertir al usuario que se guardara su ID de Android
            //  lanzarAlert(mTituloID, mTextoID);
            String androidID = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
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
        Log.d(TAG, "onStart isAppWentToBg " + isAppWentToBg);
        applicationWillEnterForeground();
        Nearby.getMessagesClient(this).publish(mMessage);
        Nearby.getMessagesClient(this).subscribe(mMessageListener);
        super.onStart();
    }


    private void applicationWillEnterForeground() {
        if (isAppWentToBg) {
            isAppWentToBg = false;
            Toast.makeText(getApplicationContext(), "App is in foreground", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop ");
        //TODO: Considerar si la aplicación no debería dejar de publicar mensajes si está parada
        Nearby.getMessagesClient(this).unpublish(mMessage);
        Nearby.getMessagesClient(this).unsubscribe(mMessageListener);
        applicationdidenterbackground();
    }


    public void applicationdidenterbackground() {
        if (!isWindowFocused) {
            isAppWentToBg = true;
            //Toast.makeText(getApplicationContext(), "App is Going to Background", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onBackPressed() {
        if (this instanceof MainActivity) {
        } else {
            isBackPressed = true;
        }

        Log.d(TAG, "onBackPressed " + isBackPressed + "" + this.getLocalClassName());
        super.onBackPressed();
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        isWindowFocused = hasFocus;

        if (isBackPressed && !hasFocus) {
            isBackPressed = false;
            isWindowFocused = true;
        }

        super.onWindowFocusChanged(hasFocus);
    }


    /*public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.action_settings:
                Intent i = new Intent(this, SettingActivity.class);
                startActivity(i);
                break;
        }
        return true;
    }*/


    /*@Override
    public boolean onMenuItemSelected(int featureId, android.view.MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.action_settings:
                Intent i = new Intent(this, SettingActivity.class);
                startActivity(i);
                break;
        }
        return true;
    }*/

}