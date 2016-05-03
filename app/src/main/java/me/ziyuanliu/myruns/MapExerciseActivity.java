package me.ziyuanliu.myruns;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import me.ziyuanliu.myruns.database.ExerciseEntry;
import me.ziyuanliu.myruns.database.ExerciseEntryDatasource;

public class MapExerciseActivity extends FragmentActivity implements OnMapReadyCallback, ServiceConnection {

    private GoogleMap mMap;
    public Marker currentMarker;
    public Marker firstMarker;
    PolylineOptions rectOptions;
    Polyline polyline;
    LatLng lastLocation;

    SharedPreferences pref;

    // variables to take care of the binding
    private Messenger mServiceMessenger = null;
    boolean mIsBound;

    private final Messenger mMessenger = new Messenger(
            new IncomingMessageHandler());

    private ServiceConnection mConnection = this;

    public final int LOCATION_PERMISSION = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_exercise);

        pref = getSharedPreferences(SettingsActivity.PREF_KEYS_USER_DETAIL, MODE_PRIVATE);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        setUpMapIfNeeded();

        mIsBound = false; // by default set this to unbound

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION);
        }else{
            initializeMap();
        }

    }

    protected void initializeMap(){
        if (!LocationService.isRunning()){
            startService(new Intent(MapExerciseActivity.this, LocationService.class));
        }

        doBindService();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] result){
        super.onRequestPermissionsResult(requestCode, permissions, result);

        if(requestCode == LOCATION_PERMISSION && result[0] == PackageManager.PERMISSION_GRANTED){
            //do things as usual init map or something else when location permission is granted
            initializeMap();
        }
    }


    private void updateWithNewLocation(LatLng latlng) {
        TextView myLocationText;
        myLocationText = (TextView)findViewById(R.id.text_stats);


        if (latlng != null) {
            // Update the map location.

            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng,
                    17));


            if(currentMarker !=null)
                currentMarker.remove();


            if (firstMarker == null){
                firstMarker = mMap.addMarker(new MarkerOptions().position(latlng).icon(BitmapDescriptorFactory.defaultMarker(
                        BitmapDescriptorFactory.HUE_RED)).title("Starting point"));
                rectOptions = new PolylineOptions().add(firstMarker.getPosition());

            }else{
                currentMarker = mMap.addMarker(new MarkerOptions().position(latlng).icon(BitmapDescriptorFactory.defaultMarker(
                        BitmapDescriptorFactory.HUE_GREEN)).title("Current Location."));
                rectOptions.add(currentMarker.getPosition());

            }

            rectOptions.color(Color.RED);
            polyline = mMap.addPolyline(rectOptions);

            myLocationText.setText(LocationService.currentString);

            lastLocation = latlng;
        }
    }



    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            }
        }
    }

    /**
     * We start by plotting all of our locations on the Map
     * Location service will be our source of truce
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        ExerciseEntry e = LocationService.getExerciseEntry();
        if (e != null){
            ArrayList<LatLng> locList = e.getmLocationList();
            Log.d("cs65", "update map"+locList.toString());

            LatLng lastLoc = null;
            for (LatLng l: locList){
                updateWithNewLocation(l);
                lastLoc = l;
            }

            if (lastLoc != null){
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lastLoc,
                        17));
            }
        }
    }

    public void onCancelClicked(View view){
        doResetService();
        doUnbindService();
        finish();
    }

    public void onSaveClicked(View view) throws IOException{
        // first grab the input and activity types and store them
        int inputType = pref.getInt(SettingsActivity.PREF_KEYS_USER_INPUT_TYPE, -1);
        int activityType = pref.getInt(SettingsActivity.PREF_KEYS_USER_ACTIVITY_TYPE, -1);

        ExerciseEntry entry = LocationService.getExerciseEntry();
        entry.setmDateTime(LocationService.startCal);
        entry.setmCalorie((int)LocationService.currentCalorie);
        entry.setmInputType(inputType);
        entry.setmActivityType(activityType);
        entry.setmDistance(LocationService.distance);

        int timeElapse = Calendar.getInstance().get(Calendar.SECOND) - LocationService.startTime;
        entry.setmDuration(timeElapse);

        ExerciseEntryDatasource datasource = new ExerciseEntryDatasource(this);
        datasource.open();
        ExerciseEntry e = datasource.createExerciseEntry(entry);
        datasource.close();
        Toast.makeText(this, "Entry #"+e.getId()+" created", Toast.LENGTH_LONG).show();
        doResetService();
        doUnbindService();
        finish();
    }

    /**
     * Bind this Activity to LocationService
     * Code taken from Binddemo
     */
    private void doBindService() {
        bindService(new Intent(this, LocationService.class), mConnection,
                Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    /**
     * Un-bind this Activity to LocationService
     * Code taken from Binddemo
     */
    private void doUnbindService() {
        if (mIsBound) {

            if (mServiceMessenger != null) {
                try {
                    Message msg = Message.obtain(null,
                            LocationService.MSG_UNREGISTER_CLIENT);
                    msg.replyTo = mMessenger;
                    mServiceMessenger.send(msg);
                } catch (RemoteException e) {
                    // crashed :(
                }
            }

            unbindService(mConnection);
            mIsBound = false;
        }
    }

    /**
     * tell the locationservice to reset itself
     */
    private void doResetService(){
        if (mIsBound) {

            if (mServiceMessenger != null) {
                try {
                    Message msg = Message.obtain(null,
                            LocationService.MSG_FINISH_EXERCISE);
                    msg.replyTo = mMessenger;
                    mServiceMessenger.send(msg);
                } catch (RemoteException e) {
                    // crashed :(
                }
            }

            unbindService(mConnection);
            mIsBound = false;
        }
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        mServiceMessenger = new Messenger(service);
        try {
            Message msg = Message.obtain(null, LocationService.MSG_REGISTER_CLIENT);
            msg.replyTo = mMessenger;
            mServiceMessenger.send(msg);
        } catch (RemoteException e) {
            // crashed :(
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        mServiceMessenger = null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            doUnbindService();
        } catch (Throwable t) {
        }
    }

    /**
     * Handle incoming messages from LocationService
     */
    private class IncomingMessageHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LocationService.MSG_SET_INT_VALUE:
                    updateMap();
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    /**
     * this method will be the API that updates the map from the LocationService
     * We add the last point to the map
     */
    public void updateMap(){
        ExerciseEntry entry = LocationService.getExerciseEntry();
        ArrayList<LatLng> list = entry.getmLocationList();
        LatLng pointToAdd = list.get(list.size()-1);

        updateWithNewLocation(pointToAdd);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pointToAdd,
                17));
    }
}
