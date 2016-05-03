package me.ziyuanliu.myruns;

import android.Manifest;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
//import android.location.LocationManager;
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
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import me.ziyuanliu.myruns.database.ExerciseEntry;
import me.ziyuanliu.myruns.database.ExerciseEntryDatasource;

public class MapExerciseActivity extends FragmentActivity implements OnMapReadyCallback, ServiceConnection {

    private GoogleMap mMap;
    public Marker whereAmI;
    public Marker firstMarker;
//    LocationManager locationManager;
    PolylineOptions rectOptions;
    Polyline polyline;
    LatLng lastLocation;

    private int startTime;
    private float currentCalorie;
    private Calendar startCal;

    SharedPreferences pref;

    private Messenger mServiceMessenger = null;
    boolean mIsBound;

    private static final String TAG = "CS65";

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

        String svcName= Context.LOCATION_SERVICE;
//        locationManager = (LocationManager)getSystemService(svcName);

        mIsBound = false; // by default set this to unbound

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION);
        }else{
            initializeMap();
        }

    }


    protected void initializeMap(){
//        Criteria criteria = new Criteria();
//        criteria.setAccuracy(Criteria.NO_REQUIREMENT);
//        criteria.setPowerRequirement(Criteria.POWER_LOW);
//        criteria.setAltitudeRequired(true);
//        criteria.setSpeedRequired(true);
//        criteria.setBearingRequired(false);
//        criteria.setSpeedRequired(true);
//        criteria.setCostAllowed(true);
//        String provider = locationManager.getBestProvider(criteria, true);

//        Location l = locationManager.getLastKnownLocation(provider);
//
//        LatLng latlng=fromLocationToLatLng(l);
//
//
//        whereAmI=mMap.addMarker(new MarkerOptions().position(latlng).icon(BitmapDescriptorFactory.defaultMarker(
//                BitmapDescriptorFactory.HUE_GREEN)));

        // Zoom in

        if (!LocationService.isRunning()){
            startService(new Intent(MapExerciseActivity.this, LocationService.class));
        }
        doBindService();




//        if (locationManager != null) {
//            if (checkCallingOrSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
//                    || checkCallingOrSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//                locationManager.requestLocationUpdates(provider, 2000, 10,
//                        locationListener);
//            }
//        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    public static LatLng fromLocationToLatLng(Location location){
        return new LatLng(location.getLatitude(), location.getLongitude());
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


            if(whereAmI!=null)
                whereAmI.remove();


            if (firstMarker == null){
                firstMarker = mMap.addMarker(new MarkerOptions().position(latlng).icon(BitmapDescriptorFactory.defaultMarker(
                        BitmapDescriptorFactory.HUE_RED)).title("Starting point"));
                rectOptions = new PolylineOptions().add(firstMarker.getPosition());

                startCal = Calendar.getInstance();
                startTime = startCal.get(Calendar.SECOND);
            }else{
                whereAmI = mMap.addMarker(new MarkerOptions().position(latlng).icon(BitmapDescriptorFactory.defaultMarker(
                        BitmapDescriptorFactory.HUE_GREEN)).title("Current Location."));
                rectOptions.add(whereAmI.getPosition());

            }

            rectOptions.color(Color.RED);
            polyline = mMap.addPolyline(rectOptions);

            myLocationText.setText(LocationService.currentString);

            lastLocation = latlng;
        }
    }


    /*
    returns the distance in meters :)
     */
//    private float getDistance(){
//        float sum = 0;
//        LatLng lastP = null;
//        for (LatLng curr: rectOptions.getPoints()){
//            if (lastP == null){
//                lastP = curr;
//                continue;
//            }
//
//            float[] results = new float[1];
//            Location.distanceBetween(lastP.latitude, lastP.longitude, curr.latitude, curr.longitude, results);
//
//            sum += results[0];
//            lastP = curr;
//        }
//
//        return sum;
//    }
//
//    private String getDistanceStr(){
//        // 0 is metric system
//        int itemChoice = pref.getInt(SettingsActivity.PREF_KEYS_USER_UNIT_TYPE,0);
//        float meterDistance = getDistance();
//        if (itemChoice == 0){
//            return meterDistance/1000.0+ " Kilometers";
//        }else{
//            return meterDistance*0.000621371+ " Miles";
//        }
//    }
//
//    private String getClimbStr(double c){
//        double climb = 0;
//        int itemChoice = pref.getInt(SettingsActivity.PREF_KEYS_USER_UNIT_TYPE,0);
//        if (itemChoice == 0){
//            climb = c/1000.0;
//        }else{
//            climb = c*0.000621371;
//        }
//        return climb+" "+ (itemChoice == 0? "kilometers" : "miles");
//    }
//
//    private String getAvgSpeedStr(){
//        int itemChoice = pref.getInt(SettingsActivity.PREF_KEYS_USER_UNIT_TYPE,0);
//        float avgSpeed;
//        float distMeter = getDistance();
//        if (itemChoice == 0){
//            avgSpeed = distMeter/1000.0f;
//        }else{
//            avgSpeed = distMeter*0.000621371f;
//        }
//
//        float timeElapsedSeconds = Calendar.getInstance().get(Calendar.SECOND) - startTime;
//        float hours = timeElapsedSeconds/60.0f/60.0f;
//        return avgSpeed/hours+" "+ (itemChoice == 0? "km/h" : "m/h");
//    }
//
//    private String getCurrentSpeedStr(Location location){
//        float currSpeed = 0;
//        if (lastLocation != null){
//            currSpeed = lastLocation.distanceTo(location);
//        }
//
//        int itemChoice = pref.getInt(SettingsActivity.PREF_KEYS_USER_UNIT_TYPE,0);
//
//        if (itemChoice != 0){
//            currSpeed = currSpeed*0.621371f;
//        }
//        return currSpeed+" "+ (itemChoice == 0? "km/h" : "m/h");
//    }

