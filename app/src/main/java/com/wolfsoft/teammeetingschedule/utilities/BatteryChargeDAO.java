package com.wolfsoft.teammeetingschedule.utilities;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.icu.text.SimpleDateFormat;

import java.util.Date;

/**
 * Created by vijay on 25-02-2017.
 */

public class BatteryChargeDAO {
    private SQLiteDatabase db;
    private SimpleDateFormat dateFormat;

    public BatteryChargeDAO(SQLiteDatabase db){
        this.db = db;
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }

    public long insert(Date date, int charge){
        ContentValues values = new ContentValues();

        values.put("START_TIME",dateFormat.format(date.getTime()));
        values.put("START_CHARGE",charge);
        long newRowId = db.insert("BATTERY_CHARGE", null, values);

        return newRowId;
    }

    public void update(Date date, int charge, long rowID){
        ContentValues values = new ContentValues();
        values.put("STOP_CHARGE",charge);

        values.put("STOP_TIME",dateFormat.format(date.getTime()));

        long newRowId = db.update("BATTERY_CHARGE",values,"B_ID = ?",new String[]{""+rowID});
    }

}
