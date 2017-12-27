package com.github.a5809909.gps_finder.MySQL;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.github.a5809909.gps_finder.Model.PhoneState;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper{   // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "PhoneStateManager.db";

    // User table name
    private static final String TABLE_PHONE_STATE = "phoneState";

    // User Table Columns names
    private static final String COLUMN_ID = "_id";
    public static final String COLUMN_TIME = "time";
    public static final String COLUMN_JSON_STRING = "jsonString";
    public static final String COLUMN_LAT = "lat";
    public static final String COLUMN_LNG = "lng";
    public static final String COLUMN_ACCURACY = "accuracy";



    private String CREATE_PHONE_STATE_TABLE = "CREATE TABLE " + TABLE_PHONE_STATE + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            COLUMN_TIME + " TEXT,"+
            COLUMN_JSON_STRING + " TEXT," +
            COLUMN_LAT + " TEXT," +
            COLUMN_LNG + " TEXT," +
            COLUMN_ACCURACY + " TEXT" + ")";

    private String DROP_PHONE_STATE_TABLE = "DROP TABLE IF EXISTS " + TABLE_PHONE_STATE;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_PHONE_STATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        //Drop User Table if exist
        db.execSQL(DROP_PHONE_STATE_TABLE);

        // Create tables again
        onCreate(db);
    }




    public void addUser(PhoneState phoneState) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TIME, phoneState.getTime());
        values.put(COLUMN_JSON_STRING, phoneState.getJsonString());
        values.put(COLUMN_LAT, phoneState.getLat());
        values.put(COLUMN_LNG, phoneState.getLng());
        values.put(COLUMN_ACCURACY, phoneState.getAccuracy());

        // Inserting Row
        db.insert(TABLE_PHONE_STATE, null, values);
        db.close();
    }

    /**
     * This method is to fetch all phoneState and return the list of phoneState records
     *
     * @return list
     */

    public Cursor getAllItems() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_PHONE_STATE, null, null, null, null, null, null);
        Log.i("cc", "getAllItems: "+cursor.getPosition());
        return cursor;
    }

    public List<PhoneState> getAllPhoneStates() {
        // array of columns to fetch
        String[] columns = {
                COLUMN_ID,
                COLUMN_TIME,
                COLUMN_JSON_STRING,
                COLUMN_LAT,
                COLUMN_LNG,
                COLUMN_ACCURACY,
        };
        // sorting orders
        String sortOrder =
                COLUMN_TIME + " ASC";
        List<PhoneState> phoneStateList = new ArrayList<PhoneState>();

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_PHONE_STATE, //Table to query
                columns,    //columns to return
                null,        //columns for the WHERE clause
                null,        //The values for the WHERE clause
                null,       //group the rows
                null,       //filter by row groups
                sortOrder); //The sort order

        // Traversing through all rows and adding to list
        PhoneState phoneState = new PhoneState();
        if (cursor.moveToFirst()) {
            do {
                phoneState.set_id(Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_ID))));
                phoneState.setTime(cursor.getString(cursor.getColumnIndex(COLUMN_TIME)));
                phoneState.setJsonString(cursor.getString(cursor.getColumnIndex(COLUMN_JSON_STRING)));
                phoneState.setLat(cursor.getString(cursor.getColumnIndex(COLUMN_LAT)));
                phoneState.setLng(cursor.getString(cursor.getColumnIndex(COLUMN_LNG)));
                phoneState.setAccuracy(cursor.getString(cursor.getColumnIndex(COLUMN_ACCURACY)));
                // Adding phoneState record to list
                phoneStateList.add(phoneState);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        // return phoneState list
        return phoneStateList;
    }

//    public void updateUser(PhoneState phoneState) {
//        SQLiteDatabase db = this.getWritableDatabase();
//
//        ContentValues values = new ContentValues();
//        values.put(COLUMN_LAC, phoneState.getMnc());
//        values.put(COLUMN_JSON_STRING, phoneState.getMcc());
//        values.put(COLUMN_LAT, phoneState.getLac());
//
//        // updating row
//        db.update(TABLE_PHONE_STATE, values, COLUMN_ID + " = ?",
//                new String[]{String.valueOf(phoneState.get_id())});
//        db.close();
//    }

    /**
     * This method is to delete phoneState record
     *
     * @param phoneState
     */
    public void deletePhoneState(PhoneState phoneState) {
        SQLiteDatabase db = this.getWritableDatabase();
        // delete phoneState record by id
        db.delete(TABLE_PHONE_STATE, COLUMN_ID + " = ?",
                new String[]{String.valueOf(phoneState.get_id())});
        db.close();
    }

}
