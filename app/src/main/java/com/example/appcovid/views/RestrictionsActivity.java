// TODO Mirar por que no va
package com.example.appcovid.views;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

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
 * @author: Iustin Mocanu
 * @version: 28/05/2021/A
 * @see BaseActivity
 * @see RestrictionsViewModel
 */
public class RestrictionsActivity extends BaseActivity {
    private final static int ALL_PERMISSIONS_RESULT = 101;
    private ArrayList mPermissionsRejected = new ArrayList();
    private ArrayList mPermissions = new ArrayList();
    private ArrayList mPermissionsToRequest;
    private RestrictionsViewModel mDataRestrictions;
    private RestrictionsAdapter mAdapter;
    public LocationManager locationManager;
    public LocationListener locationListener = new MyLocationListener();
    private static final String URL_RES = "https://api.quecovid.es/restriction/";
    private boolean gps_enable = false;
    private boolean network_enable = false;
    private ListView mListView;
    List<Address> addresses;

    /**
     * Método que se ejecuta al arrancar la actividad. Se construye el RecylerView y se consultan los permisos
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restrictions);

        // get a reference to the already created main layout
        mListView = findViewById(R.id.list_restrictions);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        getMyLocation();

        checkLocationPermission();
    }


    class MyLocationListener implements LocationListener {
        Geocoder geocoder;

        @Override
        public void onLocationChanged(@NonNull Location location) {
            if (location != null) {
                locationManager.removeUpdates(locationListener);

                geocoder = new Geocoder(RestrictionsActivity.this, Locale.getDefault());

                try {
                    addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                loadData(addresses.get(0).getPostalCode());
                //Log.d("ADDRESS", "" + addresses);
            }
        }
    }

    public void getMyLocation() {
        try {
            gps_enable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {

        }

        try {
            network_enable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {

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
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        }

        if (network_enable) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        }
    }

    private boolean checkLocationPermission() {
        int location = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int location2 = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);

        List<String> listPermission = new ArrayList<>();

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
    }

    private void loadData(String postalCode) {
        // Se construye el retrofit
        Retrofit retrofit = new Retrofit
                .Builder()
                .baseUrl(URL_RES)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RestrictionsService restrictionsService = retrofit.create(RestrictionsService.class);

        //Log.d("POSTALCODE2", gpsLocation.getmPostalCode());

        // Se construye la llamada
        Call<List<RestrictionFeed>> callAsync = restrictionsService.getRestrictions(postalCode, "lR2I41RV8NhDuEkS51V8Z9NLJ");

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