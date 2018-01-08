package com.github.a5809909.gps_finder.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
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
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.telephony.CellLocation;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.github.a5809909.gps_finder.Adapter.ViewPagerAdapter;
import com.github.a5809909.gps_finder.Fragment.DatabaseFragment;
import com.github.a5809909.gps_finder.Fragment.LocationFragment;
import com.github.a5809909.gps_finder.Fragment.MapFragment;
import com.github.a5809909.gps_finder.Fragment.WeatherFragment;
import com.github.a5809909.gps_finder.ImagrLoader.PhotoGalleryFragment;
import com.github.a5809909.gps_finder.Model.LocationModel;
import com.github.a5809909.gps_finder.R;
import com.github.a5809909.gps_finder.Service.LogService;
import com.github.a5809909.gps_finder.Utilities.Constants;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Calendar;

import static com.github.a5809909.gps_finder.Utilities.Constants.NOTIFICATION_ID.API_KEY;
import static com.github.a5809909.gps_finder.Utilities.Constants.NOTIFICATION_ID.LOCATION_PERMISSION_CODE;

public class MainActivity extends AppCompatActivity implements OnClickListener, NavigationView.OnNavigationItemSelectedListener, CompoundButton.OnCheckedChangeListener {

    LocationModel mLocationModel;
    private Toolbar toolbar;
    private TabLayout tabLayout;

    SharedPreferences sPref;
    SharedPreferences.Editor sPrefEditor;
    //    private DatabaseHelper databaseHelper;
    private TextView textviewLat, textviewLng, textviewAcc;
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

        SwitchCompat switchCompat = viewPagerRootView.findViewById(R.id.logServiceOn);

