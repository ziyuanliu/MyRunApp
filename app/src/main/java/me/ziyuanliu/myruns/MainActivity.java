package me.ziyuanliu.myruns;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;

import java.util.ArrayList;

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
        boolean isMapActivity = pref.getInt(SettingsActivity.PREF_KEYS_USER_INPUT_TYPE, 0) > 0;

        Intent intent = new Intent(this, isMapActivity ? GPSExerciseActivity.class : ManualExerciseActivity.class);
        startActivity(intent);
    }

}
