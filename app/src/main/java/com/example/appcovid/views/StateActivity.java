package com.example.appcovid.views;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.appcovid.R;
import org.threeten.bp.LocalDate;

public class StateActivity extends AppCompatActivity {

    Button boton;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_state);

        preferences = PreferenceManager.getDefaultSharedPreferences(StateActivity.this);
        boton = (Button) findViewById(R.id.button_covid);
        boton.setEnabled(comprobarTiempoConfirmacionCovid()); // Se comprueba si han pasado 14 dias

        // TODO ¿Crear un fragmento en las notificaciones?
    }

    private boolean comprobarTiempoConfirmacionCovid() {
        String fecha = preferences.getString("fechaCovid", "unknown");
        boolean valor = true;

        if (!fecha.equalsIgnoreCase("unknown")) {
            valor = LocalDate.parse(fecha).isAfter(LocalDate.parse(fecha).plusDays(14));
        }

        return valor;
    }

    public void volverMainActivity(View v){
       /* Intent intent = new Intent(StateActivity.this, MainActivity.class);
        startActivity(intent);*/

        finish();
    }

    public void confirmarCovid(View v){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.dialog_title);
        builder.setMessage(R.string.dialog_text);

        builder.setPositiveButton(R.string.button_si, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Este codigo permite guardar la fecha aunque se destruya la actividad
                SharedPreferences.Editor myEditor = preferences.edit();
                myEditor.putString("fechaCovid", LocalDate.now().toString()); // Se guarda la fecha que el usuario confirma que tiene el COVID
                myEditor.commit();

                boton.setEnabled(false); // Se deshabilita el boton durante 14 dias cuando se confirma el positivo COVID
                dialog.dismiss();

                mostrarToast(R.string.toast_text);
            }
        });

        builder.setNegativeButton(R.string.button_no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void mostrarToast(int mensaje) {
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show();
    }
}