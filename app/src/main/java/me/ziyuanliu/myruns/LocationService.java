package me.ziyuanliu.myruns;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import me.ziyuanliu.myruns.database.ExerciseEntry;

/**
 * Created by ziyuanliu on 5/2/16.
 * help from the demo code
 */
public class LocationService extends Service {
    private NotificationManager mNotificationManager;
    private static boolean isRunning = false;

    LocationManager locationManager;
    SharedPreferences pref;

    public final int LOCATION_PERMISSION = 123;
    public Boolean hasStarted = false;
    private static ExerciseEntry entry;
    public static String currentString;
    public static int startTime;
    public static float currentCalorie;
    public static Calendar startCal;
    public static float distance;

    private List<Messenger> mClients = new ArrayList<Messenger>(); // Keeps
    // track of
    // all
    // current
    // registered
    // clients.
    public static final int MSG_REGISTER_CLIENT = 1;
    public static final int MSG_UNREGISTER_CLIENT = 2;
    public static final int MSG_SET_INT_VALUE = 3;

    // Reference to a Handler, which others can use to send messages to it. This
    // allows for the implementation of message-based communication across
    // processes, by creating a Messenger pointing to a Handler in one process,
    // and handing that Messenger to another process.

    private final Messenger mMessenger = new Messenger(
            new IncomingMessageHandler()); // Target we publish for clients to
    // send messages to IncomingHandler.

    private static final String TAG = "CS65";

    public static ExerciseEntry getExerciseEntry(){
        return entry;
    }

    public void resetVars(){
        hasStarted = false;
        entry = new ExerciseEntry(getBaseContext());
        currentString = "";
        startTime = 0;
        currentCalorie = 0;
        startCal = null;
        distance = 0.f;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "S:onCreate(): Service Started.");

        String svcName= Context.LOCATION_SERVICE;
        locationManager = (LocationManager)getSystemService(svcName);

        initializeLocationManager();
        pref = getSharedPreferences(SettingsActivity.PREF_KEYS_USER_DETAIL, MODE_PRIVATE);

