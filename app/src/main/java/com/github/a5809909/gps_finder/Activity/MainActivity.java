package com.github.a5809909.gps_finder.Activity;

import android.app.Activity;
import android.app.Fragment;
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
import com.github.a5809909.gps_finder.Fragment.DatabaseFragment;
import com.github.a5809909.gps_finder.Fragment.LocationFragment;
import com.github.a5809909.gps_finder.Fragment.MapFragment;
import com.github.a5809909.gps_finder.Fragment.OneFragment;
import com.github.a5809909.gps_finder.ImagrLoader.PhotoGalleryFragment;
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

public class MainActivity extends AppCompatActivity implements OnClickListener, NavigationView.OnNavigationItemSelectedListener {
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    SharedPreferences sPref;
    private static final int LOCATION_PERMISSION_CODE = 855;
    private static final String API_KEY = "AIzaSyDNsRNkiJddjICdCY9fiFw3U6_nziORLC4";
    //    private DatabaseHelper databaseHelper;
    private TextView textviewLat, textviewLng, textviewAcc;
    private MainActivity instance;
    private static final String TAG = "Main";
    private DrawerLayout mDrawerLayout;
    private String savedLat;
    private String savedLng;
    private String savedAccuracy;
    private TextView textViewCellID;
    private TextView textViewDateAndTime;
    private TextView textViewLAC;
    private TextView textViewMNC;
    private TextView textViewMCC;
    private TextView textViewLatitude;
    private TextView textViewLongitude;
    private TextView textViewAccuracy;
    private TextView textViewCountry;
    private TextView textViewCity;
    private TextView textViewStreet;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        instance = this;

        initViews();
        getShared();
        setToolbar();
        initNavigationView();
        initTabs();
        initFab();


    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        textViewCellID = findViewById(R.id.text_date_and_time);
        textViewDateAndTime = findViewById(R.id.text_cell_id);
        textViewLAC = findViewById(R.id.text_lac);
        textViewMNC = findViewById(R.id.text_mnc);
        textViewMCC = findViewById(R.id.text_mcc);
        textViewLatitude = findViewById(R.id.text_latitude);
        textViewLongitude = findViewById(R.id.text_longitude);
        textViewAccuracy = findViewById(R.id.text_accuracy);
        textViewCountry = findViewById(R.id.text_country);
        textViewCity = findViewById(R.id.text_city);
        textViewStreet = findViewById(R.id.text_street);

    }

    private void getShared() {
        try {
            sPref = getSharedPreferences("MyPref", MODE_PRIVATE);
            savedLat = sPref.getString("lat", "").substring(0, 7);
            savedLng = sPref.getString("lng", "").substring(0, 7);
            savedAccuracy = sPref.getString("accuracy", "");

        } catch (Exception e) {
            savedLat = "";
            savedLng = "";
            savedAccuracy = "";
        }

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


    private void setToolbar() {
        if (savedLat == null || savedLat.isEmpty()) {
            getSupportActionBar().setTitle(R.string.app_name);
            toolbar.setSubtitle("");
        } else {

            getSupportActionBar().setTitle("Lat: " + savedLat + ", Lng: " + savedLng);
            toolbar.setSubtitle("Acc: " + savedAccuracy);
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

        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
        tabLayout.getTabAt(3).setIcon(tabIcons[3]);
        tabLayout.getTabAt(4).setIcon(tabIcons[4]);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new LocationFragment(), "LOCATION");
        adapter.addFrag(new DatabaseFragment(), "DATABASE");
        adapter.addFrag(new MapFragment(), "MAP");
        adapter.addFrag(new PhotoGalleryFragment(), "IMAGES");
        adapter.addFrag(new OneFragment(), "WEATHER");
        viewPager.setAdapter(adapter);
    }

    private void initNavigationView() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.show_point) {
            // Handle the camera action
        } else if (id == R.id.database) {

        } else if (id == R.id.show_map) {

        } else if (id == R.id.images) {

        } else if (id == R.id.weather) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
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

            case R.id.btn_show_database:
                Intent intentDataBase = new Intent(getApplicationContext(), DataActivity.class);
                startActivity(intentDataBase);
                break;

//            case R.id.btn_maps:
//                Intent intentMap = new Intent(getApplicationContext(), MapActivity.class);
//                startActivity(intentMap);
//                break;

            case R.id.btn_show_pictures:
                Intent startIntent = new Intent(MainActivity.this, LogService.class);
                startIntent.setAction(Constants.ACTION.STARTFOREGROUND_ACTION);
                startService(startIntent);
                break;
            case R.id.btn_show_weather:
                Intent stopIntent = new Intent(MainActivity.this, LogService.class);
                stopIntent.setAction(Constants.ACTION.STOPFOREGROUND_ACTION);
                startService(stopIntent);
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

                String cid = "" + gsmLoc.getCid();
                String lac = "" + gsmLoc.getLac();
                String mcc = op.substring(0, 3);
                String mnc = op.substring(3);
                Toast.makeText(this, "cid:" + cid, Toast.LENGTH_SHORT).show();


               LocationFragment frag1 = (LocationFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_location);
                View root = frag1.getView();
                TextView tw = root.findViewById(R.id.text_lac);
                tw.setText("mod");
//                ((TextView) frag1.getView().findViewById(R.id.text_cell_id))
//                        .setText("Access to Fragment 1 from Activity");
//


                textViewCellID.setText(cid);
//                textViewLAC.setText(lac);
//                textViewMCC.setText(mcc);
//                textViewMNC.setText(mnc);

                new HttpPostTask().execute(cid, lac, mcc, mnc);
            } else {
                Toast.makeText(instance, "No valid GSM network found",
                        Toast.LENGTH_LONG).show();
            }
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
                cellTower.put("cellId", params[0]);
                cellTower.put("locationAreaCode", params[1]);
                cellTower.put("mobileCountryCode", params[2]);
                cellTower.put("mobileNetworkCode", params[3]);

                Log.i(TAG, "cellId: " + params[0] +
                        ", locationAreaCode: " + params[1] +
                        ", mobileCountryCode: " + params[2] +
                        ", mobileNetworkCode: " + params[3]);
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
                    String accuracy = jsonResult.getString("accuracy");
                    String lat, lng;

                    lat = location.getString("lat");
                    lng = location.getString("lng");

                    sPref = getSharedPreferences("MyPref", MODE_PRIVATE);
                    SharedPreferences.Editor ed = sPref.edit();
                    ed.putString("lat", lat);
                    ed.putString("lng", lng);
                    ed.putString("accuracy", accuracy);


                    ed.commit();
                    getShared();
                    setToolbar();
                    Toast.makeText(MainActivity.this, "lat:" + lat + ", lng:" + lng + ", acc:" + accuracy, Toast.LENGTH_SHORT).show();

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
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }


}