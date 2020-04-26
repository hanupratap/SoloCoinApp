package com.example.solocoin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;

import java.util.Timer;
import java.util.TimerTask;

import io.paperdb.Paper;

public class SecondActivity extends AppCompatActivity {
    private Location currentLocation;
    Button cs, stop, start;
    FusedLocationProviderClient fusedLocationProviderClient;
    CoordinatorLayout linear;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        cs = findViewById(R.id.checkScore);
        stop = findViewById(R.id.stopService);
        start = findViewById(R.id.startService);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startService();
            }
        });
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService();
            }
        });


        cs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SecondActivity.this, DisplaPoints.class));
            }
        });


    }

    void startService()
    {
        Paper.init(this);
        Intent serviceIntent = new Intent(this, Service1.class);
        LatLng latLng = Paper.book().read("position");
        Toast.makeText(this, latLng.toString(), Toast.LENGTH_SHORT).show();

        serviceIntent.putExtra("position", latLng);
        startService(serviceIntent);
    }

    void stopService()
    {

        Intent serviceIntent = new Intent(this, Service1.class);
        stopService(serviceIntent);
    }



}
