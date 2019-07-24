package com.webrand.gpstracker;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.test.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;

import com.webrand.gpstracker.db.Columns;
import com.webrand.gpstracker.db.CreationDatabase;
import com.webrand.gpstracker.db.DatabaseOperation;
import com.webrand.gpstracker.models.TrackerInfo;

import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;

@RunWith(AndroidJUnit4.class)
public class DatabaseTest {
    private final Context mContext = InstrumentationRegistry.getTargetContext();

    private final Class mDbHelperClass = CreationDatabase.class;
    DatabaseOperation databaseOperation = DatabaseOperation.get(mContext);


    @Test
    public void create_database_test() throws Exception{


        SQLiteOpenHelper dbHelper =
                (SQLiteOpenHelper) mDbHelperClass.getConstructor(Context.class).newInstance(mContext);

        SQLiteDatabase database = dbHelper.getWritableDatabase();

        /* We think the database is open, let's verify that here */
        String databaseIsNotOpen = "The database should be open and isn't";
        assertTrue(databaseIsNotOpen, database.isOpen());

        /* This Cursor will contain the names of each table in our database */
        Cursor tableNameCursor = database.rawQuery(
                "SELECT name FROM sqlite_master WHERE type='table' AND name='" +
                        Columns.TrackerColumns.tableName + "'",
                null);

        /*
         * If tableNameCursor.moveToFirst returns false from this query, it means the database
         * wasn't created properly. In actuality, it means that your database contains no tables.
         */
        String errorInCreatingDatabase =
                "Error: This means that the database has not been created correctly";
        assertTrue(errorInCreatingDatabase,
                tableNameCursor.moveToFirst());

        /* If this fails, it means that your database doesn't contain the expected table(s) */
        assertEquals("Error: Your database was created without the expected tables.",
                Columns.TrackerColumns.tableName, tableNameCursor.getString(0));

        /* Always close a cursor when you are done with it */
        tableNameCursor.close();
    }


    @Test
    public void insert_single_record_test() throws Exception{

        /* Use reflection to try to run the correct constructor whenever implemented */
        SQLiteOpenHelper dbHelper =
                (SQLiteOpenHelper) mDbHelperClass.getConstructor(Context.class).newInstance(mContext);

        SQLiteDatabase database = dbHelper.getWritableDatabase();

        TrackerInfo testValues =  new TrackerInfo(1.0,1.0,1.0,1.0,"12:00","19.07.2019","10");

        long firstRowId = databaseOperation.add(testValues);

        /* Insert ContentValues into database and get first row ID back */


        /* If the insert fails, database.insert returns -1 */
        assertNotEquals("Unable to insert into the database", -1, firstRowId);

        /*
         * Query the database and receive a Cursor. A Cursor is the primary way to interact with
         * a database in Android.
         */
        Cursor wCursor = database.query(
                Columns.TrackerColumns.tableName,
                null,
                null,
                null,
                null,
                null,
                null);

        /* Cursor.moveToFirst will return false if there are no records returned from your query */
        String emptyQueryError = "Error: No Records returned from waitlist query";
        assertTrue(emptyQueryError,
                wCursor.moveToFirst());

        /* Close cursor and database */
        wCursor.close();
        dbHelper.close();
    }


    @Test
    public void upgrade_database_test() throws Exception{

        /* Insert 2 rows before we upgrade to check that we dropped the database correctly */

        /* Use reflection to try to run the correct constructor whenever implemented */
        SQLiteOpenHelper dbHelper =
                (SQLiteOpenHelper) mDbHelperClass.getConstructor(Context.class).newInstance(mContext);

        SQLiteDatabase database = dbHelper.getWritableDatabase();

        TrackerInfo testValues =  new TrackerInfo(1.0,1.0,1.0,
                1.0,"12:00","19.07.2019","10");


        long firstRowId = databaseOperation.add(testValues);

        /* Insert ContentValues into database and get another row ID back */
        long secondRowId = databaseOperation.add(testValues);

        dbHelper.onUpgrade(database, 0, 1);

        database = dbHelper.getReadableDatabase();

        /* This Cursor will contain the names of each table in our database */
        Cursor tableNameCursor = database.rawQuery(
                "SELECT name FROM sqlite_master WHERE type='table' AND name='" +
                        Columns.TrackerColumns.tableName + "'",
                null);

        assertEquals(1, tableNameCursor.getCount());

        /*
         * Query the database and receive a Cursor. A Cursor is the primary way to interact with
         * a database in Android.
         */
        Cursor wCursor = database.query(
                Columns.TrackerColumns.tableName,
                null,
                null,
                null,
                null,
                null,
                null);

        /* Cursor.moveToFirst will return false if there are no records returned from your query */

        assertFalse("Database doesn't seem to have been dropped successfully when upgrading",
                wCursor.moveToFirst());

        tableNameCursor.close();
        database.close();
    }
}
