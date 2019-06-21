package com.example.jnubus;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {


    private static String DB_PATH = "";
    private static String DB_NAME = "jnubus.db";
    private SQLiteDatabase myDataBase;
    private final Context myContext;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, 1);
        myContext = context;
        DB_PATH = myContext.getDatabasePath(DB_NAME).toString();
    }

    public boolean checkDatabase (){
        File dbFile = myContext.getDatabasePath(DB_NAME);
        return dbFile.exists();
    }

    public void copyDataBase() throws IOException {
        InputStream myInput = myContext.getAssets().open(DB_NAME);
        OutputStream myOutput = new FileOutputStream(DB_PATH);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer))>0)myOutput.write(buffer, 0, length);
        myOutput.flush();
        myOutput.close();
        myInput.close();
    }

    public int countTables() {
        openDataBase();
        int count = 0;
        Cursor cursor = myDataBase.rawQuery("SELECT * FROM sqlite_master WHERE type='table'", null);
        cursor.moveToFirst();
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            count++;
            getReadableDatabase().close();
        }
        cursor.close();
        closeDatabase();
        return count;
    }



    public void deleteTable (String table){
        myDataBase.execSQL("delete from "+ table);
    }




    public synchronized void openDataBase() throws SQLException {
        myDataBase = SQLiteDatabase.openDatabase(DB_PATH, null, SQLiteDatabase.OPEN_READWRITE);
    }

    public synchronized void closeDatabase() {
        if(myDataBase != null) myDataBase.close();
        super.close();

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }


    public List<String> getListPlaces() {
        List<String> places = new ArrayList<>();
        String place = null;
        Cursor cursor = myDataBase.rawQuery("SELECT name FROM places", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            place  = cursor.getString(0);
            places.add(place);
            cursor.moveToNext();
        }
        cursor.close();
        return places;
    }

    public List<Integer> getListRelation() {
        List<Integer> places = new ArrayList<>();
        Cursor cursor = myDataBase.rawQuery("SELECT id FROM relation", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            places.add(cursor.getInt(0));
            cursor.moveToNext();
        }
        cursor.close();
        return places;
    }

    public List<String> getStoppage (String busName){
        int busId = getBusByName(busName);
        List<String> stoppage = new ArrayList<>();
        Cursor cursor = myDataBase.rawQuery("SELECT placeid FROM relation WHERE busid = ? ORDER BY waypoints DESC", new String[]{String.valueOf(busId)});
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            stoppage.add(getPlaceById( cursor.getInt(cursor.getColumnIndex("placeid")) ) );
            cursor.moveToNext();
        }
        return stoppage;
    }

    public List<String> getListBuses() {
        List<String> buses = new ArrayList<>();
        Cursor cursor = myDataBase.rawQuery("SELECT name FROM buses ORDER BY name ASC", new String[]{});
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            buses.add(cursor.getString(0));
            cursor.moveToNext();
        }
        cursor.close();
        return buses;
    }

    public List<String> getListBuses (int placeId) {
        List<String> buses = new ArrayList<>();
        Cursor cursor = myDataBase.rawQuery("SELECT busid FROM relation WHERE placeid = ?", new String[]{ String.valueOf(placeId)});
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            int busId = cursor.getInt(0);
            buses.add(getBusById(busId));
            cursor.moveToNext();
        }
        cursor.close();
        return buses;
    }

    public int getPlacesByName(String name) {
        int ans = -1;
        Cursor cursor = myDataBase.rawQuery("SELECT id FROM places WHERE name = ?", new String[]{name});
        if(cursor.getCount() < 1)return -1;
        cursor.moveToFirst();
        ans = cursor.getInt(0);
        cursor.close();
        return ans;
    }

    public int getBusByName(String name) {
        Cursor cursor = myDataBase.rawQuery("SELECT id FROM buses WHERE name = ?", new String[]{name});
        cursor.moveToFirst();
        int ans = cursor.getInt(cursor.getColumnIndex("id"));
        cursor.close();
        return ans;
    }

    public String getBusById(int id) {
        Cursor cursor = myDataBase.rawQuery("SELECT name FROM buses WHERE id = ?", new String[]{String.valueOf(id)});
        cursor.moveToFirst();
        String ans = cursor.getString(0);
        cursor.close();
        return ans;
    }

    public String getPlaceById(int id) {
        Cursor cursor = myDataBase.rawQuery("SELECT name FROM places WHERE id = ?", new String[]{String.valueOf(id)});
        cursor.moveToFirst();
        String ans = cursor.getString(0);
        cursor.close();
        return ans;
    }




    public long addPlaces(Place place) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("id", place.getId());
        contentValues.put("name", place.getName());
        long returnValue = myDataBase.insert("places", null, contentValues);
        return returnValue;
    }

    public long addBuses(Bus buses) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("id", buses.getId());
        contentValues.put("name", buses.getName());
        contentValues.put("type", buses.getType());
        long returnValue = myDataBase.insert("buses", null, contentValues);
        return returnValue;
    }

    public long addRelation(Relation relation) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("id", relation.getId());
        contentValues.put("busid", relation.getBusid());
        contentValues.put("placeid", relation.getPlaceid());
        contentValues.put("waypoints", relation.getWaypoints());
        long returnValue = myDataBase.insert("relation", null, contentValues);
        return returnValue;
    }


}
