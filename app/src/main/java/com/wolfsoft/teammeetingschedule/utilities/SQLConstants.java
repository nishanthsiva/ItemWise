package com.wolfsoft.teammeetingschedule.utilities;

/**
 * Created by vijay on 25-02-2017.
 */

public class SQLConstants {

    public static final String CREATE_BATTERY_CHARGE_TABLE = "CREATE TABLE BATTERY_CHARGE (B_ID INTEGER PRIMARY KEY AUTOINCREMENT,START_TIME DATETIME, START_CHARGE INTEGER, STOP_TIME DATETIME,STOP_CHARGE INTEGER)";
    public static final String DELETE_BATTERY_CHARGE_TABLE = "DELETE FROM BATTERY_CHARGE";
}
