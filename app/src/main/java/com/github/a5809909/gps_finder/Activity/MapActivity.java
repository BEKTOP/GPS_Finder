package com.github.a5809909.gps_finder.Activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.github.a5809909.gps_finder.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


public class MapActivity  extends AppCompatActivity implements OnMapReadyCallback{
    SharedPreferences sPref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        initMap();
    }

    private void initMap() {

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        sPref = getSharedPreferences("MyPref", MODE_PRIVATE);
        String savedLat = sPref.getString("lat", "");
        String savedLng = sPref.getString("lng", "");
        Toast.makeText(this, "lat:"+savedLat+", lng:"+savedLng, Toast.LENGTH_SHORT).show();
        double lat = Double.parseDouble(savedLat);
        double lng = Double.parseDouble(savedLng);
        LatLng latLng = new LatLng(lat, lng);
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng)
                .zoom(16)
                .build();
        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
        map.animateCamera(cameraUpdate);
        map.addMarker(new MarkerOptions().position(latLng).title("lat:"+savedLat+", lng:"+savedLng));
        map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
    }
}
