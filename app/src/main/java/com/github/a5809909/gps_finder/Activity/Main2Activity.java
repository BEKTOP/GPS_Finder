package com.github.a5809909.gps_finder.Activity;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.github.a5809909.gps_finder.R;

public class Main2Activity extends AppCompatActivity {
    SharedPreferences sPref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        sPref = getSharedPreferences("MyPref", MODE_PRIVATE);
        String savedLat = sPref.getString("lat", "");
        String savedLng = sPref.getString("lng", "");
        TextView textviewGPS = findViewById(R.id.txtCurrentGpsLocation);
        textviewGPS.setText("Lat: "+savedLat+ ", Lng:"+savedLng);
    }
}
