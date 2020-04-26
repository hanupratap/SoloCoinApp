package com.example.solocoin;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Point;
import android.os.Bundle;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.Timer;
import java.util.TimerTask;

import io.paperdb.Paper;

public class DisplaPoints extends AppCompatActivity {
    TextView tv;
    int points = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_displa_points);
        tv = findViewById(R.id.textView);
        Paper.init(DisplaPoints.this);

        if(Paper.book().read("points")!=null)
        {
            points = Paper.book().read("points");
        }


        Timer timer = new Timer ();
        TimerTask hourlyTask = new TimerTask () {
            @Override
            public void run () {
                // your code here...
                if(Paper.book().read("points")!=null)
                {
                    points = Paper.book().read("points");
                }

                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

                        // Stuff that updates the UI
                        tv.setText("Points : " + points);
                    }

                });
            }
        };

// schedule the task to run starting now and then every hour...
        timer.schedule (hourlyTask, 0l, 10 );   // 1000*10*60 every 10 minut





    }
}
