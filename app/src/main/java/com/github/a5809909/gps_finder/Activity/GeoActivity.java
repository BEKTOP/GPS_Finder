package com.github.a5809909.gps_finder.Activity;

import android.location.Address;
import android.location.Geocoder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.a5809909.gps_finder.R;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class GeoActivity extends AppCompatActivity {


    double lat = 55.833167;
    double lng = 37.398949;

    final int maxResult = 5;
    String addressList[] = new String[maxResult];
    private ArrayAdapter<String> adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_geo);

        TextView myLatitude = (TextView) findViewById(R.id.latitude);
        TextView myLongitude = (TextView) findViewById(R.id.longitude);
        TextView myAddress = (TextView) findViewById(R.id.address);
        Spinner myAddressList = (Spinner) findViewById(R.id.addresslist);

        myLatitude.setText("Широта: " + String.valueOf(lat));
        myLongitude.setText("Долгота: " + String.valueOf(lng));

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng,
                    maxResult);

            if (addresses != null) {
                for (int j = 0; j < maxResult; j++) {
                    Address returnedAddress = addresses.get(j);
                    StringBuilder strReturnedAddress = new StringBuilder();

                    for (int i = 0; i < returnedAddress
                            .getMaxAddressLineIndex(); i++) {
                        strReturnedAddress.append(
                                returnedAddress.getAddressLine(i)).append("\n");
                    }
                    addressList[j] = strReturnedAddress.toString();
                }

                adapter = new ArrayAdapter<String>(this,
                        android.R.layout.simple_spinner_item, addressList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                myAddressList.setAdapter(adapter);
            } else {
                myAddress.setText("Нет адресов!");
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            myAddress.setText("Не могу получить адрес!");
        }
    }
}