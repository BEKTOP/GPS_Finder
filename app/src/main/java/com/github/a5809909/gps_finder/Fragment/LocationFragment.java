package com.github.a5809909.gps_finder.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.github.a5809909.gps_finder.Activity.MainActivity;
import com.github.a5809909.gps_finder.Model.LocationModel;
import com.github.a5809909.gps_finder.R;

public class LocationFragment extends Fragment {
LocationModel mLocationModel;
    Context context;
    SharedPreferences sPref;
    TextView textViewDateAndTime, textViewCellID, textViewLAC, textViewMNC, textViewMCC, textViewLatitude, textViewLongitude, textViewAccuracy,
            textViewCountry, textViewCity, textViewStreet;

    public LocationFragment() {}

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

    public void initFragment(View pView) {
        getShared();
        initFragmentViews(pView);
        setFragmentViews();
    }

    private void setFragmentViews() {
        textViewDateAndTime.setText(mLocationModel.getDateAndTime());
        textViewCellID.setText(mLocationModel.getCellId());
        textViewLAC.setText(mLocationModel.getLac());
        textViewMCC.setText(mLocationModel.getMcc());
        textViewMNC.setText(mLocationModel.getMnc());
        textViewLatitude.setText(mLocationModel.getLat());
        textViewLongitude.setText(mLocationModel.getLng());
        textViewAccuracy.setText(mLocationModel.getAcc());

    }

    private void initFragmentViews(View pView) {
        textViewDateAndTime = pView.findViewById(R.id.text_date_and_time);
        textViewCellID = pView.findViewById(R.id.text_cell_id);
        textViewLAC = pView.findViewById(R.id.text_lac);
        textViewMNC = pView.findViewById(R.id.text_mnc);
        textViewMCC = pView.findViewById(R.id.text_mcc);
        textViewLatitude = pView.findViewById(R.id.text_latitude);
        textViewLongitude = pView.findViewById(R.id.text_longitude);
        textViewAccuracy = pView.findViewById(R.id.text_accuracy);
        textViewCountry = pView.findViewById(R.id.text_country);
        textViewCity = pView.findViewById(R.id.text_city);
        textViewStreet = pView.findViewById(R.id.text_street);
    }

    private void getShared() {
        try {
            sPref = this.getActivity().getSharedPreferences("MyPref", Context.MODE_PRIVATE);
            mLocationModel.setCellId(sPref.getString("cellId", ""));
            mLocationModel.setLac(sPref.getString("lac", ""));
            mLocationModel.setMcc(sPref.getString("mcc", ""));
            mLocationModel.setMnc(sPref.getString("mnc", ""));
            mLocationModel.setLat(sPref.getString("lat", ""));
            mLocationModel.setLng(sPref.getString("lng", ""));
            mLocationModel.setAcc(sPref.getString("accuracy", ""));

        } catch (Exception e) {

        }
    }
}
