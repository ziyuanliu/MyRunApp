package com.example.ziyuanliu.myapplication.backend.data;

/**
 * Created by ziyuanliu on 5/17/16.
 */

import com.google.appengine.api.socket.SocketServicePb;
import com.google.appengine.labs.repackaged.org.json.JSONException;

import com.google.appengine.repackaged.org.antlr.runtime.misc.IntArray;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

import org.json.JSONObject;

import java.util.Calendar;

@Entity
public class ExerciseEntry {


    public static final String EXERCISE_PARENT_ENTITY_NAME = "ExerciseParent";
    public static final String EXERCISE_PARENT_KEY_NAME = "ExerciseParent";

    public static final String EXERCISE_ENTITY_NAME = "ExerciseEntry";

    public static final String FIELD_ID = "id";
    public static final String FIELD_INPUT_TYPE = "input_type";
    public static final String FIELD_ACTIVITY_TYPE = "activity_type";
    public static final String FIELD_DATE_TIME = "date_time";
    public static final String FIELD_DURATION = "duration";
    public static final String FIELD_DISTANCE = "distance";
    public static final String FIELD_AVG_SPEED = "average_speed";
    public static final String FIELD_CALORIE = "calories";
    public static final String FIELD_CLIMB = "climb";
    public static final String FIELD_HEART_RATE = "heartrate";
    public static final String FIELD_COMMENT = "comment";

    public static final String KEY_NAME = FIELD_ID;

    @Id
    Long id;

    @Index
    public String mId;

    public String mInputType;        // Manual, GPS or automatic
    public String mActivityType;     // Running, cycling etc.
    public String mDateTime;    // When does this entry happen
    public String mDuration;         // Exercise duration in seconds
    public String mDistance;      // Distance traveled. Either in meters or feet.
    public String mAvgSpeed;      // Average speed
    public String mCalorie;          // Calories burnt
    public String mClimb;         // Climb. Either in meters or feet.
    public String mHeartRate;        // Heart rate
    public String mComment;       // Comments

    public ExerciseEntry(String _id, String _inputType, String _activityType, String _datetime, String _duration, String _distance,
                         String _avgSpeed, String _calorie, String _climb, String _heartRate, String _comment) {
        mId = _id;
        mInputType = _inputType;
        mActivityType = _activityType;
        mDateTime = _datetime;
        mDuration = _duration;
        mDistance = _distance;
        mAvgSpeed = _avgSpeed;
        mCalorie = _calorie;
        mClimb = _climb;
        mHeartRate = _heartRate;
        mComment = _comment;
    }

    public ExerciseEntry(JSONObject obj) throws org.json.JSONException {

        mId = obj.getString("mId");
            mInputType = obj.getString("mInputType");
            mActivityType = obj.getString("mActivityType");
            mDateTime = obj.getString("mDateTime");
            mDuration = obj.getString("mDuration");
            mDistance = obj.getString("mDistance");
            mAvgSpeed = obj.getString("mAvgSpeed");
            mCalorie = obj.getString("mCalorie");
            mClimb = obj.getString("mClimb");
            mHeartRate = obj.getString("mHeartRate");
            mComment = obj.getString("mComment");
    }

    @Override
    public int hashCode() {
        return Integer.valueOf(this.mId);
    }

    @Override
    public boolean equals(Object obj) {
        return obj.hashCode() == this.hashCode();
    }
}
