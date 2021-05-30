package com.example.appcovid.views;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.ListView;

import com.example.appcovid.R;
import com.example.appcovid.controller.RestrictionsAdapter;
import com.example.appcovid.controller.RestrictionsService;
import com.example.appcovid.model.BaseActivity;
import com.example.appcovid.model.RestrictionFeed;
import com.example.appcovid.model.RestrictionsItems;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.OnTokenCanceledListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Clase que muestra las restricciones de la zona
 * @author Iván Moriche Damas
 * @author Rodrigo Garcia
 * @author Iustin Mocanu
 * @version 28/05/2021/A
 * @see BaseActivity
 * @see FusedLocationProviderClient
 */
public class RestrictionsActivity extends BaseActivity
{
    private static final String URL_RES = "https://api.quecovid.es/restriction/";
    private static final int ALL_PERMISSIONS_RESULT = 101;
    private final List<Object> mPermissionsRejected = new ArrayList<>();
    private final List<Object> mPermissions = new ArrayList<>();
    private List<Object> mPermissionsToRequest;
    private RestrictionsAdapter mAdapter;
    private ListView mListView;
    public LocationManager locationManager;
    List<Address> addresses;

    /**
     * Método que se ejecuta al arrancar la actividad. Se consultan los permisos
     * y la localización del usuario
     * @param savedInstanceState instancia de la actividad
     */
    @SuppressLint("VisibleForTests")
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restrictions);

        // Referencia del ListView que hay en el layout
        mListView = findViewById(R.id.list_restrictions);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

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

        FusedLocationProviderClient fusedLocationProviderClient = new FusedLocationProviderClient(RestrictionsActivity.this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            return;
        }

        fusedLocationProviderClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, new CancellationToken()
        {
            @Override
            public boolean isCancellationRequested()
            {
                return false;
            }

            @NonNull
            @Override
            public CancellationToken onCanceledRequested(@NonNull OnTokenCanceledListener onTokenCanceledListener)
            {
                return null;
            }}).addOnSuccessListener(location -> {
                Geocoder geocoder = new Geocoder(RestrictionsActivity.this, Locale.getDefault());

                try
                {
                    addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                loadData();
            });
    }


    /**
     * Método que consulta los permisos de App
     * @param wanted listado de permisos
     * @return result
     */
    private ArrayList<Object> findAnswerPermissions(ArrayList wanted)
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


    /**
     * Método que contruye y hace la llamada a la API. También se encarga de mostrar los datos en pantalla
     */
    private void loadData()
    {
        // Se construye el retrofit
        Retrofit retrofit = new Retrofit
                .Builder()
                .baseUrl(URL_RES)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RestrictionsService restrictionsService = retrofit.create(RestrictionsService.class);

        // Se construye la llamada
        Call<List<RestrictionFeed>> callAsync = restrictionsService.getRestrictions(addresses.get(0).getPostalCode(), "Sq8YKs9N9G3d8W7QGcryGMoRc");

        // Se hace la llamada a la API
        callAsync.enqueue(new Callback<List<RestrictionFeed>>()
        {
            @Override
            public void onResponse(@NonNull Call<List<RestrictionFeed>> call, @NonNull Response<List<RestrictionFeed>> response)
            {
                if (response.isSuccessful())
                {
                    // La API responde correctamente
                    assert response.body() != null;
                    List<RestrictionsItems> listRestrictions = new ArrayList<>();

                    for (RestrictionFeed restrictions : response.body())
                    {
                        listRestrictions.addAll(restrictions.getItems());
                    }

                    mAdapter = new RestrictionsAdapter(RestrictionsActivity.this, R.layout.element_restrictions, listRestrictions);

                    mListView.setAdapter(mAdapter);
                } else {
                    launchAlert(R.string.error_title, R.string.error_text);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<RestrictionFeed>> call, @NonNull Throwable t)
            {
                launchAlert(R.string.error_title, R.string.error_text);
            }
        });
    }
}