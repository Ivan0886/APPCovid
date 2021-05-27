package com.example.appcovid.views;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.example.appcovid.R;
import com.example.appcovid.controller.RestrictionsAdapter;
import com.example.appcovid.controller.RestrictionsViewModel;
import com.example.appcovid.model.BaseActivity;
import com.example.appcovid.model.GPSLocation;
import com.example.appcovid.model.RestrictionsItems;

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class RestrictionsActivity extends BaseActivity {

    private ArrayList mPermissionsToRequest;
    private ArrayList mPermissionsRejected = new ArrayList();
    private ArrayList mPermissions = new ArrayList();
    private RestrictionsViewModel mDataRestrictions;
    private RestrictionsAdapter mAdapter;

    private final static int ALL_PERMISSIONS_RESULT = 101;
    private GPSLocation mGpsLocation;


    @Override
    public void onCreate(Bundle savedInstanceState) {
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (mPermissionsToRequest.size() > 0) {
                requestPermissions((String[]) mPermissionsToRequest.toArray(new String[mPermissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
            }
        }

        mGpsLocation = new GPSLocation(this);
        // Log.d("HOLAAAAAAAA", String.valueOf(mGpsLocation.getmLatitude()));

        if (mGpsLocation.canGetLocation()) {

            // Se construye el ViewModel
            mDataRestrictions = new ViewModelProvider(this).get(RestrictionsViewModel.class);

            // Se comprueba si los datos han cambiado
            mDataRestrictions.getmData(mGpsLocation).observe(this, new Observer<List<RestrictionsItems>>() {
                @Override
                public void onChanged(List<RestrictionsItems> restrictionsItems) {
                    // Si la llamada ha ido bien
                    if(restrictionsItems != null) {
                        mAdapter.addData(new ArrayList(restrictionsItems));
                    } else {
                        launchError("Ha surgido un problema llamando a la API");
                    }
                }
            });
        } else {
            mGpsLocation.launchAlertConfig();
        }
    }


    private void launchError(String messageError) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.error_title));
        builder.setMessage(messageError);
        builder.setPositiveButton(R.string.text_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });
        builder.create().show();
    }


    private ArrayList findAnswerPermissions(ArrayList wanted) {
        ArrayList result = new ArrayList();

        for (Object permission : wanted) {
            if (!youHavePermission((String)permission)) {
                result.add(permission);
            }
        }
        return result;
    }


    private boolean youHavePermission(String permission) {
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
            for (Object permission : mPermissionsToRequest) {
                if (!youHavePermission((String)permission)) {
                    mPermissionsRejected.add(permission);
                }
            }

        if (mPermissionsRejected.size() > 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (shouldShowRequestPermissionRationale((String) mPermissionsRejected.get(0))) {
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


    public void OnClickBack(View v) {
       finish();
    }
}