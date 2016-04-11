package me.ziyuanliu.myruns;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.Arrays;
import java.util.List;

/**
 * Created by ziyuanliu on 4/11/16.
 */
public class ManualExerciseActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_exercise);

        // create a string array from resources
        String[] exercise_detail_fields = getResources().getStringArray(R.array.exercise_details);
        List<String> myResArrayList = Arrays.asList(exercise_detail_fields);

        // use that string array as the base for the listview adapter
        ListView exercise_details = (ListView)findViewById(R.id.exercise_detail_list);
        ArrayAdapter<String> exercise_det_list = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, myResArrayList);
        exercise_details.setAdapter(exercise_det_list);

        // handle each item click
        exercise_details.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                DialogFragment frag;
                switch (position){
                    case 0:
                        frag = new DateDialogPicker();
                        frag.show(getFragmentManager(), "datepicker");
                        break;

                    case 1:
                        frag = new TimeDialogPicker();
                        frag.show(getFragmentManager(), "timepicker");
                        break;

                    case 2:
                        frag =  CommentDialogFragment.newInstance(R.string.alert_dialog_duration, "",
                                InputType.TYPE_CLASS_NUMBER);                        frag.show(getFragmentManager(), "dialog");
                        break;

                    case 3:
                        frag =  CommentDialogFragment.newInstance(R.string.alert_dialog_distance, "",
                                InputType.TYPE_CLASS_NUMBER);                        frag.show(getFragmentManager(), "dialog");
                        break;

                    case 4:
                        frag =  CommentDialogFragment.newInstance(R.string.alert_dialog_calories, "",
                                InputType.TYPE_CLASS_NUMBER);                        frag.show(getFragmentManager(), "dialog");
                        break;

                    case 5:
                        frag =  CommentDialogFragment.newInstance(R.string.alert_dialog_heart_rate, "",
                                InputType.TYPE_CLASS_NUMBER);
                        frag.show(getFragmentManager(), "dialog");
                        break;

                    case 6:
                        frag =  CommentDialogFragment.newInstance(R.string.alert_dialog_comment, "Write your comment here",
                                InputType.TYPE_CLASS_TEXT);
                        frag.show(getFragmentManager(), "dialog");
                        break;

                    default:

                        break;

                }
            }
        });
    }
}
