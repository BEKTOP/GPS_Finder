package com.github.a5809909.gps_finder.Service;

import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.telephony.CellLocation;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.github.a5809909.gps_finder.Activity.MainActivity;
import com.github.a5809909.gps_finder.R;
import com.github.a5809909.gps_finder.Utilities.Constants;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;


public class LogService extends Service {
    private static final String LOG_TAG = "LogService";
    private boolean isRun;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getAction().equals(Constants.ACTION.STARTFOREGROUND_ACTION)) {
            Log.i(LOG_TAG, "Received Start Foreground Intent ");
           isRun=true;
            Intent notificationIntent = new Intent(this, MainActivity.class);
            notificationIntent.setAction(Constants.ACTION.MAIN_ACTION);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                    notificationIntent, 0);

            Bitmap icon = BitmapFactory.decodeResource(getResources(),
                    R.drawable.ic_pin_drop);

            Notification notification = new NotificationCompat.Builder(this)
                    .setContentTitle("GPS Finder")
                    .setTicker("Truiton Music Player")
                    .setContentText("My GPS")
                    .setSmallIcon(R.drawable.map_marker)
                    .setLargeIcon(
                            Bitmap.createScaledBitmap(icon, 128, 128, false))
                    .setContentIntent(pendingIntent)
                    .setOngoing(true).build();
            startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE,
                    notification);
        } else if (intent.getAction().equals(
                Constants.ACTION.STOPFOREGROUND_ACTION)) {
            Log.i(LOG_TAG, "Received Stop Foreground Intent");
            isRun=false;
            stopForeground(true);
            stopSelf();

        }
      //  while (isRun) {
        getLocationClicked();
//            try {
//                Thread.sleep(10000);
//            } catch (InterruptedException ie) {
//            }
//        }


        return START_STICKY;
    }

    public void getLocationClicked() {

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

            } else {

            }
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(LOG_TAG, "In onDestroy");
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Used only in case of bound services.
        return null;
    }
}
