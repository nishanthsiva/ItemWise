package com.wolfsoft.teammeetingschedule;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.wolfsoft.teammeetingschedule.utilities.DatabaseHelper;

import java.util.Calendar;

import static java.lang.Thread.sleep;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button t_in, t_out, submit_btn;
    private int mHour, mMinute;
    EditText off, hom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        t_in = (Button) findViewById(R.id.v_in);
        t_out= (Button) findViewById(R.id.v_out);
        submit_btn = (Button) findViewById(R.id.submit);

        off = (EditText) findViewById(R.id.off_address);
        hom = (EditText) findViewById(R.id.hom_address);

        t_in.setOnClickListener(this);
        t_out.setOnClickListener(this);
        submit_btn.setOnClickListener(this);
        startService(new Intent(this, NotificationService.class));

        //testing charging stats
        /*ChargingStat stat = new ChargingStat();
        DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
        System.out.println(stat.ImportStat(1, dbHelper.getWritableDatabase()));*/

    }

    @Override
    public void onClick(View v) {

        if(v == t_in) {
            final Calendar c = Calendar.getInstance();
            mHour = c.get(Calendar.HOUR_OF_DAY);
            mMinute = c.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                    new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay,
                                              int minute) {
                                t_in.setText(hourOfDay + ":" + minute);
                        }
                    }, mHour, mMinute, false);
            timePickerDialog.show();
        }

        if(v == t_out) {
            final Calendar c = Calendar.getInstance();
            mHour = c.get(Calendar.HOUR_OF_DAY);
            mMinute = c.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                    new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay,
                                              int minute) {
                            t_out.setText(hourOfDay + ":" + minute);
                        }
                    }, mHour, mMinute, false);
            timePickerDialog.show();
        }


        if(v == submit_btn){

            android.support.v4.app.NotificationCompat.Builder mBuilder =
                    new android.support.v4.app.NotificationCompat.Builder(this)
                            .setSmallIcon(R.drawable.bell)
                            .setContentTitle("Battery Wise")
                            .setContentText("Conserve battery life. Tap to know how.");

            Intent resultIntent = new Intent(this, Home.class);

            PendingIntent resultPendingIntent =
                    PendingIntent.getActivity(
                            this,
                            0,
                            resultIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );

            mBuilder.setAutoCancel(true);
            mBuilder.setContentIntent(resultPendingIntent);


            // Sets an ID for the notification
            int mNotificationId = 001;
// Gets an instance of the NotificationManager service
            NotificationManager mNotifyMgr =
                    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
// Builds the notification and issues it.
            mNotifyMgr.notify(mNotificationId, mBuilder.build());

            new AlertDialog.Builder(this)
                    .setTitle("Thank You")
                    .setMessage("Now, leave the rest to me :)")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // finish the activity here or,
                            // redirect to another activity
                            finish();
                            System.exit(0);
                        }
                    })
                    .show();



        }

    }
}
