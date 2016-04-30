package me.ziyuanliu.myruns;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Loader;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;

import me.ziyuanliu.myruns.database.ExerciseEntry;
import me.ziyuanliu.myruns.database.ExerciseEntryDatasource;

/**
 * Created by ziyuanliu on 4/24/16.
 */
public class ExerciseEntryActivity extends Activity  implements LoaderManager.LoaderCallbacks<ExerciseEntry>{

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
        setContentView(R.layout.activity_exercise_entry);

        // get the rowId passed in here
        datasource = new ExerciseEntryDatasource(getApplicationContext());
        this.rowId = getIntent().getExtras().getLong("rowId");

        // start the asyncloader
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<ExerciseEntry> onCreateLoader(int id, Bundle args) {
        return new DataLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<ExerciseEntry> loader, ExerciseEntry data) {
        // we now make sure that the edittext fields are populated
        EditText edittext = (EditText)findViewById(R.id.inputTypeEditText);
        edittext.setText(data.inputTypeFromIndex());

        edittext = (EditText)findViewById(R.id.activityTypeEditText);
        edittext.setText(data.activityTypeFromIndex());

        edittext = (EditText)findViewById(R.id.dateTimeEditText);
        edittext.setText(data.getDatetimeStr());

        edittext = (EditText)findViewById(R.id.durationEditText);
        edittext.setText(String.valueOf(data.getDurationStr()));

        edittext = (EditText)findViewById(R.id.distanceEditText);
        edittext.setText(String.valueOf(data.getDistanceWithUnits()));

        edittext = (EditText)findViewById(R.id.caloriesEditText);
        edittext.setText(String.valueOf(data.getmCalorie()));

        edittext = (EditText)findViewById(R.id.heartrateEditText);
        edittext.setText(String.valueOf(data.getmHeartRate())+" bpm");
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
            ExerciseEntry retval = datasource.fetchEntryByIndex(ExerciseEntryActivity.rowId);
            datasource.close();

            return retval;
        }
    }
}
