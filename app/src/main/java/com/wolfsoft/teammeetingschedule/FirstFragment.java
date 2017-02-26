package com.wolfsoft.teammeetingschedule;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Data;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import com.wolfsoft.teammeetingschedule.utilities.BatteryChargeDAO;
import com.wolfsoft.teammeetingschedule.utilities.DatabaseHelper;

import org.w3c.dom.Text;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class FirstFragment extends Fragment{
    public static final String ARG_PAGE = "ARG_PAGE";
    private int mPageNo;

    private View rootView;
    private LayoutInflater rootInflater;
    private static GoogleAccountCredential mCredential;


    public FirstFragment() {
        // Required empty public constructor
    }

    public static FirstFragment newInstance(int pageNo, GoogleAccountCredential mCredential) {

        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, pageNo);
        FirstFragment fragment = new FirstFragment();
        fragment.setArguments(args);
        FirstFragment.mCredential = mCredential;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPageNo = getArguments().getInt(ARG_PAGE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootInflater = inflater;
         rootView = rootInflater.inflate(R.layout.fragment_first, container, false);

//        TextView textView = (TextView) view;
//        textView.setText("Fragment #" + mPageNo);
        //addCalendarItem(inflater,view, "hello world..");
        new FirstFragment.MakeRequestTask(mCredential).execute();
        setBatteryLevels();
        return rootView;
    }

    private void setBatteryLevels(){
        DatabaseHelper dbHelper = new DatabaseHelper(rootView.getContext());
        ChargingStat chargingStat = new ChargingStat();
        double dischargeRate = chargingStat.ImportStat(0,dbHelper.getWritableDatabase());
        dischargeRate = dischargeRate *  60 * 1000; // discharge rate per minute
        if(dischargeRate < 0) dischargeRate *=(-1);
        int currentCharge = getCurrentCharge(rootView.getContext());
        double timeToFullDischarge = currentCharge/dischargeRate;
        TextView batterLifeHrs = (TextView) rootView.findViewById(R.id.battery_life_hrs);
        batterLifeHrs.setText(((int)timeToFullDischarge/60)+"");
        TextView batteryLifeMins = (TextView) rootView.findViewById(R.id.battery_life_mins);
        batteryLifeMins.setText(((int)timeToFullDischarge%60)+"");

        double chargeRate = chargingStat.ImportStat(1,dbHelper.getWritableDatabase());
        System.out.println("Charge rate "+chargeRate+"\nCurrent Charge: "+currentCharge);
        chargeRate = chargeRate *  60*1000; // charge rate per minute
        double timeToFullCharge = (100-currentCharge)/chargeRate;
        TextView chargeTimeHrs = (TextView) rootView.findViewById(R.id.charge_time_hrs);
        chargeTimeHrs.setText(((int)timeToFullCharge/60)+"");
        TextView chargeTimeMins = (TextView) rootView.findViewById(R.id.charge_time_mins);
        chargeTimeMins.setText(((int)timeToFullCharge%60)+"");
    }

    private int getCurrentCharge(Context context){
        try {
            FileInputStream fis = rootView.getContext().openFileInput("charge.txt");
            String data = "";
            while(fis.available() > 0){
                byte input[] = new byte[1];
                input[0] = (byte) fis.read();
                data += new String(input);
            }
            return (int)Float.parseFloat(data);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;

    }

    private void addCalendarItem(LayoutInflater inflater, View fragmentView, CalendarEvent calendarEvent){
        ViewGroup layout = (ViewGroup) fragmentView.findViewById(R.id.item_list);
        View view = inflater.inflate(R.layout.calendar_card_view, layout, false);
        TextView eventName = (TextView) view.findViewById(R.id.event_name);
        eventName.setText(calendarEvent.getEventName());
        TextView eventStatus = (TextView) view.findViewById(R.id.event_status);
        eventStatus.setText(calendarEvent.getEventStatus());
        TextView eventLocation = (TextView) view.findViewById(R.id.event_location);
        eventLocation.setText(calendarEvent.getEventLocation());
        layout.addView(view);

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    /**
     * An asynchronous task that handles the Google Calendar API call.
     * Placing the API calls in their own task ensures the UI stays responsive.
     */
    private class MakeRequestTask extends AsyncTask<Void, Void, List<CalendarEvent>> {
        private com.google.api.services.calendar.Calendar mService = null;
        private Exception mLastError = null;

        private List<String> output;

        MakeRequestTask(GoogleAccountCredential credential) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new com.google.api.services.calendar.Calendar.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("Google Calendar API Android Quickstart")
                    .build();
        }

        /**
         * Background task to call Google Calendar API.
         *
         * @param params no parameters needed for this task.
         */
        @Override
        protected List<CalendarEvent> doInBackground(Void... params) {
            try {

                publishProgress();
                return getDataFromApi();
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return null;
            }
        }

        /**
         * Fetch a list of the next 10 events from the primary calendar.
         *
         * @return List of Strings describing returned events.
         * @throws IOException
         */
        private List<CalendarEvent> getDataFromApi() throws IOException {
            // List the next 10 events from the primary calendar.
            DateTime now = new DateTime(System.currentTimeMillis());
            List<CalendarEvent> calendarEvents = new ArrayList<CalendarEvent>();
            Events events = mService.events().list("primary")
                    .setMaxResults(10)
                    .setTimeMin(now)
                    .setOrderBy("startTime")
                    .setSingleEvents(true)
                    .execute();
            List<Event> items = events.getItems();

            for (Event event : items) {
                DateTime start = event.getStart().getDateTime();
                if (start == null) {
                    // All-day events don't have start times, so just use
                    // the start date.
                    start = event.getStart().getDate();
                }
                DateTime end = event.getEnd().getDateTime();
                Date startDate = new Date(start.getValue());
                Date endDate = new Date(end.getValue());

                CalendarEvent calEvent = new CalendarEvent(event.getSummary(), startDate, endDate, event.getLocation(), event.getStatus());
                //Toast.makeText(getApplicationContext(), event.getSummary(),Toast.LENGTH_SHORT);
                //addCalendarItem(rootInflater, rootView, event.getSummary());
                calendarEvents.add(calEvent);
                System.out.println(
                        String.format("%s (%s)", event.getSummary(), startDate));
            }
            return calendarEvents;
        }


        @Override
        protected void onPreExecute() {
            //TextView errorText = (TextView) findViewById(R.id.text1);
            //errorText.setText("");
        }

        @Override
        protected void onPostExecute(List<CalendarEvent> output) {
            TextView errorText = (TextView) rootView.findViewById(R.id.event_name);
            if (output == null || output.size() == 0) {

                errorText.setText("No results returned.");
            } else {

                for(CalendarEvent calendarEvent: output){
                    addCalendarItem(rootInflater, rootView, calendarEvent);
                }
            }
        }

        @Override
        protected void onCancelled() {
            mLastError.printStackTrace();
            TextView errorText = (TextView) rootView.findViewById(R.id.event_name);
            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    //showGooglePlayServicesAvailabilityErrorDialog(
                           // ((GooglePlayServicesAvailabilityIOException) mLastError)
                             //       .getConnectionStatusCode());
                } else if (mLastError instanceof UserRecoverableAuthIOException) {
                    startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            CalendarActivity.REQUEST_AUTHORIZATION);
                } else {
                    errorText.setText("The following error occurred:\n"
                            + mLastError.getMessage());
                }
            } else {
                errorText.setText("Request cancelled.");
            }
        }

    }


}
