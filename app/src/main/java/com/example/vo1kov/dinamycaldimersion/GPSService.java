package com.example.vo1kov.dinamycaldimersion;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by vo1kov on 19.04.18.
 */

public class GPSService extends Service {

    final String LOG_TAG = "myLogs";
    int count = 0;
    ArrayList<Double> lattitude = new ArrayList<>();
    ArrayList<Double> longitude = new ArrayList<>();
    ArrayList<Double> altitude = new ArrayList<>();
    ArrayList<Float> speed = new ArrayList<>();
    ArrayList<Float> accuracy = new ArrayList<>();
    ArrayList<Long> time = new ArrayList<>();
    private LocationManager locationManager;
    private LocationListener locationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            //String name = "serv_1";//((EditText) findViewById(R.id.name)).getText().toString();
            storeLocation(location, name);
        }

        @Override
        public void onProviderDisabled(String provider) {

        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            if (provider.equals(LocationManager.GPS_PROVIDER)) {
                Log.d(LOG_TAG, "GPS - Status: " + String.valueOf(status));
            } else if (provider.equals(LocationManager.NETWORK_PROVIDER)) {
                Log.d(LOG_TAG, "GSM - Status: " + String.valueOf(status));
            }
        }
    };

    public void onCreate() {
        super.onCreate();

        Notification.Builder builder = new Notification.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher);//.setSubText("Идет запись");
        Notification notification;
        if (Build.VERSION.SDK_INT < 16)
            notification = builder.getNotification();
        else if (Build.VERSION.SDK_INT < 26)
            notification = builder.build();
        else {
            notification = builder.setChannelId("777").build();
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            NotificationChannel channel = new NotificationChannel("777","gps" , NotificationManager.IMPORTANCE_DEFAULT);
            channel.setShowBadge(false);
            channel.setSound(null, null);
            notificationManager.createNotificationChannel(channel);
        }
        startForeground(777, notification);


        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

        Log.d(LOG_TAG, "location manager создан");
        List<String> prov = locationManager.getAllProviders();

        for (String pr : prov)
            Log.d("1MMV", pr);

        Log.d(LOG_TAG, "onCreate");
    }

    private void storeLocation(Location location, String name) {
        longitude.add(location.getLongitude());
        lattitude.add(location.getLatitude());
        altitude.add(location.getAltitude());
        speed.add(location.getSpeed());
        accuracy.add(location.getAccuracy());
        time.add(location.getTime());
        count++;

        Log.d("MMV", "Status:" + Arrays.toString(time.toArray()));

        if (count % 10 == 0) {
            count = 0;
            HashMap testObject = new HashMap<>();
            testObject.put("name", name);
            testObject.put("longitude", Arrays.toString(longitude.toArray()));
            testObject.put("lattitude", Arrays.toString(lattitude.toArray()));
            testObject.put("altitude", Arrays.toString(altitude.toArray()));
            testObject.put("speed", Arrays.toString(speed.toArray()));
            testObject.put("accuracy", Arrays.toString(accuracy.toArray()));
            testObject.put("time", Arrays.toString(time.toArray()));

            longitude.clear();
            lattitude.clear();
            altitude.clear();
            speed.clear();
            accuracy.clear();
            time.clear();

            Backendless.Data.of("Locations").save(testObject, new AsyncCallback<Map>() {
                @Override
                public void handleResponse(Map response) {
                    Log.d("MMV", "saved in Backendless. Please check in the console.");
                }

                @Override
                public void handleFault(BackendlessFault fault) {
                    Log.e("MYAPP", "Server reported an error " + fault.getMessage());
                }
            });

        }


    }

    String name = "1";

    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(LOG_TAG, "onStartCommand");

        name = intent.getExtras().getString("user", "unknown_user");

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        return super.onStartCommand(intent, flags, startId);
    }

    public void onDestroy() {
        super.onDestroy();

        try {
            locationManager.removeUpdates(locationListener);
        } catch (Exception e) {
            Log.e("MMV", e.getMessage());

        }

        Log.d(LOG_TAG, "onDestroy");
    }

    public IBinder onBind(Intent intent) {
        Log.d(LOG_TAG, "onBind");
        return null;
    }


}