package com.wolfsoft.teammeetingschedule;

import android.app.TimePickerDialog;
import android.content.Intent;
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

            Intent redirect = new Intent(this, Home.class);
            startActivity(redirect);
        }

    }
}
