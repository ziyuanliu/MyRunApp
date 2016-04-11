package me.ziyuanliu.myruns;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import static android.widget.ArrayAdapter.*;

public class StartFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // XD: If set to true then when your layout is inflated it will be automatically added to the view hierarchy
        // of the ViewGroup specified in the 2nd parameter as a child.
        //XD:if set to false, they are not added as direct children of the parent and
        // the parent doesn't recieve any touch events from the views.
        View view =  inflater.inflate(R.layout.fragment_start, container, false);
        final SharedPreferences pref = getActivity().getSharedPreferences(SettingsActivity.PREF_KEYS_USER_DETAIL, Context.MODE_PRIVATE);

        Spinner inputTypeSpinner = (Spinner)view.findViewById(R.id.input_type);
        ArrayAdapter<CharSequence> inputTypeArrayAdapter = ArrayAdapter.createFromResource(getActivity().getBaseContext(),
                R.array.input_type_array, android.R.layout.simple_spinner_item);

        inputTypeSpinner.setAdapter(inputTypeArrayAdapter);

        int inputTypeIndex = pref.getInt(SettingsActivity.PREF_KEYS_USER_INPUT_TYPE, -1);
        if (inputTypeIndex > -1){
            inputTypeSpinner.setSelection(inputTypeIndex);
        }

        inputTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences.Editor editor = pref.edit();
                editor.putInt(SettingsActivity.PREF_KEYS_USER_INPUT_TYPE, position);
                editor.commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Spinner activityTypeSpinner = (Spinner)view.findViewById(R.id.activity_type);

        ArrayAdapter<CharSequence> activityTypeAdapter = ArrayAdapter.createFromResource(getActivity(),
            R.array.activity_type_array, android.R.layout.simple_spinner_item);

        activityTypeSpinner.setAdapter(activityTypeAdapter);

        int activityTypeInd = pref.getInt(SettingsActivity.PREF_KEYS_USER_ACTIVITY_TYPE, -1);
        if (activityTypeInd>-1){
            activityTypeSpinner.setSelection(activityTypeInd);
        }

        activityTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences.Editor editor = pref.edit();
                editor.putInt(SettingsActivity.PREF_KEYS_USER_ACTIVITY_TYPE, position);
                editor.commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        return view;

    }

}

