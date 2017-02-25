package com.wolfsoft.teammeetingschedule;

import android.util.JsonReader;
import android.util.JsonToken;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by visha on 25-Feb-17.
 */

public class Location_Time_Out {

    Map<String, List<Integer>> locationTimeMapEntry;
    Map<String, List<Integer>> locationTimeMapExit;

    public Location_Time_Out() {
        locationTimeMapEntry = new HashMap<>();
        locationTimeMapExit = new HashMap<>();
    }


    public void readJsonStream(InputStream in) throws Exception {

        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        try {
            readLocation(reader);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }


    public void readLocation(JsonReader reader) throws Exception {

        String name, key = "0,0";
        long coord1;
        int tms = 0,accuracy=10000;
        Date date; //= new Date();
        int count = 0;
        reader.beginObject();
        reader.nextName();
        reader.beginArray();

        reader.beginObject();

        //while (reader.hasNext()){
        while (count < 100) {

            name = reader.nextName();
            //System.out.println(name);
            switch (name) {
                case "timestampMs":

                    date = new Date(reader.nextLong());
                    tms=date.getHours();
                    //System.out.println(tms);
                    break;
                case "latitudeE7":
                    key = reader.nextString();
                    coord1=Long.parseLong(key);
                    coord1=Math.round((double) coord1/accuracy);
                    key=Double.toString(coord1);
                    break;
                case "longitudeE7":
                    coord1=Long.parseLong(reader.nextString());
                    coord1=Math.round((double) coord1/accuracy);
                    key = key + "," + Double.toString(coord1);
                    break;
                default:
                    reader.skipValue();
                    if (reader.peek() == JsonToken.END_OBJECT) {
                        reader.endObject();
                        StoreData(key, tms);
                        reader.beginObject();
                        count = count + 1;
                    }
                    break;
            }
        }
        GetAvg(locationTimeMapEntry);
        GetAvg(locationTimeMapExit);
    }

    public void StoreData(String key, int tms) {

        if (tms>12) {

            if (locationTimeMapEntry.containsKey(key)) {
                locationTimeMapEntry.get(key).add(tms);
            } else {
                locationTimeMapEntry.put(key, new ArrayList<Integer>());
                locationTimeMapEntry.get(key).add(tms);
            }
        }else {
            if (locationTimeMapExit.containsKey(key)) {
                locationTimeMapExit.get(key).add(tms);
            } else {
                locationTimeMapExit.put(key, new ArrayList<Integer>());
                locationTimeMapExit.get(key).add(tms);
            }
        }

    }


    public void GetAvg(Map<String, List<Integer>> datamap) {
        int hashsize = datamap.size(), sum = 0;
        List<Integer> tempList;
        System.out.println(hashsize);
        for (String key : datamap.keySet()) {
            tempList = datamap.get(key);
            System.out.println(key);
            for (int i : tempList) {
                sum += i;
            }
            datamap.get(key).add( (sum / tempList.size()));
        }
    }

    public float GetAvgEntry(String key) {
        int sum = 0;
        List<Integer> tempList;

        tempList = locationTimeMapEntry.get(key);
        for (int i : tempList) {
                sum += i;
        }
        return (float)(sum / tempList.size());
    }
    public float GetAvgExit(String key) {
        int sum = 0;
        List<Integer> tempList;

        tempList = locationTimeMapExit.get(key);
        for (int i : tempList) {
            sum += i;
        }
        return (float)(sum / tempList.size());

    }
}