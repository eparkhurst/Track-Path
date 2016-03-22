package com.elijahparkhurst.capstone;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;
    private LatLng DEFAULT_LAT_LNG;
    private static final String TAG = "MapsActivity";
    public Timer mTimer;
    private ArrayList locationArray = new ArrayList();
    public String title = "";
    private Intent service;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Log.i(TAG, "can You see me");

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MyIntentService.TRANSACTION_DONE);
        registerReceiver(locationReceiver, intentFilter);

    }
    @Override
    protected void onRestart(){
        super.onRestart();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MyIntentService.TRANSACTION_DONE);
        registerReceiver(locationReceiver, intentFilter);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        getLocation();
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(DEFAULT_LAT_LNG, 15.0f));

        try {
            mMap.setMyLocationEnabled(true);
        } catch (SecurityException ex) {
            ex.printStackTrace();
        }

    }
    public void startLogging(View view){
        service = new Intent(this, MyIntentService.class);

        startService(service);
     //   service.putExtra("action", "start");

    }

    public void getLocation(){
        double Default_Lat = 0;
        double Default_Lng = 0;
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = lm.getProviders(true);
        Location l;
        for (int i = 0; i < providers.size(); i++) {
            Log.i(TAG, providers.get(i));
            try {
                l = lm.getLastKnownLocation(providers.get(i));
                if (l != null) {
                    Default_Lat = l.getLatitude();
                    Default_Lng = l.getLongitude();
                    break;
                }
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }
        DEFAULT_LAT_LNG = new LatLng(Default_Lat, Default_Lng);
    }

    public void stopLogging(View view){
        //service.putExtra("action", "stop");
        stopService(service);
    }


    private BroadcastReceiver locationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "map activity Reciever HIT");
            ArrayList locationArray = intent.getParcelableArrayListExtra("locationData");
            LatLng[] array = new LatLng[locationArray.size()];
            int len = array.length;
            //Log.i(TAG, "the array lenght is:"+String.valueOf(len));
            locationArray.toArray(array);
            for (int i=0; i < array.length; i++) {
                double lat = array[i].latitude;
                Log.i(TAG, String.valueOf(lat));
                mMap.addCircle(new CircleOptions()
                        .center(array[i])
                        .radius(12)
                        .fillColor(0x7f0000ff)
                        .strokeWidth(0));
            }
        }
    };

    @Override
    protected void onStop()
    {
        unregisterReceiver(locationReceiver);
        super.onStop();
    }




    public void submit(View view){
        Intent intent = new Intent(this, SubmitActivity.class);
        intent.putParcelableArrayListExtra("locationData", locationArray);
        startActivity(intent);
    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

}
