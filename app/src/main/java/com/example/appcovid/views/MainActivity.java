package com.example.appcovid.views;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.preference.PreferenceManager;
import android.view.View;
import android.provider.Settings.Secure;

import com.example.appcovid.R;
import com.example.appcovid.controller.BluetoothReceiver;
//import com.example.appcovid.model.DeviceList;

import java.util.UUID;

public class MainActivity extends AppCompatActivity {
/*
    private int mTituloBT, mTextoBT, mTextoBTError;
    private BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    //private BluetoothReceiver mBluetoothReceiver;
    public static int REQUEST_BLUETOOTH = 1;
    private DeviceList deviceList;
    //private static final int mBluetoothRequestCode = 0;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
/*
        mTituloBT = R.string.main_dialog_titleBT;
        mTextoBT = R.string.main_dialog_textBT;
        mTextoBTError = R.string.main_dialog_textBTError;
*/
        //mBluetoothReceiver = new BluetoothReceiver();

        // Se comprueba si la ID del dispositivo ya se ha guardado
        /*if (!mPreferences.contains("confirmacionID")) {
            lanzarAlert(mTituloID, mTextoID);
        }*/

        // Se comprueba si el Bluetooth está activado o esta soportado por el dispositivo
/*        if (mBluetoothAdapter != null) {
            if(!mBluetoothAdapter.isEnabled()) {
                lanzarAlert(mTituloBT, mTextoBT);
            }
        } else {
            lanzarAlert(mTituloBT, mTextoBTError);
        }

        IntentFilter filtro = new IntentFilter(BluetoothDevice.ACTION_FOUND);

        deviceList = new DeviceList(mBluetoothAdapter);
        registerReceiver(deviceList.bReciever, filtro);
*/
        //Se informa al usuario que el dispositivo se va a abrir a ser descubierto por otros
        //Si la longitud del extra se pone a 0, el dispositivo siempre se podrá descubrir
        /*Intent discoverableIntent =
                new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 0);
        startActivity(discoverableIntent);*/


        // Intent que lanza la función onReceive del receiver, donde realizaremos el tratamiento
        // de los datos*/

    }
/*
    private void lanzarAlert(int titulo, int texto) {
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
                if(texto == mTextoBTError) {
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