package com.example.vo1kov.dinamycaldimersion;

import android.location.GpsStatus;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
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
import android.os.Build;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Button;

import org.w3c.dom.Text;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends ActionBarActivity {


    // @InjectView(R.id.textView1 ) TextView status;
    // @InjectView(R.id.button ) Button start;

    final String DIR_SD = "GPSTracks";
    final String LOG_TAG = "MMV";
    StringBuilder sbGPS = new StringBuilder();
    StringBuilder sbNet = new StringBuilder();
    TextView status;
    String FILENAME_SD = "gps.txt";
    TextView textStatus;
    Timer timer;
    GPSPoint p;
    long rec;
    ProgressBar progSat;

    ArrayList<String> nmeaPref;

    GpsStatus.NmeaListener nmeaListener = new GpsStatus.NmeaListener() {
        @Override
        public void onNmeaReceived(long timestamp, String nmea) {
            String pref=nmea.substring(1,6);
            if(nmeaPref.contains(pref)==false)
            {
            nmeaPref.add(pref);
                Log.d("2MMV",nmea);
            }
            //Log.d("1MMV", "NMEA " + nmea);
        }
    };


    private LocationListener locationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            showLocation(location);
            int sat = 0;
            if (location.getExtras() != null) sat = location.getExtras().getInt("satellites");


            p = new GPSPoint(location.getLatitude(), location.getLongitude(), location.getSpeed(), location.getAccuracy(), location.getTime(), sat);
            //Log.d(LOG_TAG,"p созданно");


            //String s = p.ToString();

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
    nmeaPref = new ArrayList<>();


        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }


        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        Log.d(LOG_TAG, "location manager создан");
        List<String> prov = locationManager.getAllProviders();

        for(String pr :prov)
            Log.d("1MMV",pr);

        record = new ArrayList<GPSPoint>();


    }

    @Override
    protected void onResume() {
        super.onResume();
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                1000 * 1, 10, locationListener);
        //checkEnabled();

        locationManager.addNmeaListener(nmeaListener);

        timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        addPoint();
                    }
                });
            }
        };
        timer.schedule(task, 0, 1000);


    }

    void addPoint() {
        if (p != null) {
            //record.add(new GPSPoint(p));
            p.renewTime(new Date().getTime());
            if (textStatus == null) textStatus = (TextView) findViewById(R.id.textViewStatus);
            if(progSat == null) progSat = (ProgressBar) findViewById(R.id.progressBar);
            progSat.setMax(24);
            progSat.setProgress(p.getSat());


            if (bw != null) {
                try {
                    rec++;
                    bw.write(p.toTxtString());
                    textStatus.setText(p.ToString() + String.format("\nИДЕТ ЗАПИСЬ - %1$d",rec));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else
            {
                textStatus.setText(p.ToString() + "\nЗАПИСЬ ВЫКЛЮЧЕНА");
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(locationListener);
        timer.cancel();
        if (bw != null)
            try {
                bw.close();
                bw=null;
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

    private String formatLocation(Location location) {
        if (location == null)
            return "";
        return String.format(
                "Coordinates: lat = %1$.4f, lon = %2$.4f, time = %3$tF %3$tT",
                location.getLatitude(), location.getLongitude(), new Date(
                        location.getTime()));
    }

    //@OnClick(R.id.button)
    //public void submit(View view) {
    //    status.setText("Ололололо!");
    //}

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
                bw=null;
            } catch (IOException e) {
                e.printStackTrace();
            }

    }

    public void onClickRecOn(View view) {
        rec=0;
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
        File sdFile = new File(sdPath, String.format("%1$tF_%1$tT.txt",new Date()));
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
