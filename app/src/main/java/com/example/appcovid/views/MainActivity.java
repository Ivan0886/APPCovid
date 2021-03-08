package com.example.appcovid.views;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.provider.Settings.Secure;

import com.example.appcovid.R;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences mPreferences;
    private String mAndroidId;
    private int mTituloID, mTextoID, mTituloBT, mTextoBT;
    private BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTituloID = R.string.main_dialog_title;
        mTextoID = R.string.main_dialog_text;
        mTituloBT = R.string.main_dialog_titleBT;
        mTextoBT = R.string.main_dialog_textBT;

        mPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);

        // Se comprueba si la ID del dispositivo ya se ha guardado
        if (!mPreferences.contains("confirmacionID")) {
            lanzarAlert(mTituloID, mTextoID);
        }

        // Se comprueba si el Bluetooth está activado
        if(!mBluetoothAdapter.isEnabled()) {
            lanzarAlert(mTituloBT, mTextoBT);
        }
    }


    private void lanzarAlert(int titulo, int texto) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        mAndroidId = Secure.getString(getApplicationContext().getContentResolver(), Secure.ANDROID_ID);
        builder.setTitle(getString(titulo));
        builder.setMessage(getString(texto));
        if(titulo == mTituloID) {
            builder.setNegativeButton(R.string.text_no, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    finish();
                }
            });
        }

        builder.setPositiveButton(R.string.text_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(titulo == mTituloID) {
                    SharedPreferences.Editor myEditor = mPreferences.edit();
                    myEditor.putString("confirmacionID", mAndroidId); // Se guarda la confirmacion del alert
                    myEditor.commit();
                } else {
                    dialog.dismiss();
                    finish();
                }
            }
        });
        builder.create().show();
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
}