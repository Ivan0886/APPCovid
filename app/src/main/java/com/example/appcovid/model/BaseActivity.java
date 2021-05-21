package com.example.appcovid.model;

import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.appcovid.R;
import com.example.appcovid.views.MainActivity;


public abstract class BaseActivity extends AppCompatActivity {
    protected static final String TAG = BaseActivity.class.getName();
    private BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private DeviceList mDeviceList;
    public static int REQUEST_BLUETOOTH = 1;
    public static boolean isAppWentToBg = true;
    public static boolean isWindowFocused = false;
    public static boolean isMenuOpened = false;
    public static boolean isBackPressed = false;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
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
                        PreferenceManager.getDefaultSharedPreferences(this).edit().putString("MAC", getMac()).apply();
                        Log.d("MIMAC", PreferenceManager.getDefaultSharedPreferences(this).getString("MAC", "??"));
                    } else {
                        Log.d("MIMAC2", PreferenceManager.getDefaultSharedPreferences(this).getString("MAC", "??"));
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
        builder.setTitle(getString(titulo));
        builder.setMessage(getString(texto));
        builder.setCancelable(false);

        builder.setPositiveButton(R.string.text_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(texto == R.string.main_dialog_textBTError) {
                    dialog.dismiss();
                    finish();
                } else {
                    Intent enableBT = new Intent(mBluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBT, REQUEST_BLUETOOTH);
                    dialog.dismiss();
                }
            }
        });

        if(texto != R.string.main_dialog_textBTError) {
            builder.setNegativeButton(R.string.text_no, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    finish();
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
        return android.provider.Settings.Secure.getString(getApplicationContext().getContentResolver(), "bluetooth_address");
    }

}