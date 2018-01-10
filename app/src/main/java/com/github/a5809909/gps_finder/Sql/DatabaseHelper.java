package com.github.a5809909.gps_finder.Sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.github.a5809909.gps_finder.Model.LocationModel;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {   // Database Version

    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "LocationDataBase.db";

    // User table name
    private static final String TABLE_NAME = "LocationTable";

    // User Table Columns names
    private static final String COLUMN_ID = "_id";
    public static final String COLUMN_DAY_AND_TIME = "dayAndTime";
    public static final String COLUMN_JSON_STRING = "jsonString";
    public static final String COLUMN_LAT = "lat";
    public static final String COLUMN_LNG = "lng";
    public static final String COLUMN_ACCURACY = "accuracy";
    public static final String COLUMN_ADDRESS = "address";

    private String CREATE_PHONE_STATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            COLUMN_DAY_AND_TIME + " TEXT," +
            COLUMN_JSON_STRING + " TEXT," +
            COLUMN_LAT + " TEXT," +
            COLUMN_LNG + " TEXT," +
            COLUMN_ACCURACY + " TEXT," +
            COLUMN_ADDRESS + " TEXT" + ")";

    private String DROP_PHONE_STATE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

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

    public void addUser(LocationModel pLocationModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DAY_AND_TIME, pLocationModel.getDateAndTime());
        values.put(COLUMN_JSON_STRING, pLocationModel.getJson_first());
        values.put(COLUMN_LAT, pLocationModel.getLat());
        values.put(COLUMN_LNG, pLocationModel.getLng());
        values.put(COLUMN_ACCURACY, pLocationModel.getAcc());
        values.put(COLUMN_ADDRESS, pLocationModel.getAddress());

        // Inserting Row
        db.insert(TABLE_NAME, null, values);
        db.close();

        Log.i("111", "addUser: ");
    }

    public Cursor getAllItems() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, null);
        Log.i("cc", "getAllItems: " + cursor.getPosition());
        return cursor;
    }

    public List<LocationModel> getAllLocationModels() {
        // array of columns to fetch
        String[] columns = {
                COLUMN_ID,
                COLUMN_DAY_AND_TIME,
                COLUMN_LAT,
                COLUMN_LNG,
                COLUMN_ACCURACY,
                COLUMN_ADDRESS
        };
        // sorting orders
        String sortOrder =
                COLUMN_DAY_AND_TIME + " ASC";
        List<LocationModel> pLocationModelList = new ArrayList<LocationModel>();

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME, //Table to query
                columns,    //columns to return
                null,        //columns for the WHERE clause
                null,        //The values for the WHERE clause
                null,       //group the rows
                null,       //filter by row groups
                sortOrder); //The sort order

        // Traversing through all rows and adding to list
        LocationModel pLocationModel = new LocationModel();
        if (cursor.moveToFirst()) {
            do {
                pLocationModel.set_id(Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_ID))));
                pLocationModel.setDateAndTime(cursor.getString(cursor.getColumnIndex(COLUMN_DAY_AND_TIME)));
                pLocationModel.setLat(cursor.getString(cursor.getColumnIndex(COLUMN_LAT)));
                pLocationModel.setLng(cursor.getString(cursor.getColumnIndex(COLUMN_LNG)));
                pLocationModel.setAcc(cursor.getString(cursor.getColumnIndex(COLUMN_ACCURACY)));
                pLocationModel.setAddress(cursor.getString(cursor.getColumnIndex(COLUMN_ADDRESS)));
                // Adding pLocationModel record to list
                pLocationModelList.add(pLocationModel);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        // return pLocationModel list
        return pLocationModelList;
    }

    public void updateUser(LocationModel pLocationModel) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_LAT, pLocationModel.getLat());
        values.put(COLUMN_LNG, pLocationModel.getLng());
        values.put(COLUMN_ACCURACY, pLocationModel.getAcc());
        values.put(COLUMN_ADDRESS, pLocationModel.getAddress());

        // updating row
        db.update(TABLE_NAME, values, COLUMN_ID + " = ?",
                new String[]{String.valueOf(pLocationModel.get_id())});
        db.close();
    }

    /**
     * This method is to delete pLocationModel record
     *
     * @param pLocationModel
     */
    public void deleteLocationModel(LocationModel pLocationModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        // delete pLocationModel record by id
        db.delete(TABLE_NAME, COLUMN_ID + " = ?",
                new String[]{String.valueOf(pLocationModel.get_id())});
        db.close();
    }

}
