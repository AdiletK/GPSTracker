package com.webrand.gpstracker.db;

import android.provider.BaseColumns;

public class Columns {


   public static final class TrackerColumns {
       public static final  String tableName = "TrackInfo";
        static final class Cols implements BaseColumns {
            static final String COLUMN_ID = "id";
            static final String COLUMN_START_LATITUDE = "start_lat";
            static final String COLUMN_START_LONGITUDE = "start_long";
            static final String COLUMN_END_LATITUDE = "end_lat";
            static final String COLUMN_END_LONGITUDE = "end_long";
            static final String COLUMN_TIME = "time";
            static final String COLUMN_DATE = "date";
            static final String COLUMN_DISTANCE= "distance";
        }
    }
}
