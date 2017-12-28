package com.github.a5809909.gps_finder.Activity;

import android.app.Activity;
import android.view.View;

public class Main2Activity extends Activity implements View.OnClickListener{
    @Override
    public void onClick(View v) {

    }
}

//    private static final int LOCATION_PERMISSION_CODE = 855;
//    private static final String API_KEY = "AIzaSyDNsRNkiJddjICdCY9fiFw3U6_nziORLC4";
//    SharedPreferences sPref;
//    private Main2Activity instance;
//    private static final String TAG = "Main";
//    //    private DatabaseHelper databaseHelper;
//    private TextView lbsLatitude, lbsLongtitude, lbsAltitude, lbsPrecision, lbsType;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main2);
//        sPref = getSharedPreferences("MyPref", MODE_PRIVATE);
//        String savedLat = sPref.getString("lat", "");
//        String savedLng = sPref.getString("lng", "");
//        TextView textviewLat = findViewById(R.id.textView_lat);
//        TextView textviewLng = findViewById(R.id.textView_lng);
//        TextView textviewAcc = findViewById(R.id.textView_acc);
//        textviewLat.setText(savedLat);
//        textviewLng.setText(savedLng);
//        textviewAcc.setText(savedLat);
//
//
//    }
//
//
//
//
//    public static void requestStoragePermissions(Activity activity, int PERMISSION_REQUEST_CODE) {
//        java.lang.String[] perms = {"android.permission.ACCESS_FINE_LOCATION", "android.permission.ACCESS_COARSE_LOCATION", "android.permission.READ_PHONE_STATE"};
//        ActivityCompat.requestPermissions(activity, perms, PERMISSION_REQUEST_CODE);
//    }
//
//    public static boolean isLocationPermissionsAllowed(Activity activity) {
//        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
//            return activity.checkSelfPermission("android.permission.ACCESS_FINE_LOCATION") == PackageManager.PERMISSION_GRANTED
//                    &&
//                    activity.checkSelfPermission("android.permission.ACCESS_COARSE_LOCATION") == PackageManager.PERMISSION_GRANTED
//                    &&
//                    activity.checkSelfPermission("android.permission.READ_PHONE_STATE") == PackageManager.PERMISSION_GRANTED;
//        }
//        return true;
//    }
//    @Override
//    public void onClick(View v) {
//            switch (v.getId()) {
//            case R.id.textBtn_createJson:
//            getLocationClicked(v);
//
//                break;
//            case R.id.textBtn_sendJson:
//                Toast.makeText(this, "BTN", Toast.LENGTH_SHORT).show();
//                getCid();
//
//                break;
//            case R.id.btn_show_database:
//                Intent intentDataBase = new Intent(getApplicationContext(), DataActivity.class);
//                startActivity(intentDataBase);
//
//                break;
//            case R.id.btn_maps:
//                Intent intentMap = new Intent(getApplicationContext(), MapActivity.class);
//                startActivity(intentMap);
//
//                break;
//
//        }
//    }
//
//    public void startService() {
//        Log.d(TAG, "startService() called");
//        Calendar cal = Calendar.getInstance();
////        Intent intent = new Intent(this, LoggerService.class);
////        PendingIntent pintent = PendingIntent.getService(this, 0, intent, 0);
////        AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
////        alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),60 * 1000, pintent);
//    }
//
//    public void getCid() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//
//            if (isLocationPermissionsAllowed(this)) {
//
//            } else {
//                requestStoragePermissions(this, LOCATION_PERMISSION_CODE);
//
//            }
//
//            TelephonyManager tel = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
//            Log.i(TAG, "getCid: tel");
//            if (tel != null) {
//                CellLocation loc = tel.getCellLocation();
//                Log.i(TAG, "getCid: cell");
//                if ((loc != null) && (loc instanceof GsmCellLocation)) {
//                    GsmCellLocation gsmLoc = (GsmCellLocation) loc;
//                    Log.i(TAG, "getCid: cid");
//                    String cid = "" + gsmLoc.getCid();
//
//                    Toast.makeText(this, "cid:" + cid, Toast.LENGTH_SHORT).show();
//                }
//            }
//        }
//    }
//
//
//    public void getLocationClicked(View v) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//
//            if (isLocationPermissionsAllowed(this)) {
//
//            } else {
//                requestStoragePermissions(this, LOCATION_PERMISSION_CODE);
//
//            }
//
//            TelephonyManager tel = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
//
//            if (tel != null) {
//                CellLocation loc = tel.getCellLocation();
//
//                if ((loc != null) && (loc instanceof GsmCellLocation)) {
//                    GsmCellLocation gsmLoc = (GsmCellLocation) loc;
//                    String op = tel.getNetworkOperator();
//
//                    String cid = "" + gsmLoc.getCid();
//                    String lac = "" + gsmLoc.getLac();
//                    String mcc = op.substring(0, 3);
//                    String mnc = op.substring(3);
//                    Toast.makeText(this, "cid:" + cid, Toast.LENGTH_SHORT).show();
//
//
//                    new Main2Activity.HttpPostTask().execute(cid, lac, mcc, mnc);
//                } else {
//                    Toast.makeText(instance, "No valid GSM network found",
//                            Toast.LENGTH_LONG).show();
//                }
//            }
//        }
//    }
//
//    private class HttpPostTask extends AsyncTask<String, Void, String> {
//
//        ProgressDialog pd;
//
//        @Override
//        protected void onPreExecute() {
//            pd = new ProgressDialog(instance);
//            pd.setTitle("Getting Location");
//            pd.setMessage("Please Wait...");
//            pd.setCancelable(false);
//            pd.setIndeterminate(true);
//            pd.show();
//        }
//
//        @Override
//        protected String doInBackground(String... params) {
//
//            String result = null;
//
//            HttpClient httpclient = new DefaultHttpClient();
//            HttpPost httpost = new HttpPost("https://www.googleapis.com/geolocation/v1/geolocate?key=" + API_KEY);
//
//            StringEntity se;
//
//            try {
//                JSONObject cellTower = new JSONObject();
//                cellTower.put("cellId", params[0]);
//                cellTower.put("locationAreaCode", params[1]);
//                cellTower.put("mobileCountryCode", params[2]);
//                cellTower.put("mobileNetworkCode", params[3]);
//                Log.i(TAG, "cellId: " + params[0] +
//                        ", locationAreaCode: " + params[1] +
//                        ", mobileCountryCode: " + params[2] +
//                        ", mobileNetworkCode: " + params[3]);
//                JSONArray cellTowers = new JSONArray();
//                cellTowers.put(cellTower);
//
//                JSONObject rootObject = new JSONObject();
//                rootObject.put("cellTowers", cellTowers);
//
//                se = new StringEntity(rootObject.toString());
//                se.setContentType("application/json");
//
//                httpost.setEntity(se);
//                httpost.setHeader("Accept", "application/json");
//                httpost.setHeader("Content-type", "application/json");
//                Log.i(TAG, "cellTower: " + cellTower);
//                ResponseHandler<String> responseHandler = new BasicResponseHandler();
//                String response = httpclient.execute(httpost, responseHandler);
//
//                result = response;
//            } catch (Exception e) {
//                final String err = e.getMessage();
//                runOnUiThread(new Runnable() {
//
//                    @Override
//                    public void run() {
//                        Toast.makeText(instance, "Exception requesting location: " + err, Toast.LENGTH_LONG).show();
//                    }
//
//                });
//                e.printStackTrace();
//            }
//            return result;
//        }
//
//        @Override
//        protected void onPostExecute(String result) {
//            if (pd != null) {
//                try {
//                    pd.dismiss();
//                } catch (Exception e) {
//                }
//            }
//
//            if (result != null) {
//                try {
//                    JSONObject jsonResult = new JSONObject(result);
//                    JSONObject location = jsonResult.getJSONObject("location");
//                    String lat, lng;
//
//                    lat = location.getString("lat");
//                    lng = location.getString("lng");
//                    lbsAltitude.setText(lat);
//                    lbsLongtitude.setText(lng);
//                    sPref = getSharedPreferences("MyPref", MODE_PRIVATE);
//                    SharedPreferences.Editor ed = sPref.edit();
//                    ed.putString("lat", lat);
//                    ed.putString("lng", lng);
//                    ed.commit();
//                    Toast.makeText(Main2Activity.this, "lat:" + lat + ", lng:" + lng, Toast.LENGTH_SHORT).show();
//
//                } catch (Exception e) {
//                    Toast.makeText(instance, "Exception parsing response: " + e.getMessage(), Toast.LENGTH_LONG).show();
//                    e.printStackTrace();
//                }
//            }
//        }
//
//    }
//
//
//
////    private void saveInSql(PhoneState result) {
////        databaseHelper = new DatabaseHelper(this);
////        databaseHelper.addUser(result);
////
////    }
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//    }
//
//}