        isRunning = true;
    }

    public void initializeLocationManager(){
        Log.d(TAG, "INITIALIZED");
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
        pref = getSharedPreferences(SettingsActivity.PREF_KEYS_USER_DETAIL, MODE_PRIVATE);
        entry = new ExerciseEntry(getBaseContext());

        updateWithNewLocation(l);

        if (locationManager != null) {
            if (checkCallingOrSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    || checkCallingOrSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(provider, 2000, 10,
                        locationListener);
            }
        }
    }

    private void startRecording(){
        startCal = Calendar.getInstance();
        startTime = startCal.get(Calendar.SECOND);
    }

    private void addLocation(Location location){
        ArrayList<LatLng> list = entry.getmLocationList();
        LatLng latlng=fromLocationToLatLng(location);
        list.add(latlng);
        entry.setmLocationList(list);
    }

    /*
    returns the distance in meters :)
     */
    private float getDistance(){
        float sum = 0;
        LatLng lastP = null;
            for (LatLng curr: entry.getmLocationList()){
                if (lastP == null){
                    lastP = curr;
                    continue;
                }

                float[] results = new float[1];
                Location.distanceBetween(lastP.latitude, lastP.longitude, curr.latitude, curr.longitude, results);

                sum += results[0];
                lastP = curr;
            }

            int itemChoice = pref.getInt(SettingsActivity.PREF_KEYS_USER_UNIT_TYPE,0);
            if (itemChoice == 0){
                return sum/1000.0f;
            }else{
                return sum*0.000621371f;
            }

    }

    private String getDistanceStr(){
        // 0 is metric system
        int itemChoice = pref.getInt(SettingsActivity.PREF_KEYS_USER_UNIT_TYPE,0);
        distance = getDistance();
        if (itemChoice == 0){
            return distance/1000.0+ " Kilometers";
        }else{
            return distance*0.000621371+ " Miles";
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
        if (entry!= null){
            entry.setmDuration(timeElapsedSeconds);
        }
        float hours = timeElapsedSeconds/60.0f/60.0f;
        return avgSpeed/hours+" "+ (itemChoice == 0? "km/h" : "m/h");
    }

    private String getCurrentSpeedStr(Location location){
        float currSpeed = 0;
        ArrayList<LatLng> list = entry.getmLocationList();

        if (list.size()>0){
            LatLng loc = list.get(list.size()-1);
            float[] results = new float[1];
            Location.distanceBetween(loc.latitude, loc.longitude, location.getLatitude(), location.getLongitude(), results);
            currSpeed = results[0];
        }

        int itemChoice = pref.getInt(SettingsActivity.PREF_KEYS_USER_UNIT_TYPE,0);

        if (itemChoice != 0){
            currSpeed = currSpeed*0.621371f;
        }
        return currSpeed+" "+ (itemChoice == 0? "km/h" : "m/h");
    }

    private void updateWithNewLocation(Location location) {
        if (location != null) {
            String avgSpeedStr = getAvgSpeedStr();
            String currSpeedStr = getCurrentSpeedStr(location);
            String climbStr = getClimbStr(location.getAltitude());

            String formatStr = "Type: Running\nAvg speed: %s\nCur speed: %s\nClimb: %s\nCalorie: %f\nDistance: %s";

            currentCalorie = (getDistance() / 15.0f);

            currentString = String.format(formatStr, avgSpeedStr, currSpeedStr, climbStr, currentCalorie, getDistanceStr());

            // Update the map location.
            if (hasStarted == false){
                hasStarted = true;
                startRecording();
            }else{
                addLocation(location);
            }
            sendUpdate();
        }
    }

    public static LatLng fromLocationToLatLng(Location location){
        return new LatLng(location.getLatitude(), location.getLongitude());
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

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "S:onStartCommand(): Received start id " + startId + ": "
                + intent);
        return START_STICKY; // Run until explicitly stopped.
    }

    /**
     * Display a notification in the notification bar.
     */
    private void showNotification() {

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MapExerciseActivity.class), 0);
        Notification notification = new Notification.Builder(this)
                .setContentTitle("MyRuns")
                .setContentText("Recording your exercise")
                .setSmallIcon(R.drawable.icon)
                .setContentIntent(contentIntent).build();
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notification.flags = notification.flags
                | Notification.FLAG_ONGOING_EVENT;
        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        mNotificationManager.notify(0, notification);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "S:onBind() - return mMessenger.getBinder()");

        // getBinder()
        // Return the IBinder that this Messenger is using to communicate with
        // its associated Handler; that is, IncomingMessageHandler().

        return mMessenger.getBinder();
    }

    /**
     * Send the data to all registered clients.
     *
     * Honestly just need it to udpate
     */
    private void sendUpdate() {
        Iterator<Messenger> messengerIterator = mClients.iterator();
        int placeHolder = 1;
        while (messengerIterator.hasNext()) {
            Messenger messenger = messengerIterator.next();
            try {
                // Send data as an Integer
                messenger.send(Message.obtain(null, MSG_SET_INT_VALUE,
                        placeHolder, 0));
            } catch (RemoteException e) {
                // The client is dead. Remove it from the list.
                mClients.remove(messenger);
            }
        }
    }

    public static boolean isRunning() {
        return isRunning;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "S:onDestroy():Service Stopped");
        super.onDestroy();

        mNotificationManager.cancelAll(); // Cancel the persistent notification.
        isRunning = false;
        resetVars();
    }

    /**
     * Handle incoming messages from MainActivity
     */
    private class IncomingMessageHandler extends Handler { // Handler of
        // incoming messages
        // from clients.
        @Override
        public void handleMessage(Message msg) {
            Log.d(TAG, "S:handleMessage: " + msg.what);
            switch (msg.what) {
                case MSG_REGISTER_CLIENT:
                    mClients.add(msg.replyTo);
                    showNotification();
                    break;
                case MSG_UNREGISTER_CLIENT:
                    mClients.remove(msg.replyTo);
                    resetVars();
                    break;
                case MSG_SET_INT_VALUE:
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }
}