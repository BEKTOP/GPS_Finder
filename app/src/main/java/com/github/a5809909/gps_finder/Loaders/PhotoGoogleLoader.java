package com.github.a5809909.gps_finder.Loaders;

import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;

import com.github.a5809909.gps_finder.Model.GalleryItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static com.github.a5809909.gps_finder.Utilities.Constants.NOTIFICATION_ID.FETCHR_API_KEY;
import static com.github.a5809909.gps_finder.Utilities.Constants.NOTIFICATION_ID.FETCHR_URI;

public class PhotoGoogleLoader {

    private static final String TAG = "PhotoGoogle";


    private String lat;
    private String lon;
    private String acc;
    SharedPreferences sPref;


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

    public List<GalleryItem> fetchItems() {
//        sPref = MainActivity.class.getSharedPreferences("MyPref", MODE_PRIVATE);
//        lat = sPref.getString("lat", "");
//        lon = sPref.getString("lng", "").substring(0,7);
//        acc = sPref.getString("accuracy", "");
//        lat = "53.68949";
//        lon = "23.80334";
//            String url = Uri.parse(GOOGLE_PLACES_URI)
//                    .buildUpon()
//                    .appendQueryParameter("key", GOOGLE_PLACES_API_KEY)
//                    .appendQueryParameter("location", acc)
//                    .appendQueryParameter("radius", "500")
        acc = "grodno";
        List<GalleryItem> items = new ArrayList<>();

        try {
            String url = Uri.parse(FETCHR_URI)
                    .buildUpon()
                    .appendQueryParameter("method", "flickr.photos.search")
                    //                   .appendQueryParameter("method", "flickr.photos.getRecent")
                    .appendQueryParameter("api_key", FETCHR_API_KEY)

                    .appendQueryParameter("place_id", "EYkx6bBZUL_1FVA")
                    .appendQueryParameter("format", "json")
             //       .appendQueryParameter("text", acc)
                    .appendQueryParameter("nojsoncallback", "1")
                    .appendQueryParameter("extras", "url_s")
                    .build().toString();
            Log.i(TAG, "Send url: " + url.toString());
            String jsonString = getUrlString(url);
            Log.i(TAG, "Received JSON: " + jsonString);
            JSONObject jsonBody = new JSONObject(jsonString);
            parseItems(items, jsonBody);
        } catch (IOException ioe) {
            Log.e(TAG, "Failed to fetch items", ioe);
        } catch (JSONException je) {
            Log.e(TAG, "Failed to parse JSON", je);
        }

        return items;
    }

    private void parseItems(List<GalleryItem> items, JSONObject jsonBody)
            throws IOException, JSONException {

        JSONObject photosJsonObject = jsonBody.getJSONObject("photos");
        JSONArray photoJsonArray = photosJsonObject.getJSONArray("photo");
        String[] imageSmallUrls = new String[photoJsonArray.length()];
        for (int i = 0; i < photoJsonArray.length(); i++) {
            JSONObject photoJsonObject = photoJsonArray.getJSONObject(i);

            GalleryItem item = new GalleryItem();
            item.setId(photoJsonObject.getString("id"));
            item.setCaption(photoJsonObject.getString("title"));

            if (!photoJsonObject.has("url_s")) {
                continue;
            }

            item.setUrl(photoJsonObject.getString("url_s"));


            items.add(item);

        }
        for (int j = 0; j <items.size() ; j++) {
            imageSmallUrls[j]=items.get(j).getUrl();
            Log.i(TAG, "fetchItems: " +imageSmallUrls[j]);
        }

    }

}