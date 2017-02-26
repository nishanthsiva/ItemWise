package com.wolfsoft.teammeetingschedule;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.wolfsoft.teammeetingschedule.utilities.BatteryChargeDAO;
import com.wolfsoft.teammeetingschedule.utilities.DatabaseHelper;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.Buffer;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.LogRecord;

import static android.content.Intent.ACTION_POWER_CONNECTED;
import static android.content.Intent.ACTION_POWER_DISCONNECTED;

/**
 * Created by vijay on 25-02-2017.
 */

public class NotificationService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    private static final int CHECK_BATTERY_INTERVAL = 10000;

    //private GPSTracker gps;
    private double batteryLevel;
    private Handler handler;
    public int onStartCommand(Intent intent,int flags, int startID) {
            return START_STICKY;
        }
        //System.out.println("Status:"+status);


    public void onCreate()
    {
        super.onCreate();
           //nTimer=new Timer();

        //nTimer.schedule(timerTask,2000,2*1000);
        handler = new Handler();
        //handler.postDelayed(checkBatteryStatusRunnable, CHECK_BATTERY_INTERVAL);
        registerReceiver(batInfoReceiver,
                new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

    }

    private boolean isSameChargingState(String currentState){
        try {
            FileInputStream fis = openFileInput("chargingstatus.txt");
            String data = "";
            while(fis.available() > 0){
                byte input[] = new byte[1];
                 input[0] = (byte) fis.read();
                data += new String(input);
            }
            System.out.println("File data "+data);
            System.out.println("input data "+currentState);
            if(data.startsWith(currentState))
                return true;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }


        return false;


    }

    private void addNewChargingData(Date date, int charge){
        String currentState = "charging";
        if(!isSameChargingState(currentState)){
            System.out.println("Battery is charging..");
            DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
            BatteryChargeDAO dao = new BatteryChargeDAO(dbHelper.getWritableDatabase());
            long rowID = dao.insert(date,charge);
            saveChargingState(currentState+","+rowID);
        }

    }

    private void saveChargingState(String s) {
        String FILENAME = "chargingstatus.txt";

        FileOutputStream fos = null;
        try {
            fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
            fos.write(s.getBytes());
            fos.close();
            System.out.println("writing "+s+" to file.");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    private long getExistingRecordID(){
        try {
            FileInputStream fis = openFileInput("chargingstatus.txt");
            String data = "";
            while(fis.available() > 0){
                byte input[] = new byte[1];
                input[0] = (byte) fis.read();
                data += new String(input);
            }
            return Long.parseLong(data.split(",")[1]);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1L;
    }

    private void updateExistingChargingData(Date date, int charge){
        String currentState = "discharging";
        if(!isSameChargingState(currentState)){
            System.out.println("Battery is discharging..");
            long rowID = getExistingRecordID();
            DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
            BatteryChargeDAO dao = new BatteryChargeDAO(dbHelper.getWritableDatabase());
            dao.update(date,charge, rowID);
            saveChargingState(currentState);
        }
    }


    private BroadcastReceiver batInfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent batteryIntent) {


            int status = batteryIntent.getIntExtra(BatteryManager.EXTRA_STATUS,-1);
            int rawlevel = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            if (rawlevel >= 0 && scale > 0) {
                batteryLevel = (rawlevel * 100.0) / scale;
            }
            switch(status){
                case BatteryManager.BATTERY_STATUS_FULL:
                    //System.out.println("Battery Full.");
                    updateExistingChargingData(new Date(System.currentTimeMillis()), (int)batteryLevel);
                    break;
                case BatteryManager.BATTERY_STATUS_CHARGING:
                    //System.out.println("Battery Charging.");
                    addNewChargingData(new Date(System.currentTimeMillis()), (int)batteryLevel);
                    break;
                case BatteryManager.BATTERY_STATUS_DISCHARGING:
                    //System.out.println("Battery Discharging.");
                    updateExistingChargingData(new Date(System.currentTimeMillis()), (int)batteryLevel);
                    break;

            }

            String FILENAME = "charge.txt";

            FileOutputStream fos = null;
            try {
                fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
                fos.write((batteryLevel+"").getBytes());
                fos.close();
                System.out.println("writing "+batteryLevel+" to file.");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            FILENAME = "processes.txt";
            fos = null;
            try {
                fos = openFileOutput(FILENAME, Context.MODE_APPEND);
                ActivityManager am = (ActivityManager)
                        getSystemService(Activity.ACTIVITY_SERVICE);

                for(ActivityManager.RunningTaskInfo taskInfo : am.getRunningTasks(2)){
                    String packageName = taskInfo.topActivity.getPackageName();
                    packageName = packageName.substring(packageName.lastIndexOf(".")+1,packageName.length());
                    fos.write((packageName+"\n").getBytes());
                }
                fos.close();
                System.out.println("writing "+batteryLevel+" to file.");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Log.e("Battery level is", batteryLevel + "mm");
        }
    };
    public static boolean isPlugged(Context context) {
        boolean isPlugged= false;
        Intent intent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        isPlugged = plugged == BatteryManager.BATTERY_PLUGGED_AC || plugged == BatteryManager.BATTERY_PLUGGED_USB;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
            isPlugged = isPlugged || plugged == BatteryManager.BATTERY_PLUGGED_WIRELESS;
        }
        return isPlugged;
    }

    /*private Runnable checkBatteryStatusRunnable = new Runnable() {
        @Override
        public void run() {
            //DO WHATEVER YOU WANT WITH LATEST BATTERY LEVEL STORED IN batteryLevel HERE...

            // schedule next battery check

            handler.postDelayed(checkBatteryStatusRunnable, CHECK_BATTERY_INTERVAL);
            Log.e("Battery status is", batteryLevel + "mm cached");
        }
    };
    */


    /*private Timer nTimer;
    TimerTask timerTask=new TimerTask() {
        @Override
        public void run() {
            Log.e("Log","Running");
            batteryLevel();
        }
    };
    */

    public void onDestroy()
    {
        unregisterReceiver(batInfoReceiver);
       // handler.removeCallbacks(checkBatteryStatusRunnable);
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    /*
    public void notifiy()
    {
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction("RSSPullService");

        Intent myIntent=new Intent(Intent.ACTION_VIEW, Uri.parse(""));
        PendingIntent pendingIntent=PendingIntent.getActivity(getBaseContext(),0,myIntent,Intent.FLAG_ACTIVITY_NEW_TASK);
        Context context=getApplicationContext();

        Notification.Builder builder;
        builder=new Notification.Builder(context)
                .setContentTitle("Background Service")
                .setContentText("Notification for the sound service")
                .setContentIntent(pendingIntent)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.batterywise_logo);
        Notification notification=builder.build();

        NotificationManager notificationManager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1,notification);

    }
    */


    private void batteryLevel() {
        BroadcastReceiver batteryLevelReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                //context.unregisterReceiver(this);
                int rawlevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                int level = -1;
                if (rawlevel >= 0 && scale > 0) {
                    level = (rawlevel * 100) / scale;
                }
                Log.e("Battery status is:", level + "mm");
            }
        };
        IntentFilter batteryLevelFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(batteryLevelReceiver, batteryLevelFilter);
        return;
    }
}

