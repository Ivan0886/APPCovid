// TODO Mirar por que no va
package com.example.appcovid.views;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.example.appcovid.R;
import com.example.appcovid.controller.RestrictionsAdapter;
import com.example.appcovid.controller.RestrictionsService;
import com.example.appcovid.controller.RestrictionsViewModel;
import com.example.appcovid.model.BaseActivity;
import com.example.appcovid.model.RestrictionFeed;
import com.example.appcovid.model.RestrictionsItems;

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
    private static final String URL_RES = "https://api.quecovid.es/restriction/";
    private static final int ALL_PERMISSIONS_RESULT = 101;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60;
    private final List<Object> mPermissionsRejected = new ArrayList<>();
    private final List<Object> mPermissions = new ArrayList<>();
    private List<Object> mPermissionsToRequest;
    private RestrictionsAdapter mAdapter;
    private ListView mListView;
    private boolean gps_enable = false;
    private boolean network_enable = false;
    public LocationListener locationListener = new MyLocationListener();
    public LocationManager locationManager;
    List<Address> addresses;

    /**
     * Método que se ejecuta al arrancar la actividad. Se construye el RecylerView y se consultan los permisos
     * @param savedInstanceState instancia de la actividad
     */
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

        getMyLocation();

        /*if(addresses != null) {
            loadData();
        } else {

        }*/
    }


    class MyLocationListener implements LocationListener {
        Geocoder geocoder;

        @Override
        public void onLocationChanged(@NonNull Location location) {
            //locationManager.removeUpdates(locationListener);
            geocoder = new Geocoder(RestrictionsActivity.this, Locale.getDefault());

            try {
                addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d("HOLA89", "pos eso");
            loadData();
            //Log.d("ADDRESS", "" + addresses);
        }
    }

    public void getMyLocation() {
        try {
            gps_enable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ignored) {

        }

        try {
            network_enable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ignored) {

        }

        if (!gps_enable && !network_enable) {
            launchAlert(R.string.error_title, R.string.error_text_service);
        }

        if (gps_enable) {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            //locationManager.getLastKnownLocation(String.valueOf(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)));
            if(addresses == null) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListener);
            } else {
                loadData();
                Log.d("HOLA88", "pos eso");
                //locationManager.getLastKnownLocation(String.valueOf(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)));
            }

        }

        if (network_enable) {
            //locationManager.getLastKnownLocation(String.valueOf(locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)));
            if(addresses == null) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListener);
            } else {
                loadData();
                Log.d("HOLA88", "pos eso");
                //locationManager.getLastKnownLocation(String.valueOf(locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)));
            }
        }
    }

    /*private boolean checkLocationPermission() {
        int location = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int location2 = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);

        listPermission = new ArrayList<>();

        if (location != PackageManager.PERMISSION_GRANTED) {
            listPermission.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        if (location2 != PackageManager.PERMISSION_GRANTED) {
            listPermission.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }

        if (!listPermission.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermission.toArray(new String[listPermission.size()]), 1);
        }

        return true;
    }*/

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
     * @param permisos array de permisos
     * @param grantResults permisos aceptados
     */
    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permisos, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permisos, grantResults);
        if (requestCode == ALL_PERMISSIONS_RESULT) {
            for (Object permission : mPermissionsToRequest) {
                if (!youHavePermission((String) permission)) {
                    mPermissionsRejected.add(permission);
                }
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

    private void loadData() {
        // Se construye el retrofit
        Retrofit retrofit = new Retrofit
                .Builder()
                .baseUrl(URL_RES)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RestrictionsService restrictionsService = retrofit.create(RestrictionsService.class);

        //Log.d("POSTALCODE2", gpsLocation.getmPostalCode());

        // Se construye la llamada
        Call<List<RestrictionFeed>> callAsync = restrictionsService.getRestrictions(addresses.get(0).getPostalCode(), "lR2I41RV8NhDuEkS51V8Z9NLJ");

        // Se hace la llamada a la API
        callAsync.enqueue(new Callback<List<RestrictionFeed>>() {
            @Override
            public void onResponse(@NonNull Call<List<RestrictionFeed>> call, @NonNull Response<List<RestrictionFeed>> response) {
                if (response.isSuccessful()) {
                    // La API responde correctamente
                    assert response.body() != null;

                    List<RestrictionsItems> listRestrictions = response.body().get(0).getItems();
                    listRestrictions.addAll(response.body().get(1).getItems());

                    mAdapter = new RestrictionsAdapter(RestrictionsActivity.this, R.layout.element_restrictions, listRestrictions);

                    mListView.setAdapter(mAdapter);

                    Log.d("TITULO", listRestrictions.get(0).getmTitle());
                    Log.d("TEXTO", listRestrictions.get(0).getmDescription());
                    /*for(RestrictionsItems item : list) {


                        Log.d("TITULO", item.getmTitle());
                        Log.d("TEXTO", item.getmDescription());

                        // inflate (create) another copy of our custom layout
                        //LayoutInflater inflater = getLayoutInflater();
                        //View myLayout = inflater.inflate(R.layout.element_restrictions, ll, false);

                        //LinearLayout ll2 = myLayout.findViewById(R.id.restriction_layout);

                        //TextView textView = new TextView(RestrictionsActivity.this);

                        //TextView textView = ll2.findViewById(R.id.restriction_title);
                        //TextView textView2 = ll2.findViewById(R.id.restriction_text);
                        //textView.setText(item.getmTitle());
                        //textView2.setText(item.getmDescription());
                        //ll.addView(myLayout);
                    }*/
                    //mData.postValue(list);
                } else {
                    //mData.postValue(null);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<RestrictionFeed>> call, @NonNull Throwable t) {
                //mData.postValue(null);
            }
        });
    }


}