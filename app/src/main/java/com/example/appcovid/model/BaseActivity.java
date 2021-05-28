package com.example.appcovid.model;

import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.appcovid.R;
import com.example.appcovid.views.MainActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * Clase abstracta base de la App
 * @author Iván Moriche Damas
 * @author Rodrigo Garcia
 * @author Iustin Mocanu
 * @version 28/05/2021/A
 * @see AppCompatActivity
 * @see BroadcastReceiver
 * @see DatabaseReference
 */
public abstract class BaseActivity extends AppCompatActivity
{
    public static int REQUEST_BLUETOOTH = 1;
    public static String Mac = null;
    public static boolean isAppWentToBg = true;
    public static boolean isWindowFocused = false;
    public static boolean isBackPressed = false;
    private static final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private static final FirebaseDatabase mDatabase = FirebaseDatabase.getInstance("https://fctdam-45f92-default-rtdb.europe-west1.firebasedatabase.app/");
    private final ArrayList<String> mList = new ArrayList<>();
    private DatabaseReference mRef;
    private final BroadcastReceiver mReceiver = new BroadcastReceiver()
    {
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action))
            {
                // Se ha encontrado un dispositivo. Se obtiene el objeto BluetoothDevice y su información del Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                new CountDownTimer(30000, 1000)
                {
                    public void onTick(long millisUntilFinished)
                    { }

                    public void onFinish()
                    {
                        mRef.child(device.getAddress().toUpperCase()).get().addOnCompleteListener(task -> {
                            if (task.isSuccessful())
                            {
                                if (task.getResult().getValue() != null)
                                {
                                    if (mList.contains(device.getAddress().toUpperCase()))
                                    {
                                        mRef.child(Mac.toUpperCase()).child(device.getAddress().toUpperCase()).setValue(device.getName());
                                    } else {
                                        mList.add(device.getAddress().toUpperCase());
                                    }
                                    cancel();
                                }
                            }
                        });
                    }
                }.start();
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                mBluetoothAdapter.startDiscovery();
            }
        }
    };


    /**
     * Método que se ejecuta al arrancar la actividad. Se obtiene la referencia a la BBDD
     * @param savedInstanceState instancia de la actividad
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);

        mRef = mDatabase.getReference();
    }


    /**
     * Método que se ejecuta cuando se inicia una actividad
     */
    @Override
    protected void onStart()
    {
        applicationWillEnterForeground();
        super.onStart();
    }


    /**
     * Método que comprueba si la App está en primer plano.
     * También se comprueba si el Bluetooth está desactivado y se activa nuesto dispositivo
     */
    private void applicationWillEnterForeground()
    {
        if (isAppWentToBg)
        {
            isAppWentToBg = false;

            if (mBluetoothAdapter != null)
            {
                if (!mBluetoothAdapter.isEnabled())
                {
                    launchAlert(R.string.main_dialog_titleBT, R.string.main_dialog_textBT);
                } else {
                    if (!PreferenceManager.getDefaultSharedPreferences(this).contains("MAC"))
                    {
                        Mac = getMac();
                        PreferenceManager.getDefaultSharedPreferences(this).edit().putString("MAC", Mac).apply();
                    } else {
                        Mac = PreferenceManager.getDefaultSharedPreferences(this).getString("MAC", "??");
                    }

                    // Visibilidad de nuestro dispositivo
                    Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                    discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 0);
                    startActivity(discoverableIntent);

                    // Detetar dispositivos
                    IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                    filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
                    registerReceiver(mReceiver, filter);

                    mBluetoothAdapter.startDiscovery();
                }
            } else {
                launchAlert(R.string.main_dialog_titleBT, R.string.main_dialog_textBTError);
            }
        }
    }


    /**
     * Método que se ejecuta cuando se para la App y se comprueba si está en segundo plano
     */
    @Override
    protected void onStop()
    {
        super.onStop();
        applicationDidEnterBackground();
    }


    /**
     * Método que comprueba si la App está en segundo plano
     */
    public void applicationDidEnterBackground()
    {
        if (!isWindowFocused)
        {
            isAppWentToBg = true;
        }
    }


    /**
     * Método que determina el comportamiento del botón "<-"
     */
    @Override
    public void onBackPressed()
    {
        if (!(this instanceof MainActivity))
        {
            isBackPressed = true;
        }

        super.onBackPressed();
    }


    /**
     * Método que se ejecuta al cambiar el foco
     * @param hasFocus foco
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus)
    {
        isWindowFocused = hasFocus;

        if (isBackPressed && !hasFocus)
        {
            isBackPressed = false;
            isWindowFocused = true;
        }

        super.onWindowFocusChanged(hasFocus);
    }


    /**
     * Método que lanza un alert distinto dependiendo de los parametros pasados
     * @param title título de la alerta
     * @param text texto de la alerta
     */
    protected void launchAlert(int title, int text)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        EditText inputMAC = new EditText(this);
        inputMAC.setHint(R.string.text_hint_inputMac);

        builder.setTitle(getString(title));
        builder.setMessage(getString(text));
        if (text == R.string.main_dialog_textMACInfo)
        {
            builder.setView(inputMAC);
        }
        builder.setCancelable(false);

        builder.setPositiveButton(R.string.text_ok, (dialog, which) -> {

            if (text == R.string.main_dialog_textBTError || text == R.string.error_text_service)
            {
                dialog.dismiss();
                finish();
            } else if (text == R.string.main_dialog_textMACInfo) {
                // TODO Hacer comprobaciones de longitud, etc en el texto introducido
                Mac = String.valueOf(inputMAC.getText());
            } else {
                Intent enableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBT, REQUEST_BLUETOOTH);
                dialog.dismiss();
            }
        });

        if (text == R.string.main_dialog_textMACInfo)
        {
            builder.setNegativeButton(R.string.text_look_mac, (dialog, which) -> startActivity(new Intent(android.provider.Settings.ACTION_DEVICE_INFO_SETTINGS)));
        }

        Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }


    /**
     * Método que devuelve la referencia a la BBDD
     * @return mRef
     */
    public DatabaseReference getmRef()
    {
        return mRef;
    }


    /**
     * Método que devuelve la dirección Mac de distinta forma dependiendo de la versión del dispositivo
     * @return Mac
     */
    public String getMac()
    {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
        {
            Mac = android.provider.Settings.Secure.getString(getApplicationContext().getContentResolver(), "bluetooth_address");
        } else {
            //Mac = "06:06:5A:43:40";
            launchAlert(R.string.main_dialog_titleMAC, R.string.main_dialog_textMACInfo);
        }
        return Mac;
    }
}