package com.example.appcovid.model;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.IBinder;
import android.provider.Settings;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.example.appcovid.R;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Clase que maneja los permisos y localización GPS
 * @author Iván Moriche Damas
 * @author Rodrigo Garcia
 * @author Iustin Mocanu
 * @version 28/05/2021/A
 * @see LocationListener
 * @see Service
 */
public class GPSLocation extends Service implements LocationListener
{
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60;
    private boolean mCanGetLocation = false;
    private final Context mContext;
    private Location mLocation;
    private String mPostalCode;
    private double mLatitude;
    private double mLongitude;
    protected LocationManager locationManager;
    Geocoder geocoder;
    List<Address> addresses;

    /**
     * Contructor de la clase
     * @param mContext contexto de la actividad
     */
    public GPSLocation(Context mContext)
    {
        this.mContext = mContext;
        this.mPostalCode = getmPostalCode();
    }


    /**
     * Método que devuelve la localización
     * @return mLocation
     */
    public String getmPostalCode()
    {
        try
        {
            locationManager = (LocationManager)mContext.getSystemService(LOCATION_SERVICE);

            // Obtiene el estado GPS
            boolean mCheckGPS = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            // boolean  mCheckGPS = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!mCheckGPS)
            {
                Toast.makeText(mContext, R.string.error_text_service, Toast.LENGTH_SHORT).show();
            } else {
                this.mCanGetLocation = true;

                // Si el GPS está habilitado, obtiene latitude y longitud
                if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION);
                {}

                locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                if (locationManager != null)
                {
                    mLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    //mLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        geocoder = new Geocoder(mContext, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(mLocation.getLatitude(), mLocation.getLongitude(), 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return addresses.get(0).getPostalCode();
    }


    /**
     * Método que lanza un alert para activar el GPS del dispositivo
     */
    public void launchAlertConfig()
    {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
        alertDialog.setTitle(R.string.error_title);
        alertDialog.setMessage(R.string.error_text_active_gps);

        alertDialog.setPositiveButton(R.string.text_si, (dialog, which) -> {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            mContext.startActivity(intent);
        });

        alertDialog.setNegativeButton(R.string.text_no, (dialog, which) -> dialog.cancel());

        alertDialog.show();
    }


    /**
     * @param intent i
     * @return null
     */
    @Nullable
    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    /**
     * Escuchador cuando cambia la localización
     * @param location localización
     */
    @Override
    public void onLocationChanged(@NonNull Location location) { }
}