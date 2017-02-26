package com.wolfsoft.teammeetingschedule.utilities;

import android.app.usage.UsageEvents;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.support.annotation.RequiresApi;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import java.util.Date;
import java.util.List;

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


    public String[] getData(){

        int charge;

        String[] projection = {"START_TIME","START_CHARGE","STOP_TIME","STOP_CHARGE"};

        Cursor c = db.query("BATTERY_CHARGE",projection,null,null,null,null,null,null);

        boolean stat = c.moveToFirst();

        List<String> data = new ArrayList<>();

        if (c.moveToFirst()) {
            try{
            do {

                    String start_t = dateFormat.parse(c.getString(0)).getTime()+"";
                    String start_c  = c.getString(1);
                    String stop_t = "0";
                    if(c.getString(2) != null)
                         stop_t = dateFormat.parse(c.getString(2)).getTime()+"";
                    String stop_c = "0";
                    if(c.getString(3) != null)
                        stop_c = c.getString(3);
                    System.out.println(start_t+" "+start_c+" "+stop_t+" "+stop_c);
                    data.add(start_t);
                    data.add(start_c);
                    data.add(stop_t);
                    data.add(stop_c);

            } while (c.moveToNext());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        c.close();
        return data.toArray(new String[0]);

    }

}
