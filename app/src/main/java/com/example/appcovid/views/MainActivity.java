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
import com.example.appcovid.model.BaseActivity;
import com.example.appcovid.model.DeviceList;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.List;
import java.util.prefs.PreferenceChangeEvent;

public class MainActivity extends BaseActivity {
    private DeviceList mDeviceList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

  /*  private void lanzarAlert(int titulo, int texto) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(titulo));
        builder.setMessage(getString(texto));
        builder.setCancelable(false);
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
        Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
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