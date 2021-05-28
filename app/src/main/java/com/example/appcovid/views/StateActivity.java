package com.example.appcovid.views;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.example.appcovid.R;
import com.example.appcovid.model.BaseActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import org.threeten.bp.LocalDate;

/**
 * Clase que ¿¿???
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
    private DatabaseReference mRef;
    private OnCompleteListener <DataSnapshot> mListener;

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
     * inhabilita el botón en el caso de que el usuario pulse SI
     * @param v
     */
    public void alertConfirmCovid(View v)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.dialog_title_state);
        builder.setMessage(R.string.dialog_text_state);
        builder.setCancelable(false);

        builder.setPositiveButton(R.string.text_si, (dialog, id) -> {
            mButton.setEnabled(false); // Se deshabilita el boton durante 14 dias cuando se confirma el positivo COVID

            mRef = getmRef().child(Mac.toUpperCase());
            mListener = task -> {
                if (task.isSuccessful())
                {
                    for (DataSnapshot o : task.getResult().getChildren())
                    {
                        if (!o.getKey().equals("CovidAlert"))
                        {
                            getmRef().child(o.getKey()).child("CovidAlert").setValue(true);
                        }
                    }

                }
            };
            mRef.get().addOnCompleteListener(mListener);

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