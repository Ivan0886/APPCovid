package com.example.appcovid.model;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.example.appcovid.R;
import com.example.appcovid.controller.GPSService;
import com.example.appcovid.controller.RestrictionsService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GPSLocation extends Service implements LocationListener {

    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60;
    private static final String URL_GPS = "https://geocode.xyz/";
    private static final String URL_RES = "https://api.quecovid.es/restriction/";

    private final Context mContext;

    private boolean mComprobarGPS = false;
    private boolean mPuedoObtenerLocalizacion = false;

    private Location mLocalizacion;

    private double mLatitude;
    private double mLongitude;

    protected LocationManager locationManager;


    public GPSLocation(Context mContext) {
        this.mContext = mContext;
        getLocation();
    }


    private Location getLocation() {
        try {
            locationManager = (LocationManager)mContext.getSystemService(LOCATION_SERVICE);

            // Obtiene el estado GPS
            mComprobarGPS = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            if (!mComprobarGPS) {
                Toast.makeText(mContext, R.string.error_text_service, Toast.LENGTH_SHORT).show();
            } else {
                this.mPuedoObtenerLocalizacion = true;

                // Si el GPS está habilitado, obtiene latitude y longitud
                if (mComprobarGPS) {
                    if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    }

                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                    if (locationManager != null) {
                        mLocalizacion = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                        if (mLocalizacion != null) {
                            // Se construye el retrofit
                            Retrofit retrofit = new Retrofit
                                    .Builder()
                                    .baseUrl(URL_GPS)
                                    .addConverterFactory(GsonConverterFactory.create())
                                    .build();
                            GPSService gpsService = retrofit.create(GPSService.class);

                            // Se construye la llamada
                            Call<GPSFeed> callAsync = gpsService.getAddress(mLocalizacion.getLatitude(), mLocalizacion.getLongitude());

                            // Se hace la llamada a la API
                            callAsync.enqueue(new Callback<GPSFeed>() {
                                @Override
                                public void onResponse(Call<GPSFeed> call, Response<GPSFeed> response) {
                                    if (response.isSuccessful()) {
                                        // La API responde correctamente
                                        Retrofit retrofit = new Retrofit
                                                .Builder()
                                                .baseUrl(URL_RES)
                                                .addConverterFactory(GsonConverterFactory.create())
                                                .build();
                                        RestrictionsService resService = retrofit.create(RestrictionsService.class);

                                        // Se construye la llamada
                                        Call<List<RestrictionFeed>> callAsync = resService.getRestrictions(response.body().getmCity(), "lR2I41RV8NhDuEkS51V8Z9NLJ");

                                        // Se hace la llamada a la API
                                        callAsync.enqueue(new Callback<List<RestrictionFeed>>() {
                                            @Override
                                            public void onResponse(Call<List<RestrictionFeed>> call, Response<List<RestrictionFeed>> response) {
                                                Log.d("ESTADO", response.body().get(0).getItems().get(0).getmTitulo());
                                            }

                                            @Override
                                            public void onFailure(Call<List<RestrictionFeed>> call, Throwable t) {

                                            }
                                        });
                                    } else {
                                        Log.d("ESTADO2","ssgsgs");
                                    }
                                }

                                @Override
                                public void onFailure(Call<GPSFeed> call, Throwable t) {
                                    Log.d("ESTADO3","fsddf");
                                }
                            });

                            mLatitude = mLocalizacion.getLatitude();
                            mLongitude = mLocalizacion.getLongitude();
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return mLocalizacion;
    }


    public double getmLongitude() {
        if (mLocalizacion != null) {
            mLongitude = mLocalizacion.getLongitude();
        }
        return mLongitude;
    }


    public double getmLatitude() {
        if (mLocalizacion != null) {
            mLatitude = mLocalizacion.getLatitude();
        }
        return mLatitude;
    }


    public boolean canGetLocation() {
        return this.mPuedoObtenerLocalizacion;
    }


    public void lanzarAlertConfiguracion() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
        alertDialog.setTitle(R.string.error_title);
        alertDialog.setMessage(R.string.error_text_active_gps);

        alertDialog.setPositiveButton(R.string.text_si, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(intent);
            }
        });

        alertDialog.setNegativeButton(R.string.text_no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.show();
    }


    public void stopListener() {
        if (locationManager != null) {

            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locationManager.removeUpdates(GPSLocation.this);
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onLocationChanged(Location location) { }


    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) { }


    @Override
    public void onProviderEnabled(String s) { }


    @Override
    public void onProviderDisabled(String s) { }
}