//    private final LocationListener locationListener = new LocationListener() {
//        public void onLocationChanged(Location location) {
//            LatLng l = fromLocationToLatLng(location);
//            updateWithNewLocation(l);
//        }
//        public void onProviderDisabled(String provider) {}
//        public void onProviderEnabled(String provider) {}
//        public void onStatusChanged(String provider, int status,
//                                    Bundle extras) {}
//    };

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
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        ExerciseEntry e = LocationService.getExerciseEntry();
        if (e != null){
            ArrayList<LatLng> locList = e.getmLocationList();
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

        // Add a marker in Sydney and move the camera
//        LatLng currentLoc;
//        if (lastLocation != null){
//            currentLoc = lastLocation;
//        }else{
//            currentLoc = new LatLng(-34, 151);
//
//        }
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLoc));
    }

    public void onCancelClicked(View view){
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
        doUnbindService();
        finish();
    }

    /**
     * Bind this Activity to TimerService
     */
    private void doBindService() {
        Log.d(TAG, "C:doBindService()");
        bindService(new Intent(this, LocationService.class), mConnection,
                Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    /**
     * Un-bind this Activity to TimerService
     */
    private void doUnbindService() {
        Log.d(TAG, "C:doUnBindService()");
        if (mIsBound) {
            // If we have received the service, and hence registered with it,
            // then now is the time to unregister.
            if (mServiceMessenger != null) {
                try {
                    Message msg = Message.obtain(null,
                            LocationService.MSG_UNREGISTER_CLIENT);
                    msg.replyTo = mMessenger;
                    mServiceMessenger.send(msg);
                } catch (RemoteException e) {
                    // There is nothing special we need to do if the service has
                    // crashed.
                }
            }
            // Detach our existing connection.
            unbindService(mConnection);
            mIsBound = false;
        }
    }

    // This is called when the connection with the service has been
    // established, giving us the service object we can use to
    // interact with the service.

    // bindService(new Intent(this, MyService.class), mConnection,
    // Context.BIND_AUTO_CREATE) calls onbind in the service which
    // returns an IBinder to the client.

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        Log.d(TAG, "C:onServiceConnected()");
        mServiceMessenger = new Messenger(service);
        try {
            Message msg = Message.obtain(null, LocationService.MSG_REGISTER_CLIENT);
            msg.replyTo = mMessenger;
            Log.d(TAG, "C: TX MSG_REGISTER_CLIENT");
            mServiceMessenger.send(msg);
        } catch (RemoteException e) {
            // In this case the service has crashed before we could even do
            // anything with it
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        Log.d(TAG, "C:onServiceDisconnected()");
        // This is called when the connection with the service has been
        // unexpectedly disconnected - process crashed.
        mServiceMessenger = null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "C:onDestroy()");
        try {
            doUnbindService();
        } catch (Throwable t) {
            Log.e(TAG, "Failed to unbind from the service", t);
        }
    }

    /**
     * Handle incoming messages from TimerService
     */
    private class IncomingMessageHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            Log.d(TAG, "C:IncomingHandler:handleMessage");
            switch (msg.what) {
                case LocationService.MSG_SET_INT_VALUE:
                    updateMap();
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    public void updateMap(){
        ExerciseEntry entry = LocationService.getExerciseEntry();
        ArrayList<LatLng> list = entry.getmLocationList();
        LatLng pointToAdd = list.get(list.size()-1);

        updateWithNewLocation(pointToAdd);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pointToAdd,
                17));
    }
}
