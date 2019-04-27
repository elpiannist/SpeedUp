package com.example.speedup;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements LocationListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        final TextView text_speed = findViewById(R.id.current_speed);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 0, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        TextView text_speed = findViewById(R.id.current_speed);
        double thespeed=location.getSpeed()*3.6;
        String formatted = String.format("%.1f",thespeed);
        text_speed.setText(formatted+" km/h");
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
