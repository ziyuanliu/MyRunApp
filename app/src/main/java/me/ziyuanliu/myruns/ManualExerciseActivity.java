package me.ziyuanliu.myruns;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.w3c.dom.Comment;

import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import me.ziyuanliu.myruns.database.ExerciseEntry;
import me.ziyuanliu.myruns.database.ExerciseEntryDatasource;
import me.ziyuanliu.myruns.dialog.DateDialogPicker;
import me.ziyuanliu.myruns.dialog.TimeDialogPicker;
import me.ziyuanliu.myruns.fragment.CommentDialogFragment;

/**
 * Created by ziyuanliu on 4/11/16.
 */
public class ManualExerciseActivity extends Activity {
    /*
    for ease of usage, I've decided to use static variable pattern on this activity,
    we can always assume that there's only one entry at a time
     */
    public static Calendar cal;
    public static HashMap<String, String> hashMap;

    SharedPreferences pref;

    public static final String TAG_DATE_PICKER = "datepicker";
    public static final String TAG_TIME_PICKER = "timepicker";
    public static final String TAG_DURATION = "duration";
    public static final String TAG_DISTANCE = "distance";
    public static final String TAG_HEARTBEAT = "heartbeat";
    public static final String TAG_CALORIES = "calories";
    public static final String TAG_COMMENT = "comment";

    DateDialogPicker datePicker;
    TimeDialogPicker timePicker;
    CommentDialogFragment durationDialogFragment;
    CommentDialogFragment distanceDialogFragment;
    CommentDialogFragment caloriesDialogFragment;
    CommentDialogFragment heartrateDialogFragment;
    CommentDialogFragment commentDialogFragment;

    ExerciseEntryDatasource datasource;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // we need to reset the static values
        cal = Calendar.getInstance();
        hashMap = new HashMap<String, String>();

        // get the datasource context and the sharedpreference
        datasource = new ExerciseEntryDatasource(this);
        pref = this.getSharedPreferences(SettingsActivity.PREF_KEYS_USER_DETAIL, Context.MODE_PRIVATE);

        setContentView(R.layout.activity_start_exercise);

        // create a string array from resources
        String[] exercise_detail_fields = getResources().getStringArray(R.array.exercise_details);
        List<String> myResArrayList = Arrays.asList(exercise_detail_fields);

        // use that string array as the base for the listview adapter
        ListView exercise_details = (ListView)findViewById(R.id.exercise_detail_list);
        ArrayAdapter<String> exercise_det_list = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, myResArrayList);
        exercise_details.setAdapter(exercise_det_list);

        final Activity self = this;
        // handle each item click
        exercise_details.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        if (datePicker == null)
                            datePicker = new DateDialogPicker();
                        datePicker.show(getFragmentManager(), TAG_DATE_PICKER);
                        break;

                    case 1:
                        if (timePicker == null)
                            timePicker = new TimeDialogPicker();
                        timePicker.show(getFragmentManager(), TAG_TIME_PICKER);
                        break;

                    case 2:
                        if (durationDialogFragment == null)
                            durationDialogFragment =  CommentDialogFragment.newInstance(R.string.alert_dialog_duration, "",
                                InputType.TYPE_CLASS_NUMBER);
                        durationDialogFragment.show(getFragmentManager(), TAG_DURATION);
                        break;

                    case 3:

                        SharedPreferences pref = self.getSharedPreferences(SettingsActivity.PREF_KEYS_USER_DETAIL, Context.MODE_PRIVATE);
                        int itemChoice = pref.getInt(SettingsActivity.PREF_KEYS_USER_UNIT_TYPE,0);

                        String units = "units";

                        if (itemChoice==0){
                            units="meters";
                        }else{
                            units="feet";
                        }

                        if (distanceDialogFragment==null)
                            distanceDialogFragment =  CommentDialogFragment.newInstance(R.string.alert_dialog_distance, units,
                                InputType.TYPE_CLASS_NUMBER);
                        distanceDialogFragment.show(getFragmentManager(), TAG_DISTANCE);
                        break;

                    case 4:
                        if (caloriesDialogFragment==null)
                            caloriesDialogFragment =  CommentDialogFragment.newInstance(R.string.alert_dialog_calories, "",
                                InputType.TYPE_CLASS_NUMBER);
                        caloriesDialogFragment.show(getFragmentManager(), TAG_CALORIES);
                        break;

                    case 5:
                        if (heartrateDialogFragment==null)
                            heartrateDialogFragment =  CommentDialogFragment.newInstance(R.string.alert_dialog_heart_rate, "",
                                InputType.TYPE_CLASS_NUMBER);
                        heartrateDialogFragment.show(getFragmentManager(), TAG_HEARTBEAT);
                        break;

                    case 6:
                        if (commentDialogFragment==null)
                            commentDialogFragment =  CommentDialogFragment.newInstance(R.string.alert_dialog_comment, "Write your comment here",
                                InputType.TYPE_CLASS_TEXT);
                        commentDialogFragment.show(getFragmentManager(), TAG_COMMENT);
                        break;

                    default:

                        break;

                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /*
    this method will be used to save each exercise entry into the sqlite db
     */
    public void onExerciseSave(View view) throws IOException {
        // first grab the input and activity types and store them
        int inputType = pref.getInt(SettingsActivity.PREF_KEYS_USER_INPUT_TYPE, -1);
        int activityType = pref.getInt(SettingsActivity.PREF_KEYS_USER_ACTIVITY_TYPE, -1);

        ExerciseEntry entry = new ExerciseEntry(getApplicationContext());
        entry.setmDateTime(this.cal);
        entry.setmComment(this.hashMap.get(TAG_COMMENT));
        entry.setmInputType(inputType);
        entry.setmActivityType(activityType);

        // values derived from dialogs will be stored in the static hashmap
        if (this.hashMap.containsKey(TAG_CALORIES))
            entry.setmCalorie(Integer.valueOf(this.hashMap.get(TAG_CALORIES)));

        if (this.hashMap.containsKey(TAG_HEARTBEAT))
            entry.setmHeartRate(Integer.valueOf(this.hashMap.get(TAG_HEARTBEAT)));

        if (this.hashMap.containsKey(TAG_DISTANCE)){
            double dist = Integer.valueOf(this.hashMap.get(TAG_DISTANCE));
            // we need see which units it is right now
            SharedPreferences pref = this.getSharedPreferences(SettingsActivity.PREF_KEYS_USER_DETAIL, Context.MODE_PRIVATE);
            List<String> choices = Arrays.asList(this.getResources().getStringArray(R.array.system_unit_type));
            int itemChoice = pref.getInt(SettingsActivity.PREF_KEYS_USER_UNIT_TYPE,0);

            if (itemChoice==1){
                // convert feet to meters, we store our raw distance in metric
                dist *=0.3048;
            }
            entry.setmDistance(dist);
        }

        // duration is in minutes
        if (this.hashMap.containsKey(TAG_DURATION))
            entry.setmDuration(Integer.valueOf(this.hashMap.get(TAG_DURATION)));

        datasource.open();
        ExerciseEntry e = datasource.createExerciseEntry(entry);
        datasource.close();
        Toast.makeText(this, "Entry #"+e.getId()+" created", Toast.LENGTH_LONG).show();

        // we need to reset the static variables
        this.cal = Calendar.getInstance();
        this.hashMap.clear();
        finish();
    }

    public void onExerciseCancel(View view){
        finish();
    }
}
