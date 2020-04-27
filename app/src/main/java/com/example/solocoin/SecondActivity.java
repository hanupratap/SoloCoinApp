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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;

import io.paperdb.Paper;

public class SecondActivity extends AppCompatActivity {
    private Location currentLocation;
    Button cs, stop, start;
    TextView time;
    FusedLocationProviderClient fusedLocationProviderClient;
    CoordinatorLayout linear;
    float timeval;
    boolean timerunning = false;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        cs = findViewById(R.id.checkScore);
        stop = findViewById(R.id.stopService);
        time = findViewById(R.id.timer);
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
        progressBar = findViewById(R.id.progressBar);

        progressBar.setMax(100);
        progressBar.setMin(0);
        Paper.init(this);


        Timer timer = new Timer ();
        TimerTask hourlyTask = new TimerTask () {
            @Override
            public void run () {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

                        // Stuff that updates the UI
                        progressBar.setProgress(Math.round(timeval*10));
                        DecimalFormat df = new DecimalFormat("#.#");
                        df.setRoundingMode(RoundingMode.CEILING);
                        timeval = Float.parseFloat(String.valueOf(Paper.book().read("time")))/60000;
                        time.setText("Time Remaining (in mins) : " + df.format(timeval));


                            if((int)Paper.book().read("service")==0)
                            {
                                start.setEnabled(true);
                            }
                            else {
                                start.setEnabled(false);
                            }



                    }

                });
            }
        };
        timer.schedule (hourlyTask, 0l, 10 );



        cs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SecondActivity.this, DisplaPoints.class));
            }
        });

    }



    void startService()
    {
        timerunning = true;

        Intent serviceIntent = new Intent(this, Service1.class);
        LatLng latLng = Paper.book().read("position");
        Toast.makeText(this, latLng.toString(), Toast.LENGTH_SHORT).show();
        serviceIntent.putExtra("position", latLng);
        startService(serviceIntent);
    }

    void stopService()
    {
        timerunning = false;
        Intent serviceIntent = new Intent(this, Service1.class);
        stopService(serviceIntent);
    }



}
