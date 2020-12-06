package com.example.appcovid.views;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;

import com.example.appcovid.R;

import org.threeten.bp.LocalDate;

public class MainActivity extends AppCompatActivity {

    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        preferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        if (!preferences.getBoolean("confirmacionPermisos", false)) {
            lanzarAlert();
        }
    }

    private void lanzarAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.main_dialog_title));
        builder.setMessage(getString(R.string.notifications_text));
        builder.setNegativeButton(R.string.button_no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //TODO: Pedir permisos de app

                SharedPreferences.Editor myEditor = preferences.edit();
                myEditor.putBoolean("confirmacionPermisos", true); // Se guarda la fecha que el usuario confirma que tiene el COVID
                myEditor.commit();
            }
        });
        builder.create().show();
    }



    public void onClickLanzarActivity(View v) {
        String id = v.getResources().getResourceEntryName(v.getId());

        switch(id) {
            case "button_news":
                startActivity(new Intent(this, NewsActivity.class));
                break;
            case "button_restriction":
                startActivity(new Intent(this, RestrictionsActivity.class));
                break;
            default:
                startActivity(new Intent(this, StateActivity.class));
        }
    }
}