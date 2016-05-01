package me.ziyuanliu.myruns;

import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Loader;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

import me.ziyuanliu.myruns.database.ExerciseEntry;
import me.ziyuanliu.myruns.database.ExerciseEntryDatasource;

public class MapExerciseEntryActivity extends FragmentActivity implements OnMapReadyCallback, LoaderManager.LoaderCallbacks<ExerciseEntry> {

    private GoogleMap mMap;
    public Marker lastMarker;
    public Marker firstMarker;
    static ExerciseEntry entry;
    private SharedPreferences pref;

    PolylineOptions rectOptions;
    Polyline polyline;

    static long rowId;
    static ExerciseEntryDatasource datasource;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_delete_btn, menu);
        return true;
    }

    public boolean deleteEntryClicked(MenuItem item){
        // make a runnable thread to delete the exercise entry
        Runnable deleteThread = new Runnable() {
            @Override
            public void run() {
                datasource.open();
                datasource.deleteExerciseEntry(rowId);
                datasource.close();
            }
        };

        deleteThread.run();
        finish();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        entry = null;
        setContentView(R.layout.activity_map_exercise_entry);
        pref = getSharedPreferences(SettingsActivity.PREF_KEYS_USER_DETAIL, MODE_PRIVATE);

        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.resultMap))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            }
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.resultMap);
        mapFragment.getMapAsync(this);

        // get the rowId passed in here
        datasource = new ExerciseEntryDatasource(getApplicationContext());
        this.rowId = getIntent().getExtras().getLong("rowId");

        // start the asyncloader
        getLoaderManager().initLoader(1, null, this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        //draw the map


    }

    @Override
    public Loader<ExerciseEntry> onCreateLoader(int id, Bundle args) {
        return new DataLoader(this);
    }

    public String avgSpeedStr(double dist, double seconds){
        double hours = seconds/60.0f/60.0f;
        double avgSpeed = dist/hours;

        int itemChoice = pref.getInt(SettingsActivity.PREF_KEYS_USER_UNIT_TYPE,0);
        if (itemChoice == 0){
            return avgSpeed/1000.0+ " km//h";
        }else{
            return avgSpeed*0.000621371+ " m/h";
        }
    }

    public String climbStr(double climb){
        int itemChoice = pref.getInt(SettingsActivity.PREF_KEYS_USER_UNIT_TYPE,0);
        if (itemChoice == 0){
            return climb/1000.0+ " km";
        }else{
            return climb*0.000621371+ " m";
        }
    }

    @Override
    public void onLoadFinished(Loader<ExerciseEntry> loader, ExerciseEntry data) {
        // we now make sure that the edittext fields are populated
        String formatStr = "Type: Running\nAvg speed: %s\nCur speed: %s\nClimb: %s\nCalorie: %f\nDistance: %s";

        double dist = data.getmDistance();

        String currSpeedStr = "NA";

        double climb = data.getmClimb();
        int calories = data.getmCalorie();

        String value = String.format(formatStr, avgSpeedStr(dist, data.getmDuration()), currSpeedStr, climbStr(climb), (float)calories, data.getDistanceWithUnits());
        TextView tv = (TextView)findViewById(R.id.resultStats);
        tv.setText(value);

        this.entry = data;

        ArrayList<LatLng> arrList = (ArrayList<LatLng>) entry.getmLocationList();
        LatLng last = null;
        for (int i = 0; i < arrList.size(); i ++){
            LatLng pos = arrList.get(i);
            if (i == 0){
                // add start marker
                last = pos;
                firstMarker = mMap.addMarker(new MarkerOptions().position(pos).icon(BitmapDescriptorFactory.defaultMarker(
                        BitmapDescriptorFactory.HUE_GREEN)));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pos,
                        17));
                rectOptions = new PolylineOptions();

            }else if (i == arrList.size()-1){
                // an end marker
                lastMarker = mMap.addMarker(new MarkerOptions().position(pos).icon(BitmapDescriptorFactory.defaultMarker(
                        BitmapDescriptorFactory.HUE_RED)));
            }

            rectOptions.add(pos);

        }
        rectOptions.color(Color.RED);

        polyline = mMap.addPolyline(rectOptions);
    }

    @Override
    public void onLoaderReset(Loader<ExerciseEntry> loader) {

    }

    public static class DataLoader extends AsyncTaskLoader<ExerciseEntry> {

        public DataLoader(Context context){
            super(context);
        }

        @Override
        public void onStartLoading(){
            forceLoad();
        }

        @Override
        public ExerciseEntry loadInBackground() {
            // this is what we do in the background asynchronously
            datasource.open();
            ExerciseEntry retval = datasource.fetchEntryByIndex(MapExerciseEntryActivity.rowId);
            datasource.close();

            return retval;
        }
    }
}
