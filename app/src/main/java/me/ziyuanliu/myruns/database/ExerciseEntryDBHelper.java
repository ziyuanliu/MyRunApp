package me.ziyuanliu.myruns.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by ziyuanliu on 4/23/16.
 *
 */
public class ExerciseEntryDBHelper extends SQLiteOpenHelper{
    public static final String ENTRY_TABLE_NAME = "ENTRIES";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_INPUT_TYPE = "input_type";
    public static final String COLUMN_ACTIVITY_TYPE = "activity_type";
    public static final String COLUMN_DATE_TIME = "date_time";
    public static final String COLUMN_DURATION = "duration";
    public static final String COLUMN_DISTANCE = "distance";
    public static final String COLUMN_AVG_PACE = "avg_pace";
    public static final String COLUMN_AVG_SPEED = "avg_speed";
    public static final String COLUMN_CALORIES = "calories";
    public static final String COLUMN_CLIMB = "climb";
    public static final String COLUMN_HEARTRATE = "heartrate";
    public static final String COLUMN_COMMENT = "comment";
    public static final String COLUMN_GPS_DATA = "gps_data";

    private static final String DATABASE_NAME = "myruns.db";
    private static final int DATABASE_VERSION = 1;

    /*
        0 _id INTEGER PRIMARY KEY AUTOINCREMENT,
        input_type INTEGER NOT NULL,
        activity_type INTEGER NOT NULL,
        date_time DATETIME NOT NULL,
        duration INTEGER NOT NULL,
        distance FLOAT,
        avg_pace FLOAT,
        avg_speed FLOAT,
        calories INTEGER,
        climb FLOAT,
        heartrate INTEGER,
        comment TEXT,
        gps_data BLOB
         */

    private static final String DATABASE_SCHEMA = "CREATE TABLE IF NOT EXISTS " +
            ENTRY_TABLE_NAME+ "( "
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_INPUT_TYPE + " INTEGER NOT NULL,"
            + COLUMN_ACTIVITY_TYPE + " INTEGER NOT NULL,"
            + COLUMN_DATE_TIME + " DATETIME NOT NULL,"
            + COLUMN_DURATION + " INTEGER NOT NULL,"
            + COLUMN_DISTANCE + " FLOAT,"
            + COLUMN_AVG_PACE + " FLOAT,"
            + COLUMN_AVG_SPEED + " FLOAT,"
            + COLUMN_CALORIES + " INTEGER,"
            + COLUMN_CLIMB + " FLOAT,"
            + COLUMN_HEARTRATE + " INTEGER,"
            + COLUMN_COMMENT + " TEXT,"
            + COLUMN_GPS_DATA + " BLOB"
            + ");";

    public ExerciseEntryDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_SCHEMA);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(ExerciseEntryDBHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + ENTRY_TABLE_NAME);
        onCreate(db);
    }


}
