package me.ziyuanliu.myruns.database;

import android.content.Context;
import android.content.SharedPreferences;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import com.google.android.gms.maps.model.LatLng;

import me.ziyuanliu.myruns.R;
import me.ziyuanliu.myruns.SettingsActivity;

/**
 * Created by ziyuanliu on 4/23/16.
 * taken from the lovely template on cs65 site
 */
public class ExerciseEntry {
    private Long id;

    private int mInputType;        // Manual, GPS or automatic
    private int mActivityType;     // Running, cycling etc.
    private Calendar mDateTime;    // When does this entry happen
    private int mDuration;         // Exercise duration in seconds
    private double mDistance;      // Distance traveled. Either in meters or feet.
    private double mAvgPace;       // Average pace
    private double mAvgSpeed;      // Average speed
    private int mCalorie;          // Calories burnt
    private double mClimb;         // Climb. Either in meters or feet.
    private int mHeartRate;        // Heart rate
    private String mComment;       // Comments
    private ArrayList<LatLng> mLocationList; // Location list

    private Context con;

    public ExerciseEntry(Context con){
        this.con = con;
    }

    public void setContext(Context con){
        this.con = con;
    }

    public void setId(long id){
        this.id = id;
    }

    public long getId(){
        return this.id;
    }

    public void setmInputType(int mInputType) {
        this.mInputType = mInputType;
    }

    public int getmInputType() {
        return mInputType;
    }

    public void setmActivityType(int mActivityType) {
        this.mActivityType = mActivityType;
    }

    public int getmActivityType() {
        return mActivityType;
    }

    public void setmDateTime(Calendar mDateTime) {
        this.mDateTime = mDateTime;
    }

    public Calendar getmDateTime() {
        return mDateTime;
    }

    /*
    dur is the float representation of duration in minutes
     */
    public void setmDuration(double dur) {
        int seconds = (int)(dur * 60);
        this.mDuration = seconds;
    }

    public int getmDuration() {
        return mDuration;
    }

    public void setmDistance(double mDistance) {
        this.mDistance = mDistance;
    }

    public double getmDistance() {
        return mDistance;
    }

    public void setmAvgPace(double mAvgPace) {
        this.mAvgPace = mAvgPace;
    }

    public double getmAvgPace() {
        return mAvgPace;
    }

    public void setmAvgSpeed(double mAvgSpeed) {
        this.mAvgSpeed = mAvgSpeed;
    }

    public double getmAvgSpeed() {
        return mAvgSpeed;
    }

    public void setmCalorie(int mCalorie) {
        this.mCalorie = mCalorie;
    }

    public int getmCalorie() {
        return mCalorie;
    }

    public void setmClimb(double mClimb) {
        this.mClimb = mClimb;
    }

    public double getmClimb() {
        return mClimb;
    }

    public void setmHeartRate(int mHeartRate) {
        this.mHeartRate = mHeartRate;
    }

    public int getmHeartRate() {
        return mHeartRate;
    }

    public void setmComment(String mComment) {
        this.mComment = mComment;
    }

    public String getmComment() {
        return mComment;
    }

    public void setmLocationList(ArrayList<LatLng> mLocationList) {
        this.mLocationList = mLocationList;
    }

    public ArrayList<LatLng> getmLocationList() {
        return mLocationList;
    }

    /*
    returns the String Input Type
     */
    public String inputTypeFromIndex(){
        int index = this.getmInputType();
        List<String> myInputType = Arrays.asList(con.getResources().getStringArray(R.array.input_type_array));
        return myInputType.get(index);
    }

    /*
    returns the String activity type
     */
    public String activityTypeFromIndex(){
        int index = this.getmActivityType();
        List<String> myInputType = Arrays.asList(con.getResources().getStringArray(R.array.activity_type_array));
        return myInputType.get(index);
    }

    /*
    this method returns the string version of the distance variable, it will take sharedpreference in consideration
    in terms of presentation and unit suffixes.
     */
    public String getDistanceWithUnits(){
        double meterDistance = this.getmDistance();
        SharedPreferences pref = this.con.getSharedPreferences(SettingsActivity.PREF_KEYS_USER_DETAIL, Context.MODE_PRIVATE);
        List<String> choices = Arrays.asList(con.getResources().getStringArray(R.array.system_unit_type));
        int itemChoice = pref.getInt(SettingsActivity.PREF_KEYS_USER_UNIT_TYPE,0);

        double retval = this.getmDistance();
        if (itemChoice==0){
            // kilometers
            retval/=1000.0;

        }else{
            // miles
            retval *= 0.000621371;
        }
        return String.valueOf(retval)+" "+choices.get(itemChoice);
    }

    public String getDatetimeStr(){
        Calendar tempCal = this.getmDateTime();
        SimpleDateFormat format = new SimpleDateFormat("h:mm a MMMM d, yyyy");
        return format.format(tempCal.getTime());
    }

    public String getDurationStr(){
        int dur = this.getmDuration();
        int seconds = dur % 60;
        int minutes = (int)(dur/60.0);
        String durationStr = minutes+ "mins "+seconds+"secs";
        return durationStr;
    }

    /*
    how it will appear in the history tab, let's delimit the to string with '$' for title and
    subtitle DISTANCE IS ALWAYS METRIC
     */
    public String toString(){
        String durationStr = getDurationStr();
        String inputType = inputTypeFromIndex();
        String activityType = activityTypeFromIndex();

        String datetime = getDatetimeStr();

        return inputType +": "+activityType+", "+datetime
                +"$"+getDistanceWithUnits()+", "+durationStr;
    }
}