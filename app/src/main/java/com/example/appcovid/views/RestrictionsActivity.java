// TODO Mirar por que no va
package com.example.appcovid.views;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import com.example.appcovid.R;
import com.example.appcovid.controller.RestrictionsAdapter;
import com.example.appcovid.controller.RestrictionsViewModel;
import com.example.appcovid.model.BaseActivity;
import com.example.appcovid.model.GPSLocation;

import java.util.ArrayList;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

/**
 * Clase que contiene RestrictionsViewModel
 * @author Iván Moriche Damas
 * @author Rodrigo Garcia
 * @author Iustin Mocanu
 * @version 28/05/2021/A
 * @see BaseActivity
 * @see RestrictionsViewModel
 */
public class RestrictionsActivity extends BaseActivity
{
    private final static int ALL_PERMISSIONS_RESULT = 101;
    private ArrayList mPermissionsRejected = new ArrayList<>();
    private ArrayList mPermissions = new ArrayList<>();
    private ArrayList mPermissionsToRequest;
    private RestrictionsAdapter mAdapter;
    private GPSLocation mGpsLocation;

    /**
     * Método que se ejecuta al arrancar la actividad. Se construye el RecylerView y se consultan los permisos
     * @param savedInstanceState instancia de la actividad
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restrictions);

        mPermissions.add(ACCESS_FINE_LOCATION);
        mPermissions.add(ACCESS_COARSE_LOCATION);

        mPermissionsToRequest = findAnswerPermissions(mPermissions);

        // Se construye el RecyclerView
        RecyclerView recyclerView = findViewById(R.id.list_restrictions);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Se construye el adaptador y se añade al RecyclerView
        mAdapter = new RestrictionsAdapter(this);
        recyclerView.setAdapter(mAdapter);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if (mPermissionsToRequest.size() > 0)
            {
                requestPermissions((String[]) mPermissionsToRequest.toArray(
                        new String[mPermissionsToRequest.size()]),
                        ALL_PERMISSIONS_RESULT);
            }
        }

        mGpsLocation = new GPSLocation(this);

        if (mGpsLocation.canGetLocation())
        {
            // Se construye el ViewModel
            RestrictionsViewModel mDataRestrictions = new ViewModelProvider(this).get(RestrictionsViewModel.class);

            // Se comprueba si los datos han cambiado
            mDataRestrictions.getmData(mGpsLocation).observe(this, restrictionsItems -> {
                // Si la llamada ha ido bien
                if(restrictionsItems != null)
                {
                    mAdapter.addData(new ArrayList<>(restrictionsItems));
                } else {
                    launchAlert(R.string.error_title, R.string.error_text_service);
                }
            });
        } else {
            mGpsLocation.launchAlertConfig();
        }
    }


    /**
     * Método que consulta los permisos de App
     * @param wanted listado de permisos
     * @return result
     */
    private ArrayList findAnswerPermissions(ArrayList wanted)
    {
        ArrayList result = new ArrayList<>();

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
     * @param permisos array de permisos
     * @param grantResults permisos aceptados
     */
    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permisos, @NonNull int[] grantResults)
    {
        if(requestCode == ALL_PERMISSIONS_RESULT)
        {
            for (Object permission : mPermissionsToRequest)
            {
                if (!youHavePermission((String)permission))
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
     * Método que se ejecuta cuando se para la actividad. Se para el escuchador de mGpsLocation
     * @see GPSLocation
     */
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        mGpsLocation.stopListener();
    }
}