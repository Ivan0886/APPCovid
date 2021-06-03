package com.example.appcovid.model;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.appcovid.R;
import com.example.appcovid.views.MainActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
    public static String Mac = "";
    public static boolean isAppWentToBg = true;
    public static boolean isWindowFocused = false;
    public static boolean isBackPressed = false;
    protected static final int ALL_PERMISSIONS_RESULT = 101;
    protected final static List<Object> mPermissionsRejected = new ArrayList<>();
    protected final static List<Object> mPermissions = new ArrayList<>();
    protected List<Object> mPermissionsToRequest;
    private static final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private static final FirebaseDatabase mDatabase = FirebaseDatabase.getInstance("https://fctdam-45f92-default-rtdb.europe-west1.firebasedatabase.app/");
    private final ArrayList<String> mList = new ArrayList<>();
    private static DatabaseReference mRef;
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
                    public void onTick(long millisUntilFinished) { }

                    public void onFinish()
                    {
                        String deviceAddress = device.getAddress().toUpperCase();
                        mRef.child(deviceAddress).get().addOnCompleteListener(task -> {
                            if (task.isSuccessful())
                            {
                                if (task.getResult().getValue() != null)
                                {
                                    if (mList.contains(deviceAddress))
                                    {
                                        mRef.child(Mac.toUpperCase()).child(deviceAddress).setValue(LocalDate.now().toString());
                                    } else {
                                        mList.add(deviceAddress);
                                    }
                                }
                                cancel();
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

        mPermissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        mPermissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);

        mPermissionsToRequest = findAnswerPermissions((ArrayList) mPermissions);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if (mPermissionsToRequest.size() > 0)
            {
                requestPermissions((String[]) mPermissionsToRequest.toArray(
                        new String[mPermissionsToRequest.size()]),
                        ALL_PERMISSIONS_RESULT);
            }
        }

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
     * También se comprueba si el Bluetooth está desactivado y se activa nuestro dispositivo
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
                    Mac = getMac();

                    // Visibilidad de nuestro dispositivo
                    Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                    discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 0);
                    startActivity(discoverableIntent);

                    // Detetar dispositivos
                    IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                    filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
                    registerReceiver(mReceiver, filter);

                    mBluetoothAdapter.startDiscovery();

                    FirebaseMessaging.getInstance().getToken()
                            .addOnCompleteListener(task -> {
                                if (!task.isSuccessful())
                                {
                                    Log.w("FCM", "Fetching FCM registration token failed", task.getException());
                                    return;
                                }

                                // Get new FCM registration token
                                String token = task.getResult();
                                Log.d("FCM", "onComplete: " + token);
                                mRef.child(Mac).child("FCM_token").setValue(token);
                            });
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
     * @deprecated startActivityForResult
     */
    protected void launchAlert(int title, int text)
    {
        // Creación Title Alert
        TextView titleView = new TextView(getApplicationContext());
        titleView.setText(getString(title));
        titleView.setPadding(20, 30, 20, 30);
        titleView.setTextSize(20F);
        titleView.setBackgroundColor(Color.RED); // Rojo
        titleView.setTextColor(Color.WHITE);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        EditText inputMAC = new EditText(this);
        inputMAC.setHint(R.string.text_hint_inputMac);

        if (text == R.string.main_dialog_textMACInfo)
        {
            titleView.setBackgroundColor(Color.parseColor("#FFFFBB33")); // Amarillo
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
                Mac = String.valueOf(inputMAC.getText()).toUpperCase();
                PreferenceManager.getDefaultSharedPreferences(this).edit().putString("MAC", Mac).apply();
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

        builder.setCustomTitle(titleView);
        builder.setMessage(getString(text));

        Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }


    /**
     * Método que devuelve la referencia a la BBDD
     * @return mRef
     */
    public static DatabaseReference getmRef()
    {
        return mRef;
    }


    /**
     * Método que devuelve la dirección Mac de distinta forma dependiendo de la versión del dispositivo
     * @return Mac
     */
    @SuppressLint("HardwareIds")
    public String getMac()
    {
        if (PreferenceManager.getDefaultSharedPreferences(this).contains("MAC"))
        {
            Mac = PreferenceManager.getDefaultSharedPreferences(this).getString("MAC", "??");

        } else {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            {
                Mac = mBluetoothAdapter.getAddress().toUpperCase();
            } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                Mac = android.provider.Settings.Secure.getString(getApplicationContext().getContentResolver(), "bluetooth_address").toUpperCase();
            } else {
                //Mac = "06:06:5A:43:40";
                launchAlert(R.string.main_dialog_titleMAC, R.string.main_dialog_textMACInfo);
            }
            PreferenceManager.getDefaultSharedPreferences(this).edit().putString("MAC", Mac).apply();
        }
        return Mac;
    }


    /**
     * Método que consulta los permisos de App
     * @param wanted listado de permisos
     * @return result
     */
    protected ArrayList<Object> findAnswerPermissions(ArrayList wanted)
    {
        ArrayList<Object> result = new ArrayList<>();

        for (Object permission : wanted)
        {
            if (!youHavePermission((String)permission))
            {
                result.add(permission);
            }
        }
        return result;
    }


    /**
     * Método que comprueba si tienes permiso
     * @param permission aceptación del permiso
     * @return true
     */
    private boolean youHavePermission(String permission)
    {
        if (canMakeSmores())
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            {
                return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }


    /**
     * Método que comprueba la versión del dispositivo
     * @return Build.Version
     */
    private boolean canMakeSmores()
    {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }


    /**
     * Método que solicita los permisos de la App de distinta forma dependiendo de la versión del dispositivo
     * @param requestCode codigo
     * @param permissions array de permisos
     * @param grantResults permisos aceptados
     */
    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == ALL_PERMISSIONS_RESULT)
        {
            for (Object permission : mPermissionsToRequest)
            {
                if (!youHavePermission((String) permission))
                {
                    mPermissionsRejected.add(permission);
                }
            }
        }

        if (mPermissionsRejected.size() > 0)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            {
                if (shouldShowRequestPermissionRationale((String) mPermissionsRejected.get(0)))
                {
                    finish();
                }
            }
        }
    }
}