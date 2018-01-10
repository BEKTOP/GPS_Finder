package com.github.a5809909.gps_finder.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.a5809909.gps_finder.Model.LocationModel;
import com.github.a5809909.gps_finder.R;

public class LocationFragment extends Fragment {

    LocationModel mLocationModel;
    SharedPreferences sPref;
    TextView textViewDateAndTime, textViewCellID, textViewLAC, textViewMNC, textViewMCC, textViewLatitude, textViewLongitude, textViewAccuracy,
            textViewAddress;

    public LocationFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_location, container, false);
        mLocationModel = new LocationModel();
        initFragment(view);

        return view;
    }


    private void initFragment(View pView) {
        Log.i("222", "initFragment: ");
        getShared();
        initFragmentViews(pView);
        setFragmentViews();
    }

    public void setFragmentViews() {


        textViewDateAndTime.setText(mLocationModel.getDateAndTime());
        textViewCellID.setText(mLocationModel.getCellId());
        textViewLAC.setText(mLocationModel.getLac());
        textViewMCC.setText(mLocationModel.getMcc());
        textViewMNC.setText(mLocationModel.getMnc());
        textViewLatitude.setText(mLocationModel.getLat());
        textViewLongitude.setText(mLocationModel.getLng());
        textViewAccuracy.setText(mLocationModel.getAcc());
        textViewAddress.setText(mLocationModel.getAddress());
        Log.i("222", "setFragmentViews: "+mLocationModel.getDateAndTime());
    }

    private void initFragmentViews(View pView) {
        Log.i("222", "initFragmentViews: ");
        textViewDateAndTime = pView.findViewById(R.id.text_date_and_time);
        textViewCellID = pView.findViewById(R.id.text_cell_id);
        textViewLAC = pView.findViewById(R.id.text_lac);
        textViewMNC = pView.findViewById(R.id.text_mnc);
        textViewMCC = pView.findViewById(R.id.text_mcc);
        textViewLatitude = pView.findViewById(R.id.text_latitude);
        textViewLongitude = pView.findViewById(R.id.text_longitude);
        textViewAccuracy = pView.findViewById(R.id.text_accuracy);
        textViewAddress = pView.findViewById(R.id.text_address);

    }

    private void getShared() {
        try {
            sPref = this.getActivity().getSharedPreferences("MyPref", Context.MODE_PRIVATE);

            mLocationModel.setDateAndTime(sPref.getString("dayAndTime", ""));
            mLocationModel.setCellId(sPref.getString("cellId", ""));
            mLocationModel.setLac(sPref.getString("lac", ""));
            mLocationModel.setMcc(sPref.getString("mcc", ""));
            mLocationModel.setMnc(sPref.getString("mnc", ""));
            mLocationModel.setLat(sPref.getString("lat", ""));
            mLocationModel.setLng(sPref.getString("lng", ""));
            mLocationModel.setAcc(sPref.getString("accuracy", ""));
            mLocationModel.setAddress(sPref.getString("address", ""));
            Log.i("222", "getShared: "+sPref.getString("dayAndTime", ""));
        } catch (Exception e) {

        }
    }
}
