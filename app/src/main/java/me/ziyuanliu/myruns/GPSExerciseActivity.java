package me.ziyuanliu.myruns;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

/**
 * Created by ziyuanliu on 4/11/16.
 * Will be implemented for Google Maps tracking
 */
public class GPSExerciseActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps);
    }

    public void gpsCancel(View view){
        finish();
    }
}
