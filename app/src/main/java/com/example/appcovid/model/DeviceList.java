/*package com.example.appcovid.model;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;
import java.util.Set;

public class DeviceList {
    private ArrayList<DeviceItem> deviceItemList;

    public DeviceList(BluetoothAdapter bTAdapter) {
        deviceItemList = new ArrayList<DeviceItem>();

        Set<BluetoothDevice> pairedDevices = bTAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                DeviceItem newDevice= new DeviceItem(device.getAddress(),"false");
                deviceItemList.add(newDevice);
            }
        }

        // If there are no devices, add an item that states so. It will be handled in the view.
        if(deviceItemList.size() == 0) {
            deviceItemList.add(new DeviceItem("", "false"));
        }
    }

    public final BroadcastReceiver bReciever = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                Log.d("DEVICELIST", "Bluetooth device found\n");
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // Create a new device item
                DeviceItem newDevice = new DeviceItem(device.getAddress(), "false");
                deviceItemList.add(newDevice);
                Log.d("MAC", newDevice.getAddress());
            }
        }
    };

    // TODO: Si tiene conexion BT con otro dispositivo durante mas de 15 min, guardar IDs y fecha en la base de datos
    //  (el otro dispositivo tiene que tener la APP también)
}
*/