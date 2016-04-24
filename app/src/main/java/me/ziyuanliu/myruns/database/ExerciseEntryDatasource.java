package me.ziyuanliu.myruns.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by ziyuanliu on 4/23/16.
 */
public class ExerciseEntryDatasource {
    private SQLiteDatabase database;
    private SQLiteOpenHelper dbHelper;
    private String[] allColumns = {
            ExerciseEntryDBHelper.COLUMN_ID,
            ExerciseEntryDBHelper.COLUMN_INPUT_TYPE,
            ExerciseEntryDBHelper.COLUMN_ACTIVITY_TYPE,
            ExerciseEntryDBHelper.COLUMN_DATE_TIME,
            ExerciseEntryDBHelper.COLUMN_DURATION,
            ExerciseEntryDBHelper.COLUMN_DISTANCE,
            ExerciseEntryDBHelper.COLUMN_AVG_PACE,
            ExerciseEntryDBHelper.COLUMN_AVG_SPEED,
            ExerciseEntryDBHelper.COLUMN_CALORIES,
            ExerciseEntryDBHelper.COLUMN_CLIMB,
            ExerciseEntryDBHelper.COLUMN_HEARTRATE,
            ExerciseEntryDBHelper.COLUMN_COMMENT,
            ExerciseEntryDBHelper.COLUMN_GPS_DATA
    };

    private final String TAG = "ExerciseEntryDS:";
    private Context con;

    public ExerciseEntryDatasource(Context context) {
        this.con = context;
        dbHelper = new ExerciseEntryDBHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        database.close();
    }

    /*
        here we create the exercise entry using the db helper, initially the only parameters that
        are needed are the ones that required to create the entry:
        COLUMN_INPUT_TYPE + " INTEGER NOT NULL,"
        COLUMN_DATE_TIME + " DATETIME NOT NULL,"
        COLUMN_DURATION + " INTEGER NOT NULL,"
     */
    public ExerciseEntry createExerciseEntry(ExerciseEntry entry) throws IOException {
        ContentValues values = new ContentValues();

        values.put(ExerciseEntryDBHelper.COLUMN_INPUT_TYPE, entry.getmInputType());
        values.put(ExerciseEntryDBHelper.COLUMN_ACTIVITY_TYPE, entry.getmActivityType());
        values.put(ExerciseEntryDBHelper.COLUMN_DATE_TIME, entry.getmDateTime().getTime().getTime());
        values.put(ExerciseEntryDBHelper.COLUMN_DURATION, entry.getmDuration());
        values.put(ExerciseEntryDBHelper.COLUMN_DISTANCE, entry.getmDistance());


        ArrayList<LatLng> locs = entry.getmLocationList();
        if (locs == null){
            locs = new ArrayList<LatLng>();
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(baos);
        for (LatLng element : locs) {
            String toWrite = element.latitude + "$" + element.longitude;
            out.writeUTF(toWrite);
        }
        byte[] bytes = baos.toByteArray();
        values.put(ExerciseEntryDBHelper.COLUMN_GPS_DATA, bytes);

        long insertId = database.insert(ExerciseEntryDBHelper.ENTRY_TABLE_NAME, null,
                values);


        return fetchEntryByIndex(insertId);
    }

    public void deleteExerciseEntry(long rowId) {
        database.delete(ExerciseEntryDBHelper.ENTRY_TABLE_NAME, ExerciseEntryDBHelper.COLUMN_ID + "=" + String.valueOf(rowId), null);
    }

    // Query a specific entry by its index.
    public ExerciseEntry fetchEntryByIndex(long rowId) {
        Cursor cursor = database.query(ExerciseEntryDBHelper.ENTRY_TABLE_NAME,
                allColumns, ExerciseEntryDBHelper.COLUMN_ID + " = " + rowId, null,
                null, null, null);
        cursor.moveToFirst();
        ExerciseEntry entry = cursorToEntry(cursor);

        // Log the entry stored
        Log.d(TAG, "entry = " + cursorToEntry(cursor).toString()
                + " insert ID = " + rowId);

        cursor.close();

        return entry;
    }

    public void deleteExerciseEntry(ExerciseEntry exerciseEntry) {
        long rowId = exerciseEntry.getId();
        this.deleteExerciseEntry(rowId);
    }

    public void deleteAllExerciseEntries() {
        database.delete(ExerciseEntryDBHelper.ENTRY_TABLE_NAME, null, null);
    }

    public List<ExerciseEntry> getAllEntries() {
        List<ExerciseEntry> entries = new ArrayList<ExerciseEntry>();
        Cursor cursor = database.query(ExerciseEntryDBHelper.ENTRY_TABLE_NAME,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            ExerciseEntry entry = cursorToEntry(cursor);
            entry.setContext(this.con);
            Log.d(TAG, "get entry = " + cursorToEntry(cursor).toString());
            entries.add(entry);
            cursor.moveToNext();
        }
        // Make sure to close the cursor
        cursor.close();
        return entries;
    }

    /*
    the orm helper, translates everything to
     */
    private ExerciseEntry cursorToEntry(Cursor cursor){

        ExerciseEntry entry = new ExerciseEntry(this.con);
        entry.setContext(this.con);
        entry.setId(cursor.getLong(0));
        entry.setmInputType(cursor.getInt(1));
        entry.setmActivityType(cursor.getInt(2));

        long epoch = cursor.getLong(3);
        Date date = new Date(epoch);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        entry.setmDateTime(cal);
        entry.setmDuration(cursor.getInt(4));
        entry.setmDistance(cursor.getDouble(5));
        entry.setmAvgPace(cursor.getDouble(6));
        entry.setmAvgSpeed(cursor.getDouble(7));
        entry.setmCalorie(cursor.getInt(8));
        entry.setmClimb(cursor.getDouble(9));
        entry.setmHeartRate(cursor.getInt(10));
        entry.setmComment(cursor.getString(11));
        byte[] locationBlob = cursor.getBlob(12);

        ByteArrayInputStream bais = new ByteArrayInputStream(locationBlob);
        DataInputStream in = new DataInputStream(bais);
        ArrayList<LatLng> locs = new ArrayList<>();

        try {
            while (in.available() > 0) {
                String inTemp = in.readUTF();
                String[] splittedTemp = inTemp.split("$");

                LatLng lt = new LatLng(Double.parseDouble(splittedTemp[0]), Double.parseDouble(splittedTemp[1]));
                locs.add(lt);
            }

            entry.setmLocationList(locs);
        }catch (IOException e){

        }

        return entry;
    }
}