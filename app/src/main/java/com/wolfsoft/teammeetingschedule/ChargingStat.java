package com.wolfsoft.teammeetingschedule;

import android.database.sqlite.SQLiteDatabase;

import com.wolfsoft.teammeetingschedule.utilities.BatteryChargeDAO;
import com.wolfsoft.teammeetingschedule.utilities.DatabaseHelper;

/**
 * Created by visha on 25-Feb-17.
 */

public class ChargingStat {


    public float ImportStat(int stat, SQLiteDatabase db)
    {
        int i=0,dc=0,rc=0;
        float discharge_r=0,charge_r=0;
        long start_t=0,stop_t=0;
        int start_c = 0, stop_c = 0;

        BatteryChargeDAO BatteryStat= new BatteryChargeDAO(db);
        String StatData[] =BatteryStat.getData();

        while(i<(StatData.length-2)){
            start_t=Long.parseLong(StatData[i]);
            i++;
            start_c=Integer.parseInt(StatData[i]);
            i++;
            if((i+2)<StatData.length ){
                if(i>2){
                    discharge_r=discharge_r+ChargeChange(stop_t,stop_c,start_t,start_c);
                    dc++;
                }
                stop_t= Long.parseLong(StatData[i]);
                i++;
                stop_c=Integer.parseInt(StatData[i]);
                i++;
                charge_r=charge_r+ChargeChange(start_t,start_c,stop_t,stop_c);
                rc++;
            }else{
                discharge_r=discharge_r+ChargeChange(stop_t,stop_c,start_t,start_c);
                dc++;
            }
        }
        if(stat==0) {
            discharge_r = discharge_r / dc;
            return discharge_r;
        }else{
        charge_r=charge_r/rc;
        return charge_r;
        }

    }
    public float ChargeChange(long start_t,int start_c,long stop_t, int stop_c){
        float rate;
        rate=(float)(start_c-stop_c)/(float)(start_t-stop_t);
    return rate;
    }
}
