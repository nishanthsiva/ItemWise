package com.wolfsoft.teammeetingschedule;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class SecondFragment extends Fragment {

    public static final String ARG_PAGE = "ARG_PAGE";
    private int mPageNo;

    private View rootView;
    private LayoutInflater rootInflater;

    public SecondFragment() {
        // Required empty public constructor
    }

    public static SecondFragment newInstance(int pageNo) {

        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, pageNo);
        SecondFragment fragment = new SecondFragment();
        fragment.setArguments(args);
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
        rootView =  inflater.inflate(R.layout.fragment_second, container, false);
        rootInflater = inflater;
        setLocationEntryInfo();
        setBatteryCardInfo();
        return rootView;
    }

    private void setLocationEntryInfo() {
        TextView tracked_content_1 = (TextView) rootView.findViewById(R.id.tracked_content_1);
        TextView tracked_content_2 = (TextView) rootView.findViewById(R.id.tracked_content_2);
        Location_Time_Out lto = new Location_Time_Out();
        InputStream databaseInputStream = getResources().openRawResource(R.raw.locationhistory);
        try {
            lto.readJsonStream(databaseInputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        int val = (int) lto.GetAvgExit("41998.0,-93633.0");
        val %= 12;
        if(val == 0) val += 9;
        tracked_content_1.setText("You leave home around "+val+" AM everyday.");
        val = (int) lto.GetAvgEntry("41998.0,-93633.0");
        val %= 12;
        tracked_content_2.setText("You return home around "+val+" PM everyday.");
    }

    private void setBatteryCardInfo() {
        TextView notify_card_1_1 = (TextView) rootView.findViewById(R.id.notify_card_1_1);
        TextView notify_card_1_2 = (TextView) rootView.findViewById(R.id.notify_card_1_2);
        TextView battery_charge = (TextView)rootView.findViewById(R.id.battery_life_hrs);
        int battery_left = getCurrentCharge(rootView.getContext());
        if(battery_left < 20){
            notify_card_1_1.setText("Low Battery!");
            notify_card_1_2.setText("It is advisable that you carry a charger to work.");
        }else{
            notify_card_1_1.setText("Sufficient Battery :)");
            notify_card_1_2.setText("You have sufficient battery to last you the whole day.");
        }
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


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

}
