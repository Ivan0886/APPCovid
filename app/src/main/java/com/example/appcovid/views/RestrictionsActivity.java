package com.example.appcovid.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.appcovid.R;
import com.example.appcovid.controller.NewsViewModel;
import com.example.appcovid.controller.RestrictionsAdapter;
import com.example.appcovid.controller.RestrictionsViewModel;
import com.example.appcovid.controller.RssAdapter;
import com.example.appcovid.model.GPSLocation;
import com.example.appcovid.model.RestrictionsItems;
import com.example.appcovid.model.RssItem;

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class RestrictionsActivity extends AppCompatActivity {

    private ArrayList mPermisosParaSolicitar;
    private ArrayList mPermisosRechazados = new ArrayList();
    private ArrayList mPermisos = new ArrayList();
    private RestrictionsViewModel mDatosRestrictions;
    private RestrictionsAdapter mAdapter;

    private final static int ALL_PERMISSIONS_RESULT = 101;
    private GPSLocation mGpsLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restrictions);

        mPermisos.add(ACCESS_FINE_LOCATION);
        mPermisos.add(ACCESS_COARSE_LOCATION);

        mPermisosParaSolicitar = encontrarRespuestaPermisos(mPermisos);

        // Se construye el RecyclerView
        RecyclerView recyclerView = findViewById(R.id.list_restrictions);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Se construye el adaptador y se añade al RecyclerView
        mAdapter = new RestrictionsAdapter(this);
        recyclerView.setAdapter(mAdapter);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (mPermisosParaSolicitar.size() > 0) {
                requestPermissions((String[]) mPermisosParaSolicitar.toArray(new String[mPermisosParaSolicitar.size()]), ALL_PERMISSIONS_RESULT);
            }
        }

        mGpsLocation = new GPSLocation(this);

        if (mGpsLocation.canGetLocation()) {

            // Se construye el ViewModel
            mDatosRestrictions = new ViewModelProvider(this).get(RestrictionsViewModel.class);

            // Se comprueba si los datos han cambiado
            mDatosRestrictions.getmDatos(mGpsLocation).observe(this, new Observer<List<RestrictionsItems>>() {
                @Override
                public void onChanged(List<RestrictionsItems> restrictionsItems) {
                    // Si la llamada ha ido bien
                    if(restrictionsItems != null) {
                        mAdapter.addData(new ArrayList(restrictionsItems));
                    } else {
                        lanzarError("Ha surgido un problema llamando a la API");
                    }
                }
            });
        } else {
            mGpsLocation.lanzarAlertConfiguracion();
        }
    }


    private void lanzarError(String mensajeError) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.error_title));
        builder.setMessage(mensajeError);
        builder.setPositiveButton(R.string.text_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });
        builder.create().show();
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