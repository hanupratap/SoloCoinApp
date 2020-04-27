package com.example.solocoin;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.location.Address;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.os.Parcelable;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import io.paperdb.Paper;

import static com.example.solocoin.App.CHANNEL_ID;



public class Service1 extends Service {

    FusedLocationProviderClient fusedLocationProviderClient;
    Location currentLocation;
    private LatLng latLng;
    float dist[]  = new float[10];
    boolean isserviceRunning = true;
    int points = 0;
    Timer timer;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    void timer()
    {
        new CountDownTimer(1000*60*10, 1000) {

            public void onTick(long millisUntilFinished) {

                Paper.book().write("time", millisUntilFinished);
                if(isserviceRunning==false)
                {
                    cancel();
                }
            }

            public void onFinish() {
                start();
            }
        }.start();



    }


    void getlocation() {

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            final Task location = fusedLocationProviderClient.getLastLocation();
            location.addOnCompleteListener(new OnCompleteListener() {
                @SuppressLint("MissingPermission")
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful())
                    {
                        if(task.getResult()!=null)
                        {

                            if(isserviceRunning==false)
                            {
                                timer.cancel();
                                timer.purge();
                            }
                            else
                            {

                                currentLocation = (Location) task.getResult();


                                latLng = Paper.book().read("position");

                                Location.distanceBetween(latLng.latitude, latLng.longitude, currentLocation.getLatitude(), currentLocation.getLongitude(), dist);
                                if(dist[0]<100)
                                {
                                    points = points + 10;
                                    Toast.makeText(Service1.this, "10 points added, distance = " + dist[0], Toast.LENGTH_SHORT).show();

                                }

                                else
                                {
                                    if(points!=0)
                                    {
                                        points = points - 10;
                                        Toast.makeText(Service1.this, "10 Points deducted, distance = " + dist[0], Toast.LENGTH_SHORT).show();
                                    }


                                }


                                Paper.book().write("points", points);
                            }



                        }
                        else
                        {
                            LocationListener locationListener;
                            locationListener = new LocationListener() {
                                @Override
                                public void onLocationChanged(Location location) {
                                    currentLocation = location;
                                }

                                @Override
                                public void onStatusChanged(String s, int i, Bundle bundle) {

                                }

                                @Override
                                public void onProviderEnabled(String s) {

                                }

                                @Override
                                public void onProviderDisabled(String s) {
                                    Toast.makeText(Service1.this, "Please Turn on Location Services", Toast.LENGTH_SHORT).show();

                                }
                            };

                            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                            locationManager.requestLocationUpdates("gps", 5000,0, locationListener);

                        }
                    }
                    else
                    {
                        Toast.makeText(Service1.this, "Location not found", Toast.LENGTH_SHORT).show();
                    }
                }
            });


        }
        catch (Exception e)
        {

        }

    }




    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


            Toast.makeText(this, "Service started" + flags + startId, Toast.LENGTH_SHORT).show();

            latLng = (LatLng) intent.getParcelableExtra("position");
            Intent notificationIntent = new Intent(Service1.this, SecondActivity.class);
            notificationIntent.putExtra("position", latLng);
            PendingIntent pendingIntent = PendingIntent.getActivity(Service1.this,
                    0, notificationIntent, 0);


            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("SoloCoin")
                    .setContentText("Lat: " + latLng.latitude + "& lon: " + latLng.longitude)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentIntent(pendingIntent)
                    .build();
            startForeground(1, notification);




            timer = new Timer ();
            TimerTask hourlyTask = new TimerTask () {
                @Override
                public void run () {
                    // your code here...
                    if(isserviceRunning==false)
                    {
                        timer.cancel();
                        timer.purge();
                    }

                    getlocation();

                }
            };

// schedule the task to run starting now and then every hour...
            timer.schedule (hourlyTask, 0l, 1000*60*10 );   // 1000*10*60 every 10 minutes


        return START_NOT_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Paper.init(this);
        timer();
        if(Paper.book().read("points")!=null)
            points = Paper.book().read("points");
        else
            Paper.book().write("points", 0);
        Paper.book().write("service", 1);
        Toast.makeText(this, "Created", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        isserviceRunning=false;
        Paper.book().write("service", 0);
    }
}
