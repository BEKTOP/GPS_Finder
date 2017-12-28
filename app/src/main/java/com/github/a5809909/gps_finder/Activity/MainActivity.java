package com.github.a5809909.gps_finder.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.telephony.CellLocation;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.github.a5809909.gps_finder.R;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Calendar;

public class MainActivity extends Activity implements OnClickListener {

    SharedPreferences sPref;
    private static final int LOCATION_PERMISSION_CODE = 855;
    private static final String API_KEY = "AIzaSyDNsRNkiJddjICdCY9fiFw3U6_nziORLC4";
    //    private DatabaseHelper databaseHelper;
    private TextView textviewLat, textviewLng, textviewAcc;
    private MainActivity instance;
    private static final String TAG = "Main";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        instance = this;
        initViews();
        sPref = getSharedPreferences("MyPref", MODE_PRIVATE);
        String savedLat = sPref.getString("lat", "");
        String savedLng = sPref.getString("lng", "");
        String savedAccuracy = sPref.getString("accuracy", "");

        setTextViewLocation(savedLat, savedLng, savedAccuracy);

    }

    private void setTextViewLocation(String savedLat, String savedLng, String savedAccuracy) {
        textviewLat.setText(savedLat);
        textviewLng.setText(savedLng);
        textviewAcc.setText(savedAccuracy);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btn_json_create:
                getCid();
                break;

            case R.id.btn_json_send:
                getLocationClicked(v);
                break;

            case R.id.btn_show_database:
                Intent intentDataBase = new Intent(getApplicationContext(), DataActivity.class);
                startActivity(intentDataBase);
                break;

            case R.id.btn_maps:
                Intent intentMap = new Intent(getApplicationContext(), MapActivity.class);
                startActivity(intentMap);
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

    public void getCid() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (isLocationPermissionsAllowed(this)) {

            } else {
                requestStoragePermissions(this, LOCATION_PERMISSION_CODE);

            }

            TelephonyManager tel = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
            Log.i(TAG, "getCid: tel");
            if (tel != null) {
                CellLocation loc = tel.getCellLocation();
                Log.i(TAG, "getCid: cell");
                if ((loc != null) && (loc instanceof GsmCellLocation)) {
                    GsmCellLocation gsmLoc = (GsmCellLocation) loc;
                    Log.i(TAG, "getCid: cid");
                    String cid = "" + gsmLoc.getCid();

                    Toast.makeText(this, "cid:" + cid, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public void getLocationClicked(View v) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (isLocationPermissionsAllowed(this)) {

            } else {
                requestStoragePermissions(this, LOCATION_PERMISSION_CODE);
                if (!isLocationPermissionsAllowed(this)) {
                    return;
                }
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


                    new HttpPostTask().execute(cid, lac, mcc, mnc);
                } else {
                    Toast.makeText(instance, "No valid GSM network found",
                            Toast.LENGTH_LONG).show();
                }
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
                    Toast.makeText(MainActivity.this, "lat:" + lat + ", lng:" + lng + ", acc:" + accuracy, Toast.LENGTH_SHORT).show();
                    setTextViewLocation(lat, lng, accuracy);
                } catch (Exception e) {
                    Toast.makeText(instance, "Exception parsing response: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        }

    }

    private void initViews() {
        textviewLat = findViewById(R.id.textView_lat);
        textviewLng = findViewById(R.id.textView_lng);
        textviewAcc = findViewById(R.id.textView_acc);


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

}