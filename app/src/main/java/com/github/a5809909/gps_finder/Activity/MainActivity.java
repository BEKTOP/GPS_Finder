package com.github.a5809909.gps_finder.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.CellLocation;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.github.a5809909.gps_finder.Adapter.ViewPagerAdapter;
import com.github.a5809909.gps_finder.Fragment.DatabaseFragment2;
import com.github.a5809909.gps_finder.Loaders.LocationLoaderAsyncTask;
import com.github.a5809909.gps_finder.Fragment.DatabaseFragment;
import com.github.a5809909.gps_finder.Fragment.LocationFragment;
import com.github.a5809909.gps_finder.Fragment.MapFragment;
import com.github.a5809909.gps_finder.Fragment.PhotoGalleryFragment;
import com.github.a5809909.gps_finder.Fragment.WeatherFragment;
import com.github.a5809909.gps_finder.Loaders.IAsyncTaskListener;
import com.github.a5809909.gps_finder.Model.LocationModel;
import com.github.a5809909.gps_finder.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import static com.github.a5809909.gps_finder.Utilities.Constants.NOTIFICATION_ID.LOCATION_PERMISSION_CODE;

public class MainActivity extends AppCompatActivity implements OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    SimpleDateFormat formatter;
    LocationModel mLocationModel;
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private DrawerLayout drawer;
    ProgressDialog pd;

    SharedPreferences sPref;
    SharedPreferences.Editor sPrefEditor;
    //    private DatabaseHelper databaseHelper;
    private MainActivity instance;
    private static final String TAG = "Main";
    private DrawerLayout mDrawerLayout;

    private ViewPager viewPager;
    View viewPagerRootView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        instance = this;
        mLocationModel = new LocationModel();
        getShared();
        initToolbar();
        initNavigationView();
        initTabs();
        initFab();

        viewPager = findViewById(R.id.view_pager);
        viewPagerRootView = viewPager.getRootView();
    }

    private void getShared() {
        try {
            sPref = getSharedPreferences("MyPref", MODE_PRIVATE);
            mLocationModel.setDateAndTime(sPref.getString("dayAndTime", ""));
            mLocationModel.setCellId(sPref.getString("cellId", ""));
            mLocationModel.setLac(sPref.getString("lac", ""));
            mLocationModel.setMcc(sPref.getString("mcc", ""));
            mLocationModel.setMnc(sPref.getString("mnc", ""));
            mLocationModel.setLat(sPref.getString("lat", ""));
            mLocationModel.setLng(sPref.getString("lng", ""));
            mLocationModel.setAcc(sPref.getString("accuracy", ""));
            mLocationModel.setAddress(sPref.getString("address", ""));

        } catch (Exception e) {
            //  Toast.makeText(this, "1 time", Toast.LENGTH_LONG).show();
        }
    }

    private void setShared() {
        sPrefEditor = sPref.edit();
        sPrefEditor.putString("dayAndTime", mLocationModel.getDateAndTime());
        sPrefEditor.putString("cellId", mLocationModel.getCellId());
        sPrefEditor.putString("lac", mLocationModel.getLac());
        sPrefEditor.putString("mcc", mLocationModel.getMcc());
        sPrefEditor.putString("mnc", mLocationModel.getMnc());
        sPrefEditor.putString("lat", mLocationModel.getLat());
        sPrefEditor.putString("lng", mLocationModel.getLng());
        sPrefEditor.putString("accuracy", mLocationModel.getAcc());
        sPrefEditor.putString("address", mLocationModel.getAddress());
        sPrefEditor.apply();
    }

    private void initFab() {
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(this);
    }

    private void initTabs() {
        viewPager = findViewById(R.id.view_pager);
        setupViewPager(viewPager);

        tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();
    }

    private void initToolbar() {
        toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        if (mLocationModel.getLat() == null || mLocationModel.getLat().isEmpty()) {
            getSupportActionBar().setTitle(R.string.app_name);
            toolbar.setSubtitle("");
        } else {

            getSupportActionBar().setTitle("Lat: " + mLocationModel.getLat().substring(0, 7) + ", Lng: " + mLocationModel.getLng().substring(0, 7));
            toolbar.setSubtitle("Acc: " + mLocationModel.getAcc());
        }
    }

    private void setToolbar() {
        try {
            getSupportActionBar().setTitle("Lat: " + mLocationModel.getLat().substring(0, 7) + ", Lng: " + mLocationModel.getLng().substring(0, 7));
            toolbar.setSubtitle("Acc: " + mLocationModel.getAcc());
        } catch (Exception e) {

        }

    }

    private void setupTabIcons() {
        int[] tabIcons = {
                R.drawable.map_marker_radius,
                R.drawable.database,
                R.drawable.google_maps,
                R.drawable.image,
                R.drawable.weather_partlycloudy
        };

        for (int i = 0; i <= 4; i++) {
            tabLayout.getTabAt(i).setIcon(tabIcons[i]);
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new LocationFragment(), "LOCATION");
        adapter.addFrag(new DatabaseFragment(), "DATABASE");
        adapter.addFrag(new MapFragment(), "MAP");
        adapter.addFrag(new PhotoGalleryFragment(), "IMAGES");
        adapter.addFrag(new WeatherFragment(), "WEATHER");
        viewPager.setAdapter(adapter);
    }

    private void initNavigationView() {
        drawer = findViewById(R.id.drawerLayout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_show_point) {
            viewPager.setCurrentItem(0);
        } else if (id == R.id.nav_database) {
            viewPager.setCurrentItem(1);
        } else if (id == R.id.nav_show_map) {
            viewPager.setCurrentItem(2);
        } else if (id == R.id.nav_images) {
            viewPager.setCurrentItem(3);
        } else if (id == R.id.nav_weather) {
            viewPager.setCurrentItem(4);
        } else if (id == R.id.nav_settings) {
            Intent intent = new Intent(this,SettingsActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = findViewById(R.id.drawerLayout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.fab:
                checkPermission();
                getLocationClicked();
                break;

        }
    }

    public void startService() {
        Log.d(TAG, "startService() called");
        Calendar cal = Calendar.getInstance();
//        Intent intent = new Intent(this, LoggerService.class);
//        PendingIntent pintent = PendingIntent.getService(this, 0, intent, 0);
//        AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//        alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),60 * 1000, pintent);
    }

    public void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (isLocationPermissionsAllowed(this)) {

            } else {
                requestStoragePermissions(this, LOCATION_PERMISSION_CODE);

            }
        }

    }

    public static void requestStoragePermissions(Activity activity, int PERMISSION_REQUEST_CODE) {
        try {
            java.lang.String[] perms = {"android.permission.ACCESS_FINE_LOCATION", "android.permission.ACCESS_COARSE_LOCATION", "android.permission.READ_PHONE_STATE"};
            ActivityCompat.requestPermissions(activity, perms, PERMISSION_REQUEST_CODE);
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    public void getLocationClicked() {
        if (!isLocationPermissionsAllowed(this)) {
            return;
        }

        TelephonyManager tel = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);

        if (tel != null) {
            CellLocation loc = tel.getCellLocation();

            if ((loc != null) && (loc instanceof GsmCellLocation)) {
                GsmCellLocation gsmLoc = (GsmCellLocation) loc;
                String op = tel.getNetworkOperator();

                formatter = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
                formatter.setTimeZone(TimeZone.getTimeZone("Europe/Minsk"));

                mLocationModel.setDateAndTime(formatter.format(System.currentTimeMillis()));
                mLocationModel.setCellId("" + gsmLoc.getCid());
                mLocationModel.setLac("" + gsmLoc.getLac());
                mLocationModel.setMcc(op.substring(0, 3));
                mLocationModel.setMnc(op.substring(3));

                new LocationLoaderAsyncTask(instance, new IAsyncTaskListener() {

                    @Override
                    public void finishedAsyncTask() {
                        setShared();
                        setTextViewFragment();
                        setToolbar();
                        if (pd != null) {
                            try {
                                pd.dismiss();
                            } catch (Exception e) {
                            }
                        }
                        //   Toast.makeText(instance, "cid:" + mLocationModel.getCellId(), Toast.LENGTH_SHORT).show();

                    }
                }).execute(mLocationModel);
                pd = new ProgressDialog(instance);
                pd.setTitle("Getting Location");
                pd.setMessage("Please Wait...");
                pd.setCancelable(false);
                pd.setIndeterminate(true);
                pd.show();
                if (mLocationModel.getErrors() != null) {
                    Toast.makeText(instance, mLocationModel.getErrors(), Toast.LENGTH_LONG).show();
                }
            }
        } else {
            Toast.makeText(instance, "No valid GSM network found",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void setTextViewFragment() {

        final TextView textViewCellID, textViewDateAndTime, textViewLAC, textViewMNC, textViewMCC, textViewLatitude, textViewLongitude,
                textViewAccuracy, textViewAddress;
        try {
//            LocationFragment fragment = new LocationFragment();
//
//            fragment.setFragmentViews();

            textViewDateAndTime = viewPagerRootView.findViewById(R.id.text_date_and_time);
            textViewCellID = viewPagerRootView.findViewById(R.id.text_cell_id);
            textViewLAC = viewPagerRootView.findViewById(R.id.text_lac);
            textViewMNC = viewPagerRootView.findViewById(R.id.text_mnc);
            textViewMCC = viewPagerRootView.findViewById(R.id.text_mcc);
            textViewLatitude = viewPagerRootView.findViewById(R.id.text_latitude);
            textViewLongitude = viewPagerRootView.findViewById(R.id.text_longitude);
            textViewAccuracy = viewPagerRootView.findViewById(R.id.text_accuracy);
            textViewAddress = viewPagerRootView.findViewById(R.id.text_address);

            textViewDateAndTime.setText(mLocationModel.getDateAndTime());
            textViewCellID.setText(mLocationModel.getCellId());
            textViewLAC.setText(mLocationModel.getLac());
            textViewMCC.setText(mLocationModel.getMcc());
            textViewMNC.setText(mLocationModel.getMnc());
            textViewLatitude.setText(mLocationModel.getLat());
            textViewLongitude.setText(mLocationModel.getLng());
            textViewAccuracy.setText(mLocationModel.getAcc());
            textViewAddress.setText(mLocationModel.getAddress());
        } catch (Exception e) {

        }
    }

    public static boolean isLocationPermissionsAllowed(Activity activity) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            return activity.checkSelfPermission("android.permission.ACCESS_FINE_LOCATION") == PackageManager.PERMISSION_GRANTED
                    &&
                    activity.checkSelfPermission("android.permission.ACCESS_COARSE_LOCATION") == PackageManager.PERMISSION_GRANTED
                    &&
                    activity.checkSelfPermission("android.permission.READ_PHONE_STATE") == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        assert drawer != null;
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        getShared();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

}