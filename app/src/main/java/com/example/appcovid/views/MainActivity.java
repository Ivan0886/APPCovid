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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // TODO: Comprobar si al cerrar la app del todo se siguen registrando usuarios conectados
        //unregisterReceiver(mBluetoothReceiver);
    }
}