        if (switchCompat != null) {
            switchCompat.setOnCheckedChangeListener(this);
        }
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Toast.makeText(this, "Отслеживание переключения: " + (isChecked ? "on" : "off"),
                Toast.LENGTH_SHORT).show();
    }

    private void getShared() {
        try {
            sPref = getSharedPreferences("MyPref", MODE_PRIVATE);
            mLocationModel.setCellId(sPref.getString("cellId", ""));
            mLocationModel.setLac(sPref.getString("lac", ""));
            mLocationModel.setMcc(sPref.getString("mcc", ""));
            mLocationModel.setMnc(sPref.getString("mnc", ""));
            mLocationModel.setLat(sPref.getString("lat", ""));
            mLocationModel.setLng(sPref.getString("lng", ""));
            mLocationModel.setAcc(sPref.getString("accuracy", ""));

        } catch (Exception e) {
            Toast.makeText(this, "1 time", Toast.LENGTH_LONG).show();
        }
    }

    private void setShared() {
        sPrefEditor = sPref.edit();
        sPrefEditor.putString("cellId", mLocationModel.getCellId());
        sPrefEditor.putString("lac", mLocationModel.getLac());
        sPrefEditor.putString("mcc", mLocationModel.getMcc());
        sPrefEditor.putString("mnc", mLocationModel.getMnc());
        sPrefEditor.putString("lat", mLocationModel.getLat());
        sPrefEditor.putString("lng", mLocationModel.getLng());
        sPrefEditor.putString("accuracy", mLocationModel.getAcc());
        sPrefEditor.apply();
    }

    private void initFab() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
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
        getSupportActionBar().setTitle("Lat: " + mLocationModel.getLat().substring(0, 7) + ", Lng: " + mLocationModel.getLng().substring(0, 7));
        toolbar.setSubtitle("Acc: " + mLocationModel.getAcc());
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
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
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
        if (id == R.id.show_point) {

        } else if (id == R.id.database) {
        } else if (id == R.id.show_map) {
        } else if (id == R.id.images) {
        } else if (id == R.id.weather) {
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void onSwitchClicked(View view) {
        boolean on = ((Switch) view).isChecked();
        if (on) {
            Intent startIntent = new Intent(MainActivity.this, LogService.class);
            startIntent.setAction(Constants.ACTION.STARTFOREGROUND_ACTION);
            startService(startIntent);
        } else {
            Intent stopIntent = new Intent(MainActivity.this, LogService.class);
            stopIntent.setAction(Constants.ACTION.STOPFOREGROUND_ACTION);
            startService(stopIntent);
        }
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

                mLocationModel.setCellId("" + gsmLoc.getCid());
                mLocationModel.setLac("" + gsmLoc.getLac());
                mLocationModel.setMcc(op.substring(0, 3));
                mLocationModel.setMnc(op.substring(3));
                //              Toast.makeText(this, "cid:" + mLocationModel.getCellId(), Toast.LENGTH_SHORT).show();

                setTextViewFragment();

                new HttpPostTask().execute();
            } else {
                Toast.makeText(instance, "No valid GSM network found",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    private void setTextViewFragment() {

        final TextView textViewCellID, textViewDateAndTime, textViewLAC, textViewMNC, textViewMCC, textViewLatitude, textViewLongitude,
                textViewAccuracy, textViewCountry, textViewCity, textViewStreet;
        try {

            textViewDateAndTime = viewPagerRootView.findViewById(R.id.text_date_and_time);
            textViewCellID = viewPagerRootView.findViewById(R.id.text_cell_id);
            textViewLAC = viewPagerRootView.findViewById(R.id.text_lac);
            textViewMNC = viewPagerRootView.findViewById(R.id.text_mnc);
            textViewMCC = viewPagerRootView.findViewById(R.id.text_mcc);
            textViewLatitude = viewPagerRootView.findViewById(R.id.text_latitude);
            textViewLongitude = viewPagerRootView.findViewById(R.id.text_longitude);
            textViewAccuracy = viewPagerRootView.findViewById(R.id.text_accuracy);
            textViewCountry = viewPagerRootView.findViewById(R.id.text_country);
            textViewCity = viewPagerRootView.findViewById(R.id.text_city);
            textViewStreet = viewPagerRootView.findViewById(R.id.text_street);

            textViewDateAndTime.setText(mLocationModel.getDateAndTime());
            textViewCellID.setText(mLocationModel.getCellId());
            textViewLAC.setText(mLocationModel.getLac());
            textViewMCC.setText(mLocationModel.getMcc());
            textViewMNC.setText(mLocationModel.getMnc());
            textViewLatitude.setText(mLocationModel.getLat());
            textViewLongitude.setText(mLocationModel.getLng());
            textViewAccuracy.setText(mLocationModel.getAcc());
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

    private class HttpPostTask extends AsyncTask<String, Void, String> {

        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(instance);
            pd.setTitle("Getting Location");
            pd.setMessage("Please Wait...");
            pd.setCancelable(false);
            pd.setIndeterminate(true);
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {

            String result = null;

            HttpClient httpclient = new DefaultHttpClient();

            HttpPost httpost = new HttpPost("https://www.googleapis.com/geolocation/v1/geolocate?key=" + API_KEY);

            StringEntity se;

            try {
                JSONObject cellTower = new JSONObject();
                cellTower.put("cellId", mLocationModel.getCellId());
                cellTower.put("locationAreaCode", mLocationModel.getLac());
                cellTower.put("mobileCountryCode", mLocationModel.getMcc());
                cellTower.put("mobileNetworkCode", mLocationModel.getMnc());

                Log.i(TAG, "cellId: " + mLocationModel.getCellId() +
                        ", locationAreaCode: " + mLocationModel.getLac() +
                        ", mobileCountryCode: " + mLocationModel.getMcc() +
                        ", mobileNetworkCode: " + mLocationModel.getMnc());
                JSONArray cellTowers = new JSONArray();
                cellTowers.put(cellTower);

                JSONObject rootObject = new JSONObject();
                rootObject.put("cellTowers", cellTowers);

                se = new StringEntity(rootObject.toString());
                se.setContentType("application/json");

                httpost.setEntity(se);
                httpost.setHeader("Accept", "application/json");
                httpost.setHeader("Content-type", "application/json");
                Log.i(TAG, "cellTower: " + cellTower);
                Log.i(TAG, "rootObject.toString(): " + rootObject.toString());
                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                String response = httpclient.execute(httpost, responseHandler);

                result = response;
            } catch (Exception e) {
                final String err = e.getMessage();
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        Toast.makeText(instance, "Exception requesting location: " + err, Toast.LENGTH_LONG).show();
                    }

                });
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            if (pd != null) {
                try {
                    pd.dismiss();
                } catch (Exception e) {
                }
            }

            if (result != null) {
                try {
                    JSONObject jsonResult = new JSONObject(result);
                    JSONObject location = jsonResult.getJSONObject("location");

                    mLocationModel.setAcc(jsonResult.getString("accuracy"));
                    mLocationModel.setLat(location.getString("lat"));
                    mLocationModel.setLng(location.getString("lng"));

                    setShared();
                    setTextViewFragment();
                    setToolbar();
                    Toast.makeText(MainActivity.this, "lat:" + mLocationModel.getLat() + ", lng:" + mLocationModel.getLng() + ", acc:" + mLocationModel.getAcc(), Toast.LENGTH_SHORT).show();

                } catch (Exception e) {
                    Toast.makeText(instance, "Exception parsing response: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        }

    }

//    private void saveInSql(PhoneState result) {
//        databaseHelper = new DatabaseHelper(this);
//        databaseHelper.addUser(result);
//
//    }

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