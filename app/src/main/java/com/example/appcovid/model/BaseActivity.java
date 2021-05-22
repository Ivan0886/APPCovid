package com.example.appcovid.model;

import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.appcovid.R;
import com.example.appcovid.views.MainActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public abstract class BaseActivity extends AppCompatActivity {
    protected static final String TAG = BaseActivity.class.getName();
    private BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private DeviceList mDeviceList;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;
    public static int REQUEST_BLUETOOTH = 1;
    public static String Mac = null;
    public static boolean isAppWentToBg = true;
    public static boolean isWindowFocused = false;
    public static boolean isMenuOpened = false;
    public static boolean isBackPressed = false;

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            Log.d("onReceive", "onReceive: Entrando en onReceive");
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                Log.d("MAC", deviceHardwareAddress);
                // TODO: Buscar una mejor forma de descubrir dispositivos constantemente
                // TODO: Evitar que nos salga el alert de confirmación en todas las actividades

                //mRef.child("Direcciones").setValue(Mac);
                //mRef.child(Mac).setValue(deviceHardwareAddress);
                // mRef.child(Mac).push().setValue(deviceHardwareAddress);

                //DatabaseReference newRef = mRef.child(Mac).push();
                mRef.child(Mac).child(deviceHardwareAddress).setValue("holaaaaaaaa");
                //TODO: Contar los 15 (para demo 5) minutos antes de insertar
                //newRef.setValue(deviceHardwareAddress);


                //mRef = mDatabase.getReference().child(getMacAddress());
                // Set valores a la Base de Datos
                //mRef.setValue(getMacAddress());

                /*mRef.child(Mac).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Map<String, Object> td = (HashMap<String, Object>) dataSnapshot.getValue();
                        List<Object> values = new ArrayList<>(td.values());

                        for(Object value : values) {
                            Log.d(TAG, "onDataChange: " + value);
                            Log.d("HardwareAddress", "onDataChange: " + deviceHardwareAddress);
                            /*if (!value.equals(deviceHardwareAddress)) {
                                newRef.setValue(deviceHardwareAddress);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });*/
            }else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                mBluetoothAdapter.startDiscovery();
            }
        }
    };


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Visibilidad de nuestro dispositivo
        Intent discoverableIntent =
                new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 0);
        startActivity(discoverableIntent);

        Log.d(TAG, "onCreate: En onCreate");
        mBluetoothAdapter.startDiscovery();
        // Detetar dispositivos
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(mReceiver, filter);

        mBluetoothAdapter.startDiscovery();

        mDatabase = FirebaseDatabase.getInstance("https://fctdam-45f92-default-rtdb.europe-west1.firebasedatabase.app/");
        mRef = mDatabase.getReference();

    }


    @Override
    protected void onStart() {
        Log.d(TAG, "onStart isAppWentToBg " + isAppWentToBg);
        applicationWillEnterForeground();

        super.onStart();
    }


    private void applicationWillEnterForeground() {
        if (isAppWentToBg) {
            isAppWentToBg = false;

            if (mBluetoothAdapter != null) {
                if(!mBluetoothAdapter.isEnabled()) {
                    lanzarAlert(R.string.main_dialog_titleBT, R.string.main_dialog_textBT);
                } else {
                    if (!PreferenceManager.getDefaultSharedPreferences(this).contains("MAC")) {
                        Mac = getMac();
                        PreferenceManager.getDefaultSharedPreferences(this).edit().putString("MAC", Mac).apply();
                    } else {
                        Mac = PreferenceManager.getDefaultSharedPreferences(this).getString("MAC", "??");
                    }
                }
            } else {
                lanzarAlert(R.string.main_dialog_titleBT, R.string.main_dialog_textBTError);
            }

            Toast.makeText(getApplicationContext(), "App is in foreground", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop ");
        applicationdidenterbackground();
    }


    public void applicationdidenterbackground() {
        if (!isWindowFocused) {
            isAppWentToBg = true;
            //Toast.makeText(getApplicationContext(), "App is Going to Background", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onBackPressed() {
        if (this instanceof MainActivity) {
        } else {
            isBackPressed = true;
        }

        Log.d(TAG, "onBackPressed " + isBackPressed + "" + this.getLocalClassName());
        super.onBackPressed();
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        isWindowFocused = hasFocus;

        if (isBackPressed && !hasFocus) {
            isBackPressed = false;
            isWindowFocused = true;
        }

        super.onWindowFocusChanged(hasFocus);
    }


    private void lanzarAlert(int titulo, int texto) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        EditText inputMAC = new EditText(this);
        inputMAC.setHint(R.string.text_hint_inputMac);

        builder.setTitle(getString(titulo));
        builder.setMessage(getString(texto));
        if(texto == R.string.main_dialog_textMACInfo) {
            builder.setView(inputMAC);
        }
        builder.setCancelable(false);

        builder.setPositiveButton(R.string.text_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(texto == R.string.main_dialog_textBTError) {
                    dialog.dismiss();
                    finish();
                } else if(texto == R.string.main_dialog_textMACInfo) {
                    // TODO Hacer comprobaciones de longitud, etc en el texto introducido
                    Mac = String.valueOf(inputMAC.getText());
                } else {
                    Intent enableBT = new Intent(mBluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBT, REQUEST_BLUETOOTH);
                    dialog.dismiss();
                }
            }
        });

        if(texto == R.string.main_dialog_textBTError) {
            builder.setNegativeButton(R.string.text_no, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    finish();
                }
            });
        } else if(texto == R.string.main_dialog_textMACInfo) {
            builder.setNegativeButton(R.string.text_look_mac, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(new Intent(android.provider.Settings.ACTION_DEVICE_INFO_SETTINGS));
                }
            });
        }

        Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }


    public String getMac() {
        // TODO Hacerlo para Android > 8
        // mBluetoothAdapter.getAddress();
        // new Intent(android.provider.Settings.ACTION_DEVICE_INFO_SETTINGS)
        if(Build.VERSION.SDK_INT <= 23) {
            Mac = android.provider.Settings.Secure.getString(getApplicationContext().getContentResolver(), "bluetooth_address");
        } else {
            lanzarAlert(R.string.main_dialog_titleMAC, R.string.main_dialog_textMACInfo);
            //startActivity(new Intent(android.provider.Settings.ACTION_DEVICE_INFO_SETTINGS));
            //MAC = "";
        }
        return Mac;
    }

}