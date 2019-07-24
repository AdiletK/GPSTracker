package com.webrand.gpstracker.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.webrand.gpstracker.db.Columns.TrackerColumns;
import com.webrand.gpstracker.models.TrackerInfo;

import java.util.ArrayList;

public class DatabaseOperation {
    private static  DatabaseOperation sOperation;

    private SQLiteDatabase mDatabase;

    private DatabaseOperation(Context context) {
        Context context1 = context.getApplicationContext();
        mDatabase = new CreationDatabase(context1)
                .getWritableDatabase();
    }

    public static DatabaseOperation get(Context context){
        if (sOperation==null){
            sOperation = new DatabaseOperation(context);
        }
        return sOperation;
    }

//    //operations with Table Track
    public long add(TrackerInfo data){
        ContentValues values = getValues(data);
        return addOperation(values, TrackerColumns.tableName);
    }

    public void delete(TrackerInfo data){
        deleteOperation(TrackerColumns.tableName,
                TrackerColumns.Cols.COLUMN_ID,data.getId());
    }

    public void update(TrackerInfo data){
        ContentValues values = getValues(data);
        updateOperation(TrackerColumns.tableName, TrackerColumns.Cols.COLUMN_ID,String.valueOf(data.getId()), values);
    }

    public ArrayList<TrackerInfo> getListOfTrackers(){
        ArrayList<TrackerInfo> trackers = new ArrayList<>();
        try (CursorWrapperHelper cursor = query(TrackerColumns.tableName,
                null, null,null)) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                trackers.add(cursor.getTracker());
                cursor.moveToNext();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return trackers;
    }

    public TrackerInfo getTrack(int id){
        try (CursorWrapperHelper track = query(TrackerColumns.tableName,
                TrackerColumns.Cols.COLUMN_ID + "=?",
                new String[]{String.valueOf(id)},null
        )) {
            if (track.getCount() == 0) {
                return null;
            }
            track.moveToFirst();
            return track.getTracker();
        }
    }

    private static ContentValues getValues(TrackerInfo info){
        ContentValues values = new ContentValues();
        values.put(TrackerColumns.Cols.COLUMN_START_LATITUDE ,info.getStart_latitude());
        values.put(TrackerColumns.Cols.COLUMN_START_LONGITUDE    ,info.getStart_longitude());
        values.put(TrackerColumns.Cols.COLUMN_END_LATITUDE  ,info.getEnd_latitude());
        values.put(TrackerColumns.Cols.COLUMN_END_LONGITUDE, info.getEnd_longitude());
        values.put(TrackerColumns.Cols.COLUMN_TIME     ,info.getTime());
        values.put(TrackerColumns.Cols.COLUMN_DATE     ,info.getDate());
        values.put(TrackerColumns.Cols.COLUMN_DISTANCE     ,info.getDistance());

        return values;
    }

    private void deleteOperation(String table,String whereClause, int id) {
        mDatabase.delete(table, whereClause + "=?",
                new String[]{String.valueOf(id)});
    }

    private long addOperation(ContentValues values, String name) {
        return mDatabase.insert(name, null, values);
    }

    private void updateOperation(String table,String whereClause,String uuid, ContentValues values) {
        mDatabase.update(table,values,
                whereClause + "=?",
                new String[]{uuid});
    }

    private CursorWrapperHelper query(String table, String whereClause,
                                      String[] whereArgs,String orderby){
        Cursor cursor = mDatabase.query(
                table,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                orderby+" DESC"
        );
        return new CursorWrapperHelper(cursor);
    }
}
