package com.example.vo1kov.dinamycaldimersion;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

//import
import android.widget.ProgressBar;
import android.widget.TextView;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity {
    final String DIR_SD = "GPSTracks";
    final String LOG_TAG = "MMV";
    StringBuilder sbGPS = new StringBuilder();
    StringBuilder sbNet = new StringBuilder();
    TextView status;
    String FILENAME_SD = "gps.txt";
    TextView textStatus;
    // Timer timer;
    GPSPoint p;
    long rec;
    ProgressBar progSat;

    ArrayList<String> nmeaPref;

    /*GpsStatus.NmeaListener nmeaListener = new GpsStatus.NmeaListener() {
        @Override
        public void onNmeaReceived(long timestamp, String nmea) {
            String pref=nmea.substring(1,6);
            if(nmeaPref.contains(pref)==false)
            {
            nmeaPref.add(pref);
                Log.d("2MMV",nmea);
            }

            NMEAMessage m = new NMEAMessage(nmea);

            if(m.Type!=null)
            {
                Log.d("QQQ","");

            }
            //Log.d("1MMV", "NMEA " + nmea);
        }
    };*/


    private LocationListener locationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            showLocation(location);
            storeLocation(formatLocation(location));
            int sat = 0;
            if (location.getExtras() != null) sat = location.getExtras().getInt("satellites");


            p = new GPSPoint(location.getLatitude(), location.getLongitude(), location.getSpeed(), location.getAccuracy(), location.getTime(), sat);


            if (p != null) {
                if (textStatus == null) textStatus = (TextView) findViewById(R.id.textViewStatus);
                if (progSat == null) progSat = (ProgressBar) findViewById(R.id.progressBar);
                progSat.setMax(24);
                progSat.setProgress(p.getSat());


                if (bw != null) {
                    try {
                        rec++;
                        bw.write(p.toTxtString());
                        textStatus.setText(p.ToString() + String.format("\nИДЕТ ЗАПИСЬ - %1$d", rec));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    textStatus.setText(p.ToString() + "\nЗАПИСЬ ВЫКЛЮЧЕНА");
                }
            }
        }

        @Override
        public void onProviderDisabled(String provider) {
            checkEnabled();
        }

        @Override
        public void onProviderEnabled(String provider) {
            checkEnabled();
            showLocation(locationManager.getLastKnownLocation(provider));
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            if (provider.equals(LocationManager.GPS_PROVIDER)) {
                Log.d(LOG_TAG, "GPS - Status: " + String.valueOf(status));
                textStatus.setText("GPS - Status: " + String.valueOf(status));

            } else if (provider.equals(LocationManager.NETWORK_PROVIDER)) {
                Log.d(LOG_TAG, "GSM - Status: " + String.valueOf(status));
            }
        }
    };
    ArrayList<GPSPoint> record;
    boolean recOn = false;
    BufferedWriter bw;
    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Backendless.setUrl( Defaults.SERVER_URL );
        Backendless.initApp( getApplicationContext(), Defaults.APPLICATION_ID, Defaults.API_KEY );

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_FINE_LOCATION}, 101);
        }


        nmeaPref = new ArrayList<>();


        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

        Log.d(LOG_TAG, "location manager создан");
        List<String> prov = locationManager.getAllProviders();

        for (String pr : prov)
            Log.d("1MMV", pr);

        record = new ArrayList<GPSPoint>();


    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            locationManager.removeUpdates(locationListener);
        } catch (Exception e) {
            Log.e("MMV", e.getMessage());

        }


        if (bw != null)
            try {
                bw.close();
                bw = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    private void showLocation(Location location) {
        if (location == null)
            return;
        if (location.getProvider().equals(LocationManager.GPS_PROVIDER)) {
            Log.d(LOG_TAG, "GPS + " + formatLocation(location));



        } else if (location.getProvider().equals(
                LocationManager.NETWORK_PROVIDER)) {
            Log.d(LOG_TAG, "GSM + " + formatLocation(location));
        }
    }


    private void storeLocation(String location)
    {
        HashMap testObject = new HashMap<>();
        testObject.put( "data", location );
        Backendless.Data.of( "TestTable" ).save(testObject, new AsyncCallback<Map>() {
            @Override
            public void handleResponse(Map response) {
                Log.d("MMV","saved in Backendless. Please check in the console.");
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Log.e( "MYAPP", "Server reported an error " + fault.getMessage() );
            }
        });

    }

    private String formatLocation(Location location) {
        if (location == null)
            return "";
        return String.format(
                "Coordinates: lat = %1$.4f, lon = %2$.4f, time = %3$tF %3$tT",
                location.getLatitude(), location.getLongitude(), new Date(
                        location.getTime()));
    }

    private void checkEnabled() {
        Log.d(LOG_TAG, "Enabled: "
                + locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER));
        Log.d(LOG_TAG, "Enabled: "
                + locationManager
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void onClickRecOff(View view) {

        nmeaPref.clear();
        if (bw != null)
            try {
                bw.close();
                bw = null;
            } catch (IOException e) {
                e.printStackTrace();
            }

    }

    public void onClickRecOn(View view) {
        rec = 0;
        // проверяем доступность SD
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            Log.d(LOG_TAG, "SD-карта не доступна: " + Environment.getExternalStorageState());
            return;
        }
        // получаем путь к SD
        File sdPath = Environment.getExternalStorageDirectory();
        // добавляем свой каталог к пути
        sdPath = new File(sdPath.getAbsolutePath() + "/" + DIR_SD);
        // создаем каталог
        sdPath.mkdirs();
        // формируем объект File, который содержит путь к файлу
        File sdFile = new File(sdPath, String.format("%1$tF_%1$tT.txt", new Date()));
        try {
            // открываем поток для записи
            bw = new BufferedWriter(new FileWriter(sdFile));

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }
}
