package me.ziyuanliu.myruns;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
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
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import me.ziyuanliu.myruns.database.ExerciseEntry;
import me.ziyuanliu.myruns.database.ExerciseEntryDatasource;

public class MapExerciseActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    public Marker whereAmI;
    public Marker firstMarker;
    LocationManager locationManager;
    PolylineOptions rectOptions;
    Polyline polyline;
    Location lastLocation;

    private int startTime;
    private float currentCalorie;
    private Calendar startCal;

    SharedPreferences pref;

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
        locationManager = (LocationManager)getSystemService(svcName);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION);
        }else{
            initializeMap();
        }
    }


    protected void initializeMap(){
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.NO_REQUIREMENT);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        criteria.setAltitudeRequired(true);
        criteria.setSpeedRequired(true);
        criteria.setBearingRequired(false);
        criteria.setSpeedRequired(true);
        criteria.setCostAllowed(true);
        String provider = locationManager.getBestProvider(criteria, true);

        Location l = locationManager.getLastKnownLocation(provider);

        LatLng latlng=fromLocationToLatLng(l);


        whereAmI=mMap.addMarker(new MarkerOptions().position(latlng).icon(BitmapDescriptorFactory.defaultMarker(
                BitmapDescriptorFactory.HUE_GREEN)));

        // Zoom in
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng,
                17));

        updateWithNewLocation(l);

        if (locationManager != null) {
            if (checkCallingOrSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    || checkCallingOrSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(provider, 2000, 10,
                        locationListener);
            }
        }
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

    private void updateWithNewLocation(Location location) {
        TextView myLocationText;
        myLocationText = (TextView)findViewById(R.id.text_stats);


        if (location != null) {
            // Update the map location.

            LatLng latlng=fromLocationToLatLng(location);

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


            String avgSpeedStr = getAvgSpeedStr();
            String currSpeedStr = getCurrentSpeedStr(location);
            String climbStr = getClimbStr(location.getAltitude());

            String formatStr = "Type: Running\nAvg speed: %s\nCur speed: %s\nClimb: %s\nCalorie: %f\nDistance: %s";

            currentCalorie = (getDistance() / 15.0f);

            String value = String.format(formatStr, avgSpeedStr, currSpeedStr, climbStr, currentCalorie, getDistanceStr());

            myLocationText.setText(value);

            lastLocation = location;
        }
    }


    /*
    returns the distance in meters :)
     */
    private float getDistance(){
        float sum = 0;
        LatLng lastP = null;
        for (LatLng curr: rectOptions.getPoints()){
            if (lastP == null){
                lastP = curr;
                continue;
            }

            float[] results = new float[1];
            Location.distanceBetween(lastP.latitude, lastP.longitude, curr.latitude, curr.longitude, results);

            sum += results[0];
            lastP = curr;
        }

        return sum;
    }

    private String getDistanceStr(){
        // 0 is metric system
        int itemChoice = pref.getInt(SettingsActivity.PREF_KEYS_USER_UNIT_TYPE,0);
        float meterDistance = getDistance();
        if (itemChoice == 0){
            return meterDistance/1000.0+ " Kilometers";
        }else{
            return meterDistance*0.000621371+ " Miles";
        }
    }

    private String getClimbStr(double c){
        double climb = 0;
        int itemChoice = pref.getInt(SettingsActivity.PREF_KEYS_USER_UNIT_TYPE,0);
        if (itemChoice == 0){
            climb = c/1000.0;
        }else{
            climb = c*0.000621371;
        }
        return climb+" "+ (itemChoice == 0? "kilometers" : "miles");
    }

    private String getAvgSpeedStr(){
        int itemChoice = pref.getInt(SettingsActivity.PREF_KEYS_USER_UNIT_TYPE,0);
        float avgSpeed;
        float distMeter = getDistance();
        if (itemChoice == 0){
            avgSpeed = distMeter/1000.0f;
        }else{
            avgSpeed = distMeter*0.000621371f;
        }

        float timeElapsedSeconds = Calendar.getInstance().get(Calendar.SECOND) - startTime;
        float hours = timeElapsedSeconds/60.0f/60.0f;
        return avgSpeed/hours+" "+ (itemChoice == 0? "km/h" : "m/h");
    }

    private String getCurrentSpeedStr(Location location){
        float currSpeed = 0;
        if (lastLocation != null){
            currSpeed = lastLocation.distanceTo(location);
        }

        int itemChoice = pref.getInt(SettingsActivity.PREF_KEYS_USER_UNIT_TYPE,0);

        if (itemChoice != 0){
            currSpeed = currSpeed*0.621371f;
        }
        return currSpeed+" "+ (itemChoice == 0? "km/h" : "m/h");
    }

    private final LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            updateWithNewLocation(location);
        }
        public void onProviderDisabled(String provider) {}
        public void onProviderEnabled(String provider) {}
        public void onStatusChanged(String provider, int status,
                                    Bundle extras) {}
    };

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

        // Add a marker in Sydney and move the camera
        LatLng currentLoc;
        if (lastLocation != null){
            currentLoc = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
        }else{
            currentLoc = new LatLng(-34, 151);

        }
        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLoc));
    }

    public void onCancelClicked(View view){
        finish();
    }

    public void onSaveClicked(View view) throws IOException{
        // first grab the input and activity types and store them
        int inputType = pref.getInt(SettingsActivity.PREF_KEYS_USER_INPUT_TYPE, -1);
        int activityType = pref.getInt(SettingsActivity.PREF_KEYS_USER_ACTIVITY_TYPE, -1);

        ExerciseEntry entry = new ExerciseEntry(getApplicationContext());
        entry.setmDateTime(this.startCal);
        entry.setmCalorie((int)this.currentCalorie);
        entry.setmInputType(inputType);
        entry.setmActivityType(activityType);

        double dist = getDistance();
        entry.setmDistance(dist);

        int timeElapse = Calendar.getInstance().get(Calendar.SECOND) - startTime;
        entry.setmDuration(timeElapse);
        entry.setmLocationList((ArrayList<LatLng>) rectOptions.getPoints());

        ExerciseEntryDatasource datasource = new ExerciseEntryDatasource(this);
        datasource.open();
        ExerciseEntry e = datasource.createExerciseEntry(entry);
        datasource.close();
        Toast.makeText(this, "Entry #"+e.getId()+" created", Toast.LENGTH_LONG).show();
        finish();
    }
}
