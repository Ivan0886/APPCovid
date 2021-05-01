package com.example.appcovid.model;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class DeviceList {
    private ArrayList<DeviceItem> deviceItemList;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;

    public DeviceList(BluetoothAdapter bTAdapter) {

        getMacAddress();
        if (bTAdapter != null) {
            deviceItemList = new ArrayList<DeviceItem>();

            // Conexion a la base de datos FireBase
            mDatabase = FirebaseDatabase.getInstance("https://fctdam-45f92-default-rtdb.europe-west1.firebasedatabase.app/");
            mRef = mDatabase.getReference();
            //mRef.setValue(getMacAddress());

            Set<BluetoothDevice> pairedDevices = bTAdapter.getBondedDevices();
            if (pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {
                    DeviceItem newDevice = new DeviceItem(device.getAddress(), "false");
                    deviceItemList.add(newDevice);
                }
            }

            // If there are no devices, add an item that states so. It will be handled in the view.
            if (deviceItemList.size() == 0) {
                deviceItemList.add(new DeviceItem("", "false"));
            }
        } else {
            // TODO Controlar error dispositivo no tiene BT
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
                Log.d("MAC", newDevice.getmAddress());

                //mRef = mDatabase.getReference().child(getMacAddress());
                // Set valores a la Base de Datos
                //mRef.setValue(getMacAddress());
            }
        }
    };


    public void getMacAddress(){
        /*try {
            List<NetworkInterface> networkInterfaceList = Collections.list(NetworkInterface.getNetworkInterfaces());
            String stringMac = "";

            for (NetworkInterface networkInterface : networkInterfaceList) {
                if (networkInterface.getName().equalsIgnoreCase("wlon0")) {
                    for (int i = 0 ;i <networkInterface.getHardwareAddress().length; i++) {
                        String stringMacByte = Integer.toHexString(networkInterface.getHardwareAddress()[i]& 0xFF);
                        if (stringMacByte.length() == 1) {
                            stringMacByte = "0" +stringMacByte;
                        }
                        stringMac = stringMac + stringMacByte.toUpperCase() + ":";
                    }
                }
            }

            return stringMac;
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return  "0";*/
        try {
        List<NetworkInterface> networkInterfaceList = Collections.list(NetworkInterface.getNetworkInterfaces());
        for (NetworkInterface networkInterface : networkInterfaceList) {
            Log.d("MAC2", networkInterface.getName());
        }
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    // TODO: Si tiene conexion BT con otro dispositivo durante mas de 15 min, guardar IDs y fecha en la base de datos
    //  (el otro dispositivo tiene que tener la APP también)
}




/*import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;*/

//import redis.clients.jedis.Jedis;

/*import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;*/

/*public class DeviceList {
    private ArrayList<DeviceItem> mDeviceItemList;
    private BluetoothAdapter mBluetoothAdapter;
    private Jedis jedis;
    //private RedisClient mRedisClient;

    public DeviceList(BluetoothAdapter bTAdapter) {
        mDeviceItemList = new ArrayList<DeviceItem>();
        mBluetoothAdapter = bTAdapter;
        //jedis = new Jedis("83.38.116.69", 6278);
        //mRedisClient = RedisClient.create("redis://XJ37InyXOd@83.38.116.69:6278/0");

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                DeviceItem newDevice= new DeviceItem(device.getAddress(),"false");
                mDeviceItemList.add(newDevice);
            }
        }

        // If there are no devices, add an item that states so. It will be handled in the view.
        if(mDeviceItemList.size() == 0) {
            mDeviceItemList.add(new DeviceItem("", "false"));
        }
    }

    public final BroadcastReceiver bReciever = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                Log.d("DEVICELIST", "Bluetooth device found\n");
                // BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // Create a new device item
                //DeviceItem newDevice = new DeviceItem(device.getAddress(), "false");
                //mDeviceItemList.add(newDevice);

                //StatefulRedisConnection<String, String> connection = mRedisClient.connect();
                //RedisCommands<String, String> syncCommands = connection.sync();
                //syncCommands.lpush(mBluetoothAdapter.getAddress(), newDevice.getmAddress());
                //connection.close();

                //jedis.lpush("Prueba", newDevice.getmAddress());

                //final MyTask con = new MyTask();
                //con.execute();

                /*con.doInBackground(newDevice.getmAddress());
                Log.d("MAC", newDevice.getmAddress());*/

/*
                // TODO Primero comprobar si ya existe el dispositivo en la lista
                Log.d("-MIMAC-", mBluetoothAdapter.getAddress());
                //Log.d("MAC", newDevice.getmAddress());
            }
            //mRedisClient.shutdown();
            jedis.close();
        }


        /*class MyTask extends AsyncTask<Void, Void, String> {
            @Override
            protected String doInBackground(Void... arg0) {
                jedis = new Jedis("83.38.116.69", 6278);

                DeviceItem newDevice = new DeviceItem(device.getAddress(), "false");
                mDeviceItemList.add(newDevice);

                jedis.lpush("Prueba", arg0);
                return "exito";
            }
        }

    };
/*
    // TODO: Si tiene conexion BT con otro dispositivo durante mas de 15 min, guardar IDs y fecha en la base de datos
    //  (el otro dispositivo tiene que tener la APP también)
}*/


