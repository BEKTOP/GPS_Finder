package com.github.a5809909.gps_finder.Loaders;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.github.a5809909.gps_finder.Model.LocationModel;
import com.github.a5809909.gps_finder.Sql.DatabaseHelper;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static com.github.a5809909.gps_finder.Utilities.Constants.NOTIFICATION_ID.GOOGLE_GEOCODING_API_KEY;
import static com.github.a5809909.gps_finder.Utilities.Constants.NOTIFICATION_ID.GOOGLE_GEOCODING_URI;
import static com.github.a5809909.gps_finder.Utilities.Constants.NOTIFICATION_ID.GOOGLE_GEOLOCATE_API_KEY;
import static com.github.a5809909.gps_finder.Utilities.Constants.NOTIFICATION_ID.GOOGLE_GEOLOCATE_URI;

public class LocationLoaderAsyncTask extends AsyncTask<LocationModel, Void, LocationModel> {

    private String TAG = "AsyncTask";
    LocationModel mLocationModel;
    Context instance;
    IAsyncTaskListener mIAsyncTaskListener;
    private DatabaseHelper databaseHelper;

    public LocationLoaderAsyncTask(Context pContext, IAsyncTaskListener pIAsyncTaskListener) {
        mIAsyncTaskListener = pIAsyncTaskListener;
        instance = pContext;
    }

    @Override
    protected LocationModel doInBackground(LocationModel... params) {
        mLocationModel = params[0];
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httpost = new HttpPost(GOOGLE_GEOLOCATE_URI + GOOGLE_GEOLOCATE_API_KEY);
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
            mLocationModel.setJson_first(rootObject.toString());
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            String response = httpclient.execute(httpost, responseHandler);
            JSONObject jsonResult = new JSONObject(response);
            JSONObject location = jsonResult.getJSONObject("location");
            mLocationModel.setAcc(jsonResult.getString("accuracy"));
            mLocationModel.setLat(location.getString("lat"));
            mLocationModel.setLng(location.getString("lng"));
            String latlng = mLocationModel.getLat() + "," + mLocationModel.getLng();

            List<String> items = new ArrayList<>();
            String url = Uri.parse(GOOGLE_GEOCODING_URI)
                    .buildUpon()
                    .appendQueryParameter("key", GOOGLE_GEOCODING_API_KEY)
                    .appendQueryParameter("latlng", latlng)
                    .appendQueryParameter("language", "ru")
                    //  .appendQueryParameter("result_type", "country|street_address|postal_code")

                    .build().toString();

            String jsonString = getUrlString(url);
            Log.i(TAG, "Received JSON: " + jsonString);
            JSONObject jsonBody = new JSONObject(jsonString);
            JSONArray addressJsonArray = jsonBody.getJSONArray("results");
            JSONObject addressObject = addressJsonArray.getJSONObject(0);
            addressObject.length();
            String address = addressObject.getString("formatted_address");
            mLocationModel.setAddress(address);
            databaseHelper = new DatabaseHelper(instance);
            databaseHelper.addUser(mLocationModel);
            databaseHelper.close();
        } catch (Exception e) {
            final String err = e.getMessage();
            Log.i(TAG, "doInBackground: " + err);
            mLocationModel.setErrors(err);
        }
        return mLocationModel;
    }

    @Override
    protected void onPostExecute(LocationModel pLocationModel) {
        super.onPostExecute(pLocationModel);

        mIAsyncTaskListener.finishedAsyncTask();
    }

    public byte[] getUrlBytes(String urlSpec) throws IOException {
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(connection.getResponseMessage() +
                        ": with " +
                        urlSpec);
            }
            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            return out.toByteArray();
        } finally {
            connection.disconnect();
        }
    }

    public String getUrlString(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }

}