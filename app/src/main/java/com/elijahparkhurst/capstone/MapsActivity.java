package com.elijahparkhurst.capstone;

import android.content.Context;
import android.content.Intent;
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

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;
    private LatLng DEFAULT_LAT_LNG;
    private static final String TAG = "MapsActivity";
    public Timer mTimer;
    public List<LatLng> locationArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Log.i(TAG, "can You see me");

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

//    public void getLocation(){
//        double Default_Lat = 0;
//        double Default_Lng = 0;
//        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        List<String> providers = lm.getProviders(true);
//        Location l;
//        for (int i = 0; i < providers.size(); i++) {
//            Log.i(TAG, providers.get(i));
//            try {
//                l = lm.getLastKnownLocation(providers.get(i));
//                if (l != null) {
//                    Default_Lat = l.getLatitude();
//                    Default_Lng = l.getLongitude();
//                    break;
//                }
//            } catch (SecurityException e) {
//                e.printStackTrace();
//            }
//        }
//    };

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        LatLng denver = new LatLng(40, -105);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //mMap.addCircle(new CircleOptions().center(DEFAULT_LAT_LNG).radius(12).fillColor(16711681));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(denver, 15.0f));

        try {
            mMap.setMyLocationEnabled(true);
        } catch (SecurityException ex) {
            ex.printStackTrace();
        }

    }
    public void startLogging(View view){
        mTimer = new Timer();
        TimerTask task = new TimerTask() {
            public void run() {
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
                locationArray.add(DEFAULT_LAT_LNG);
            }
        };
        mTimer.scheduleAtFixedRate(task, 500, 10000);

    }

    public void stopLogging(View view){
        mTimer.cancel();
       // Log.i(TAG, )
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
//    mMap.addCircle(new CircleOptions().center(DEFAULT_LAT_LNG).radius(12).fillColor(0xff0000ff));
}
