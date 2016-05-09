package me.ziyuanliu.myruns;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import me.ziyuanliu.myruns.database.ExerciseEntry;

/**
 * Created by ziyuanliu on 5/2/16.
 * help from the demo code binddemo
 * instead of using the broadcast, I am using the
 */
public class LocationService extends Service implements SensorEventListener{
    private NotificationManager mNotificationManager;
    LocationManager locationManager;
    SharedPreferences pref;

    public static Boolean hasStarted = false;
    private static ExerciseEntry entry;
    private SensorManager sensorManager;

    public static String currentString;
    public static int startTime;
    public static float currentCalorie;
    public static Calendar startCal;
    public static float distance;

    private List<Messenger> mClients = new ArrayList<Messenger>();
    public static final int MSG_REGISTER_CLIENT = 1;
    public static final int MSG_UNREGISTER_CLIENT = 2;
    public static final int MSG_SET_INT_VALUE = 3;
    public static final int MSG_FINISH_EXERCISE = 4;

    private final Messenger mMessenger = new Messenger(
            new IncomingMessageHandler());

    public static ExerciseEntry getExerciseEntry(){
        return entry;
    }
    public static double activityType = 0;

    public LinkedHashMap<Integer, Double> queue;
    public long lastTimeCheck = System.currentTimeMillis();
    public boolean isAutomatic;

    /**
     * This resets the variables, in case of a save, cancel. etc
     */
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

        activityType = 0;

        String svcName= Context.LOCATION_SERVICE;
        locationManager = (LocationManager)getSystemService(svcName);

        initializeLocationManager();
        pref = getSharedPreferences(SettingsActivity.PREF_KEYS_USER_DETAIL, MODE_PRIVATE);

        hasStarted = true;

        if (isAutomatic){
            sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
            sensorManager.registerListener(this,
                    sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                    SensorManager.SENSOR_DELAY_NORMAL);
        }


        queue = new LinkedHashMap<Integer, Double>(){
            @Override
            protected boolean removeEldestEntry(Entry<Integer, Double> eldest) {
                return this.size()>40;
            }
        };

    }

    @Override
    public void onSensorChanged (SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            checkActivityType(event);
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void checkActivityType(SensorEvent event){
        // Movement
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        float accelerationSquareRoot = (x * x + y * y + z * z)
                / (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);
        long actualTime = System.currentTimeMillis();

        queue.put((int)actualTime, (double)accelerationSquareRoot);
        long currentTime = System.currentTimeMillis();

        if (currentTime - lastTimeCheck > 30000){
            lastTimeCheck = currentTime;
            Collection<Double> data = queue.values();
            try {
                activityType  = WekaClassifier.classify(data.toArray());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Essentially we need to make sure that the locationManager is initialize here, we ask
     * for the permission in the MapExerciseActivity
     */
    public void initializeLocationManager(){
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

    /**
     * Starts recording the locations to the entry
     * */
    private void startRecording(){
        startCal = Calendar.getInstance();
        startTime = startCal.get(Calendar.SECOND);
    }

    /**
     * Adds the current point to the list of locations
     * */
    private void addLocation(Location location){
        ArrayList<LatLng> list = entry.getmLocationList();
        LatLng latlng=fromLocationToLatLng(location);
        list.add(latlng);
        entry.setmLocationList(list);
        Log.d("cs65", "the loc size is" + entry.getmLocationList().size());
    }

    /**
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

    /**
     * This generates the climb to the correct string format -- taking in consideration
     * of the preference units
     * */
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

    /**
    * This converts the current speed to the correct string format -- taking in consideration
     * of the preference units
    * */
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

    public String getActivityTypeStr(){
        int actType = (int)activityType;
        List<String> myInputType = Arrays.asList(getBaseContext().getResources().getStringArray(R.array.activity_type_array));
        return myInputType.get((int)activityType);
    }

    /**
     * We do all of our updates here, we need to update the display string as well
     * */
    private void updateWithNewLocation(Location location) {
        if (location != null) {
            String avgSpeedStr = getAvgSpeedStr();
            String currSpeedStr = getCurrentSpeedStr(location);
            String climbStr = getClimbStr(location.getAltitude());

            String formatStr = "Type: %s\nAvg speed: %s\nCur speed: %s\nClimb: %s\nCalorie: %f\nDistance: %s";

            currentCalorie = (getDistance() / 15.0f);

            currentString = String.format(formatStr, getActivityTypeStr(),avgSpeedStr, currSpeedStr, climbStr, currentCalorie, getDistanceStr());

            // Update the exercise entry object.
            if (hasStarted == false){
                hasStarted = true;
                startRecording();
            }

            addLocation(location);

            // tell the map to draw the current state
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
        isAutomatic = intent.getExtras().getBoolean("isAutomatic");
        activityType = (double)intent.getExtras().getInt("activityType");
        return START_STICKY; // Run until explicitly stopped.
    }

    /**
     * Display a notification in the notification bar.
     */
    private void showNotification() {

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MapExerciseActivity.class), 0);
        Notification notification = new Notification.Builder(this)
                .setContentTitle(this.getString(R.string.app_name))
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
                // we send a placeholder integer, it doesn't matter at all though
                messenger.send(Message.obtain(null, MSG_SET_INT_VALUE,
                        placeHolder, 0));
            } catch (RemoteException e) {
                // The client is dead. Remove it from the list.
                // Ideally we only need one client reference, instead of an arraylist
                mClients.remove(messenger);
            }
        }
    }

    public static boolean isRunning() {
        return hasStarted;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // we need to remove the notification from the top
        mNotificationManager.cancelAll();
        hasStarted = false;

        // reset the variables, in case the service is destroyed, but not the activity
        resetVars();
    }

    /**
     * Handle incoming messages from MapExerciseActivity
     * we dont really care for any other values being sent in
     * other than registration and unregistration
     */
    private class IncomingMessageHandler extends Handler { // Handler of

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_REGISTER_CLIENT:
                    mClients.add(msg.replyTo);
                    showNotification();
                    break;
                case MSG_UNREGISTER_CLIENT:
                    mClients.remove(msg.replyTo);
                    break;
                case MSG_FINISH_EXERCISE:
                    mClients.remove(msg.replyTo);
                    mNotificationManager.cancelAll();
                    resetVars();
                default:
                    super.handleMessage(msg);
            }
        }
    }
}