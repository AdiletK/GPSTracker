package com.webrand.gpstracker.db;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.webrand.gpstracker.models.TrackerInfo;

import static com.webrand.gpstracker.db.Columns.TrackerColumns.Cols.*;

public class CursorWrapperHelper extends CursorWrapper {

    CursorWrapperHelper(Cursor cursor) {
        super(cursor);
    }

    TrackerInfo getTracker(){
        int id  = getInt(getColumnIndex(COLUMN_ID));
        double start_latitude = getDouble(getColumnIndex(COLUMN_START_LATITUDE));
        double start_longitude = getDouble(getColumnIndex(COLUMN_START_LONGITUDE));
        double end_latitude = getDouble(getColumnIndex( COLUMN_END_LATITUDE));
        double end_longitude = getDouble(getColumnIndex(COLUMN_END_LONGITUDE));
        String start_time = getString(getColumnIndex(COLUMN_TIME));
        String date = getString(getColumnIndex(COLUMN_DATE));
        String distance = getString(getColumnIndex(COLUMN_DISTANCE));

        return new TrackerInfo(id,start_latitude,start_longitude,end_latitude,end_longitude,start_time,date,distance);

    }
}
