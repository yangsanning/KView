package ysn.com.kview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.Random;

import ysn.com.kview.mode.bean.TimeSharing;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        KView kView = findViewById(R.id.k_view);
        Random random = new Random();
        TimeSharing timeSharing = new TimeSharing();
        for (int i = 1; i < 240; i++) {
            if (i % 2 == 0) {
                timeSharing.stockPrice += String.valueOf(4000 + random.nextInt(10)) + ",";
                timeSharing.stockVolume += String.valueOf(10000 + random.nextInt(10000)) + ",";
            } else {
                timeSharing.stockPrice += String.valueOf(4000 - random.nextInt(10)) + ",";
                timeSharing.stockVolume += String.valueOf(10000 - random.nextInt(10000)) + ",";
            }
        }
        timeSharing.stockPrice += "4010";
        timeSharing.stockVolume += "15000";
        timeSharing.lastClose = 4000;
        kView.setDate(timeSharing);
    }
}
