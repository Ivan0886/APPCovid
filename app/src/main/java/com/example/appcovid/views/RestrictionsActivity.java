package com.example.appcovid.views;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.appcovid.R;
import com.example.appcovid.model.GPSLocation;

import java.util.ArrayList;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class RestrictionsActivity extends AppCompatActivity {

    private ArrayList mPermisosParaSolicitar;
    private ArrayList mPermisosRechazados = new ArrayList();
    private ArrayList mPermisos = new ArrayList();

    private final static int ALL_PERMISSIONS_RESULT = 101;
    private GPSLocation mGpsLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restrictions);

        mPermisos.add(ACCESS_FINE_LOCATION);
        mPermisos.add(ACCESS_COARSE_LOCATION);

        mPermisosParaSolicitar = encontrarRespuestaPermisos(mPermisos);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (mPermisosParaSolicitar.size() > 0) {
                requestPermissions((String[]) mPermisosParaSolicitar.toArray(new String[mPermisosParaSolicitar.size()]), ALL_PERMISSIONS_RESULT);
            }
        }

        mGpsLocation = new GPSLocation(this);

        if (mGpsLocation.canGetLocation()) {
            Log.d("HOLA", String.valueOf(mGpsLocation.getmLongitude()));
            Log.d("HOLA2", String.valueOf(mGpsLocation.getmLatitude()));
        } else {
            mGpsLocation.lanzarAlertConfiguracion();
        }
    }


    private ArrayList encontrarRespuestaPermisos(ArrayList wanted) {
        ArrayList result = new ArrayList();

        for (Object permiso : wanted) {
            if (!tienePermiso((String)permiso)) {
                result.add(permiso);
            }
        }
        return result;
    }


    private boolean tienePermiso(String permission) {
        if (canMakeSmores()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }


    private boolean canMakeSmores() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }


    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permisos, int[] grantResults) {
        if(requestCode == ALL_PERMISSIONS_RESULT)
            for (Object permiso : mPermisosParaSolicitar) {
                if (!tienePermiso((String)permiso)) {
                    mPermisosRechazados.add(permiso);
                }
            }

        if (mPermisosRechazados.size() > 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (shouldShowRequestPermissionRationale((String) mPermisosRechazados.get(0))) {
                    finish();
                }
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mGpsLocation.stopListener();
    }


    public void OnClickVolver(View v) {
       finish();
    }
}