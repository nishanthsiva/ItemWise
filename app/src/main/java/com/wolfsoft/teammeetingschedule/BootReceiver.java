package com.wolfsoft.teammeetingschedule;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.util.Log;

/**
 * Created by vijay on 25-02-2017.
 */

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {


        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            //int batteryLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
            Intent serviceLauncher = new Intent(context, NotificationService.class);

            context.startService(serviceLauncher);
                    }    }
}
