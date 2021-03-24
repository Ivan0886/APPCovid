package com.example.appcovid.controller;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BluetoothReceiver extends BroadcastReceiver {

    public BluetoothReceiver() {
        super();
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        String accionDeIntent = intent.getAction();
        if (BluetoothDevice.ACTION_FOUND.equals(accionDeIntent)) {
            //Se ha descubierto un dispositivo
            //TODO: Tratar datos
            BluetoothDevice dispositivo = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            String nombreDispostivo = dispositivo.getName();
            String direccionDispositivo = dispositivo.getAddress(); // MAC address
        }
    }


}
