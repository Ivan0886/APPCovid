package com.example.appcovid.views;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.example.appcovid.R;
import com.example.appcovid.model.BaseActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;

/**
 * Clase que contiene el estado del usuario referente al COVID-19
 * @author Iván Moriche Damas
 * @author Rodrigo Garcia
 * @author Iustin Mocanu
 * @version 28/05/2021/A
 * @see BaseActivity
 */
public class StateActivity extends BaseActivity
{
    private Button mButton;
    private SharedPreferences mPreferences;

    /**
     * Método que se ejecuta al arrancar la actividad. Se consulta el estado del botón y se desactiva/habilita si
     * han pasado 14 dias
     * @param savedInstanceState instancia de la actividad
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_state);

        mPreferences = PreferenceManager.getDefaultSharedPreferences(StateActivity.this);
        mButton = findViewById(R.id.button_covid);
        // Se comprueba si han pasado 14 dias
        mButton.setEnabled(checkTimeConfirmationCovid());
    }


    /**
     * Método que devuelve si aún se tiene COVID-19
     * @return value
     */
    private boolean checkTimeConfirmationCovid()
    {
        String date = mPreferences.getString("fechaCovid", "unknown");
        boolean value = true;

        if (!date.equalsIgnoreCase("unknown")) {
            value = LocalDate.now().isAfter(LocalDate.parse(date).plusDays(14));
        }

        return value;
    }


    /**
     * Método que lanza un alert para confirmar el positivo de COVID-19 e
     * inhabilita el botón en el caso de que el usuario pulse SI. También se
     * encarga de mandar los TOKENS de los dispositivos asociados al servidor
     * para que este mande las notificaciones
     * @param v vista
     */
    public void alertConfirmCovid(View v)
    {
        // Creación Title Alert
        TextView titleView = new TextView(getApplicationContext());
        titleView.setText(R.string.dialog_title_state);
        titleView.setPadding(20, 30, 20, 30);
        titleView.setTextSize(20F);
        titleView.setBackgroundColor(Color.RED); // Rojo
        titleView.setTextColor(Color.WHITE);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCustomTitle(titleView);
        builder.setMessage(R.string.dialog_text_state);
        builder.setCancelable(false);

        builder.setPositiveButton(R.string.text_si, (dialog, id) -> {
            mButton.setEnabled(false); // Se deshabilita el boton durante 14 dias cuando se confirma el positivo COVID
            mPreferences.edit().putString("fechaCovid", LocalDate.now().toString()).apply();

            getmRef().child(Mac).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task)
                {
                    if (task.isSuccessful())
                    {
                        for (DataSnapshot o : task.getResult().getChildren())
                        {
                            if (!o.getKey().equals("FCM_token") )
                            {
                                getmRef().orderByKey().equalTo(o.getKey()).addListenerForSingleValueEvent(new ValueEventListener()
                                {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot)
                                    {
                                        /* Se comprueba si existe el token en la BBDD. En caso de que exista,
                                        se le manda la notificación creando un hilo nuevo */
                                        if (snapshot.exists())
                                        {
                                            new Thread()
                                            {
                                                @Override
                                                public void run()
                                                {
                                                    super.run();
                                                    try
                                                    {
                                                        URL url = new URL("http://35.195.162.3:3000");
                                                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                                                        connection.setDoOutput(true);
                                                        connection.setChunkedStreamingMode(0);
                                                        OutputStream out = new BufferedOutputStream(connection.getOutputStream());
                                                        String s = (String) snapshot.child(o.getKey()).child("FCM_token").getValue();
                                                        assert s != null;
                                                        out.write(s.getBytes());
                                                        out.flush();
                                                        out.close();

                                                        connection.disconnect();
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            }.start();
                                        }
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error)
                                    {
                                        Toast.makeText(getApplicationContext(), "ERROR" + error.getDetails(), Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        }
                    }
                }
            });

            dialog.dismiss();
            showToast();
        });

        builder.setNegativeButton(R.string.text_no, (dialog, id) -> dialog.cancel());

        Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }


    /**
     * Método que muestra un mensaje de estado
     */
    private void showToast()
    {
        Toast.makeText(this, R.string.toast_text_state, Toast.LENGTH_LONG).show();
    }


    /**
     * Método que se ejecuta cuando se para la actividad
     */
    @Override
    protected void onStop()
    {
        super.onStop();
    }
}