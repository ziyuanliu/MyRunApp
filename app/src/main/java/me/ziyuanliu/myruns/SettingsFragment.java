package me.ziyuanliu.myruns;

import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

public class SettingsFragment extends Fragment {

    private static final String[] SETTINGS_ROWS = new String[]{
            "section#Account Preferences",
            "Name, Email, Class etc#User Profile",
            "Privacy Setting#Posting your records anonymously#checkbox",
            "section#Additional Settings",
            "Unit Preference#Select the units",
            "Comments#Please enter your comments",
            "section#Misc.",
            "Webpage#http://web.cs.dartmouth.edu/",
    };

    public void doPositiveClick(String fragType) {
    }

    public void doNegativeClick(String fragType) {
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // XD: If set to true then when your layout is inflated it will be automatically added to the view hierarchy
        // of the ViewGroup specified in the 2nd parameter as a child.
        //XD:if set to false, they are not added as direct children of the parent and
        // the parent doesn't recieve any touch events from the views.

        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        ListView settingsListView = (ListView)view.findViewById(R.id.settings_list_view);
        final SharedPreferences pref = getActivity().getSharedPreferences(SettingsActivity.PREF_KEYS_USER_DETAIL, SettingsActivity.MODE_PRIVATE);

        ArrayAdapter<String> listViewAdapter = new ArrayAdapter<String>(getActivity(), R.layout.list_view_row, SETTINGS_ROWS){

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
//                Log.d("LIST", String.valueOf(position));
                String setting_row_str = SETTINGS_ROWS[position];
                String[] splitted_str = setting_row_str.split("#");

                View row = convertView;

                if (row==null){
                    LayoutInflater inflater=(LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    row=inflater.inflate(R.layout.list_view_row, null);
                }


                TextView title = (TextView)row.findViewById(R.id.text_view_title);
                TextView subtitle = (TextView)row.findViewById(R.id.text_view_subtitle);
                CheckBox btn = (CheckBox) row.findViewById(R.id.text_view_checkbox);
                btn.setEnabled(false);
                if (splitted_str[0].equals("section")){
                    title.setText(splitted_str[1]);
                    title.setTextSize(10);
                    title.setTextColor(Color.RED);
                    subtitle.setVisibility(View.GONE);
                    btn.setVisibility(View.GONE);
                    row.setEnabled(false);

                }else{
                    title.setText(splitted_str[0]);
                    subtitle.setText(splitted_str[1]);

                    if (splitted_str.length==2){
                        btn.setVisibility(View.GONE);
                    }else{
                        Boolean isChecked = pref.getBoolean(SettingsActivity.PREF_KEYS_USER_ANON, false);
                        btn.setChecked(isChecked);
                    }

                }

                return row;
            }
        };

        settingsListView.setAdapter(listViewAdapter);

        final Fragment self = this;
        settingsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String setting_row_str = SETTINGS_ROWS[position];
                String[] splitted_str = setting_row_str.split("#");

                View row = view;
                TextView title = (TextView)row.findViewById(R.id.text_view_title);
                TextView subtitle = (TextView)row.findViewById(R.id.text_view_subtitle);
                CheckBox btn = (CheckBox)row.findViewById(R.id.text_view_checkbox);

                if (row.isEnabled()==false){
                    return;
                }

                if (splitted_str.length==3){
                    btn.setChecked(!btn.isChecked());
                }
                Intent intent;
                DialogFragment newFragment;
                switch (position){
                    case 1:
                        intent = new Intent(getActivity(), SettingsActivity.class);
                        getActivity().startActivity(intent);
                        break;
                    case 2:
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putBoolean(SettingsActivity.PREF_KEYS_USER_ANON, btn.isChecked());
                        editor.commit();
                        break;
                    case 4:
                        newFragment = UnitDialogFragment.newInstance(R.string.alert_dialog_unit_preferred, self);
                        newFragment.show(getFragmentManager(), "dialog");

                        break;
                    case 5:
                        newFragment = CommentDialogFragment.newInstance(R.string.alert_dialog_comment, "Write your comment here",
                                InputType.TYPE_CLASS_TEXT);
                        newFragment.show(getFragmentManager(), "dialog");
                        break;
                    case 7:
                        intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(splitted_str[1]));
                        getActivity().startActivity(intent);
                        break;

                    default:
                        break;
                }

            }
        });

        return view;

    }
}

