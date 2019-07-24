package com.webrand.gpstracker.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static com.webrand.gpstracker.db.Columns.TrackerColumns.Cols.COLUMN_DATE;
import static com.webrand.gpstracker.db.Columns.TrackerColumns.Cols.COLUMN_DISTANCE;
import static com.webrand.gpstracker.db.Columns.TrackerColumns.Cols.COLUMN_END_LATITUDE;
import static com.webrand.gpstracker.db.Columns.TrackerColumns.Cols.COLUMN_END_LONGITUDE;
import static com.webrand.gpstracker.db.Columns.TrackerColumns.Cols.COLUMN_ID;
import static com.webrand.gpstracker.db.Columns.TrackerColumns.Cols.COLUMN_START_LATITUDE;
import static com.webrand.gpstracker.db.Columns.TrackerColumns.Cols.COLUMN_START_LONGITUDE;
import static com.webrand.gpstracker.db.Columns.TrackerColumns.Cols.COLUMN_TIME;
import static com.webrand.gpstracker.db.Columns.TrackerColumns.tableName;


public class CreationDatabase extends SQLiteOpenHelper {

    private static final int VERSION = 4;
    private static final String DATABASE_NAME = "gps.db";



    public CreationDatabase(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table " + tableName + " ( " +
                COLUMN_ID + " INTEGER primary key autoincrement, " +
                COLUMN_START_LATITUDE + " , " +
                COLUMN_START_LONGITUDE + " , " +
                COLUMN_END_LATITUDE + "  , " +
                COLUMN_END_LONGITUDE + "  , " +
                COLUMN_TIME + " , " +
                COLUMN_DISTANCE + " , " +
                COLUMN_DATE+ " DATE );"

        );
        Log.e("DATA","Creation DAta");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL(" DROP TABLE IF EXISTS " + tableName);
        onCreate(db);
    }
}
