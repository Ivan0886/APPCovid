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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
    protected static final int ALL_PERMISSIONS_RESULT = 101;
    protected final static List<Object> mPermissionsRejected = new ArrayList<>();
    protected final static List<Object> mPermissions = new ArrayList<>();
    protected List<Object> mPermissionsToRequest;
    protected static final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
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
                        String deviceAddress = null;
                        try {
                            deviceAddress = md5Mac(device.getAddress().toUpperCase());
                        } catch (NoSuchAlgorithmException e) {
                            e.printStackTrace();
                        }

                        String finalDeviceAddress = deviceAddress;
                        mRef.child(finalDeviceAddress).get().addOnCompleteListener(task -> {
                            if (task.isSuccessful())
                            {
                                if (task.getResult().getValue() != null)
                                {
                                    if (mList.contains(finalDeviceAddress))
                                    {
                                        mRef.child(Mac.toUpperCase()).child(finalDeviceAddress).setValue(LocalDate.now().toString());
                                    } else {
                                        mList.add(finalDeviceAddress);
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
     * Método que se ejecuta cuando se inicia una actividad. Se comprueba el estado del Bluetooth
     * y hace visible al dispositivo.
     */
    @Override
    protected void onStart()
    {
        super.onStart();
        if (mBluetoothAdapter != null)
        {
            if (!mBluetoothAdapter.isEnabled())
            {
                launchAlert(R.string.main_dialog_titleBT, R.string.main_dialog_textBT, BaseActivity.this);
            } else {
                try {
                    Mac = getMac();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }

                // Visibilidad de nuestro dispositivo
                if (mBluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE)
                {
                    Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                    discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 0);
                    startActivity(discoverableIntent);
                }

                // Detetar dispositivos
                IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
                registerReceiver(mReceiver, filter);

                mBluetoothAdapter.startDiscovery();

                FirebaseMessaging.getInstance().getToken()
                        .addOnCompleteListener(task -> {
                            if (!task.isSuccessful())
                            {
                                launchAlert(R.string.error_title, R.string.error_text_service, BaseActivity.this);
                                return;
                            }

                            if (haveNetworkConnection())
                            {
                                // Coge el token FCM de registro
                                String token = task.getResult();
                                Log.d("FCM", "onComplete: " + token);
                                if (!Mac.equals(""))
                                {
                                    mRef.child(Mac).child("FCM_token").setValue(token);
                                }
                            }
                        });
            }
        } else {
            launchAlert(R.string.main_dialog_titleBT, R.string.main_dialog_textBTError, BaseActivity.this);
        }
    }


    /**
     * Método que comprueba si el nternet está activado
     * @return haveConnectedWifi || haveConnectedMobile
     */
    protected boolean haveNetworkConnection()
    {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo)
        {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected()) haveConnectedWifi = true;

            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected()) haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }


    /**
     * Método que lanza un alert distinto dependiendo de los parametros pasados
     * @param title título de la alerta
     * @param text texto de la alerta
     * @param pContext contexto de la actividad
     * @deprecated startActivityForResult
     */
    protected void launchAlert(int title, int text, Context pContext)
    {
        // Creación Title Alert
        TextView titleView = new TextView(pContext);
        titleView.setText(getString(title));
        titleView.setPadding(20, 30, 20, 30);
        titleView.setTextSize(20F);
        titleView.setBackgroundColor(Color.RED); // Rojo
        titleView.setTextColor(Color.WHITE);

        AlertDialog.Builder builder = new AlertDialog.Builder(pContext);

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
                try
                {
                    Mac = md5Mac(inputMAC.getText().toString().toUpperCase());
                    PreferenceManager.getDefaultSharedPreferences(this).edit().putString("MAC", Mac).apply();
                    startActivity(new Intent(this, MainActivity.class));
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
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
     * @throws NoSuchAlgorithmException excepción
     */
    @SuppressLint("HardwareIds")
    public String getMac() throws NoSuchAlgorithmException
    {
        if (PreferenceManager.getDefaultSharedPreferences(this).contains("MAC") &&
                !PreferenceManager.getDefaultSharedPreferences(this).getString("MAC", "??").equals(""))
        {
            Mac = PreferenceManager.getDefaultSharedPreferences(this).getString("MAC", "??");

        } else {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            {
                Mac = md5Mac(mBluetoothAdapter.getAddress().toUpperCase());
            } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                Mac = md5Mac(android.provider.Settings.Secure.getString(getApplicationContext().getContentResolver(), "bluetooth_address").toUpperCase());
            } else {
                //Mac = "06:06:5A:43:40";
                launchAlert(R.string.main_dialog_titleMAC, R.string.main_dialog_textMACInfo, BaseActivity.this);
            }

            PreferenceManager.getDefaultSharedPreferences(this).edit().putString("MAC", Mac).apply();
        }
        return Mac.contains(":") ? md5Mac(Mac) : Mac;
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
    @TargetApi(Build.VERSION_CODES.M) @Override
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


    /**
     * Método que encripta la direcciones MAC para introducirlas en la BBDD
     * @param mac dirección MAC
     * @return hashmac.toString()
     * @throws NoSuchAlgorithmException excepción
     */
    private static String md5Mac(String mac) throws NoSuchAlgorithmException
    {
        // La MAC se pasa a MD5
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] messageDigest = md.digest(mac.toUpperCase().getBytes());
        BigInteger hashmac = new BigInteger(1, messageDigest);

        return hashmac.toString();
    }
}