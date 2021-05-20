package com.example.appcovid.views;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.appcovid.R;
import com.example.appcovid.model.BaseActivity;

import org.threeten.bp.LocalDate;

public class StateActivity extends BaseActivity {

    private Button mBoton;
    private SharedPreferences mPreferences;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_state);

        mPreferences = PreferenceManager.getDefaultSharedPreferences(StateActivity.this);
        mBoton = (Button) findViewById(R.id.button_covid);
        mBoton.setEnabled(comprobarTiempoConfirmacionCovid()); // Se comprueba si han pasado 14 dias

        // TODO ¿Crear un fragmento en las notificaciones?
    }


    private boolean comprobarTiempoConfirmacionCovid() {
        String fecha = mPreferences.getString("fechaCovid", "unknown");
        boolean valor = true;

        if (!fecha.equalsIgnoreCase("unknown")) {
            valor = LocalDate.now().isAfter(LocalDate.parse(fecha).plusDays(14));
        }

        return valor;
    }


    public void volverMainActivity(View v) {
       // startActivity(new Intent(StateActivity.this, MainActivity.class));
        finish();
    }


    public void confirmarCovid(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.dialog_title_state);
        builder.setMessage(R.string.dialog_text_state);
        builder.setCancelable(false);

        builder.setPositiveButton(R.string.text_si, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Este codigo permite guardar la fecha aunque se destruya la actividad
                SharedPreferences.Editor myEditor = mPreferences.edit();
                myEditor.putString("fechaCovid", LocalDate.now().toString()); // Se guarda la fecha que el usuario confirma que tiene el COVID
                myEditor.commit();

                mBoton.setEnabled(false); // Se deshabilita el boton durante 14 dias cuando se confirma el positivo COVID
                dialog.dismiss();

                mostrarToast(R.string.toast_text_state);
            }
        });

        builder.setNegativeButton(R.string.text_no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }


    private void mostrarToast(int mensaje) {
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show();
    }
}