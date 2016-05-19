package me.ziyuanliu.myruns;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import me.ziyuanliu.myruns.database.ExerciseEntry;
import me.ziyuanliu.myruns.database.ExerciseEntryDBHelper;
import me.ziyuanliu.myruns.database.ExerciseEntryDatasource;
import me.ziyuanliu.myruns.fragment.HistoryFragment;
import me.ziyuanliu.myruns.fragment.SettingsFragment;
import me.ziyuanliu.myruns.fragment.StartFragment;
import me.ziyuanliu.myruns.view.SlidingTabLayout;


/**
 * Created by ziyuanliu on 4/8/16.
 */


public class MainActivity extends Activity{
    private SlidingTabLayout slidingTabLayout;
    private ViewPager viewPager;

    private ArrayList<Fragment> fragments;
    private ActionTabsViewPagerAdapter actionTabsViewPagerAdapter;

    public static String SERVER_ADDR = "http://10.0.2.2:8080";

    /** Called when the activity is first created. Inspiration from actionbar file*/
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Define SlidingTabLayout (shown at top)
        // and ViewPager (shown at bottom) in the layout.
        // Get their instances.
        slidingTabLayout = (SlidingTabLayout) findViewById(R.id.tab);
        viewPager = (ViewPager) findViewById(R.id.viewpager);

        // create a fragment list in order.
        fragments = new ArrayList<Fragment>();
        fragments.add(new StartFragment());
        fragments.add(new HistoryFragment());
        fragments.add(new SettingsFragment());

        // use FragmentPagerAdapter to bind the slidingTabLayout (tabs with different titles)
        // and ViewPager (different pages of fragment) together.
        actionTabsViewPagerAdapter = new ActionTabsViewPagerAdapter(this.getFragmentManager(), fragments);
        viewPager.setAdapter(actionTabsViewPagerAdapter);

        // make sure the tabs are equally spaced.
        slidingTabLayout.setDistributeEvenly(true);
        slidingTabLayout.setViewPager(viewPager);

    }

    /*
    * Deal with start button being clicked, here we decide on whether it's going to be a map or manual activity
    * */
    public void startBtnClicked(View view){
        SharedPreferences pref = getSharedPreferences(SettingsActivity.PREF_KEYS_USER_DETAIL, MODE_PRIVATE);
        int inputType = pref.getInt(SettingsActivity.PREF_KEYS_USER_INPUT_TYPE, 0);
        boolean isMapActivity = inputType > 0;
        int activityType = pref.getInt(SettingsActivity.PREF_KEYS_USER_ACTIVITY_TYPE, 0);
        boolean isAutomatic = inputType == 2;

        Intent intent = new Intent(this, isMapActivity ? MapExerciseActivity.class : ManualExerciseActivity.class);
        intent.putExtra("isAutomatic", isAutomatic);
        intent.putExtra("activityType", activityType);
        startActivity(intent);
    }

    /**
     * deal with the sync operation
     * @param view
     */
    public void syncBtnClicked(View view){
        new AsyncTask<Void, Void, String>(){

            @Override
            protected String doInBackground(Void... params) {
                String uploadState="";
                try {
                    // lets open up a db datasource to get the list of activities

                    ExerciseEntryDatasource datasource = new ExerciseEntryDatasource(getApplicationContext());

                    datasource.open();
                    ArrayList<ExerciseEntry> exercises = (ArrayList<ExerciseEntry>) datasource.getAllEntries();
                    datasource.close();

                    ArrayList<JSONObject> jsonList = new ArrayList<JSONObject>();
                    for (ExerciseEntry entry: exercises){
                        jsonList.add(entry.toJson());
                    }

                    JSONArray jsonArray = new JSONArray(jsonList);
                    String jsonStr = jsonArray.toString();

                    Map<String, String> payload = new HashMap<String, String>();
                    payload.put("json", jsonStr);

                    ServerUtilities.post(SERVER_ADDR+"/sync.do", payload);
                } catch (IOException e1) {
                    uploadState = "Sync failed: " + e1.getCause();
                    Log.e("TAGG", "data posting error " + e1);
                }

                return uploadState;
            }
        }.execute();
    }

}
