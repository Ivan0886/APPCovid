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
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.appcovid.R;
import com.example.appcovid.controller.BluetoothCountdown;
import com.example.appcovid.views.MainActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.prefs.PreferenceChangeEvent;


public abstract class BaseActivity extends AppCompatActivity {
    protected static final String TAG = BaseActivity.class.getName();
    private BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;
    public static int REQUEST_BLUETOOTH = 1;
    public static String Mac = null;
    public static boolean isAppWentToBg = true;
    public static boolean isWindowFocused = false;
    public static boolean isMenuOpened = false;
    public static boolean isBackPressed = false;

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        private ArrayList<String> macListOld = new ArrayList<>();
        private ArrayList<String> macListNew = null;
        private ArrayList<BluetoothCountdown> timerList = new ArrayList<>();

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //TODO: Comprobar que la MAC recibida esté registrada en la BBDD
                BluetoothCountdown count = new BluetoothCountdown(device.getAddress(), Mac, mRef, 60000, 1000, device.getName());
                // TODO: Buscar una mejor forma de descubrir dispositivos
                // TODO: Evitar que nos salga el alert de confirmación en todas las actividades

                //comprobamos que es el primer escaneo
                if (macListNew == null) {
                    macListOld.add(device.getAddress());
                    count.start();
                    timerList.add(count);
                } else {
                    macListNew.add(device.getAddress());
                    if (!timerList.contains(count)) {
                        count.start();
                        timerList.add(count);
                    }
                }

            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                if (macListNew == null) {
                    macListNew = new ArrayList<>();
                } else {
                    ArrayList<String> newMacList = new ArrayList<>();
                    for (String macCheck : macListNew) {
                        if (!macListOld.contains(macCheck)) {
                            newMacList.add(macCheck);
                        }
                    }
                    for (BluetoothCountdown count : timerList) {
                        if (newMacList.contains(count.getmDeviceMac())) {
                            count.cancelCounting();

                            timerList.remove(count);
                        }
                    }


                    macListOld = new ArrayList<>();
                    Collections.copy(macListOld, macListNew);
                    macListNew = new ArrayList<>();
                }
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

        mRef.child(Mac);
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