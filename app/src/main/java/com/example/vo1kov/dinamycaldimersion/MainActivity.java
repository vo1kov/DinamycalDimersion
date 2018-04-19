package com.example.vo1kov.dinamycaldimersion;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.backendless.Backendless;


public class MainActivity extends AppCompatActivity {
    final String LOG_TAG = "MMV";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_main);

        Backendless.setUrl(Defaults.SERVER_URL);
        Backendless.initApp(getApplicationContext(), Defaults.APPLICATION_ID, Defaults.API_KEY);
        statusTextView = findViewById(R.id.textViewStatus);



        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_FINE_LOCATION}, 101);
        }
    }

    TextView statusTextView;

    public void onClickRecOff(View view) {

        stopService(new Intent(this, GPSService.class));
        statusTextView.setText("Включите запись");
    }

    public void onClickRecOn(View view) {
        statusTextView = findViewById(R.id.textViewStatus);

        Intent intent = new Intent(this, GPSService.class);
        intent.putExtra("user", ((EditText) findViewById(R.id.name)).getText().toString());
        startService(intent);
        statusTextView.setText("Идет запись в фоне");
    }
}
