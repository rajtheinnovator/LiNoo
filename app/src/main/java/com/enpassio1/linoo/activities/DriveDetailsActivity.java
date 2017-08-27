package com.enpassio1.linoo.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.enpassio1.linoo.R;
import com.enpassio1.linoo.models.UpcomingDrives;

public class DriveDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drive_details);
        Bundle driveDetailsBundle = getIntent().getBundleExtra("driveDetailsBundle");
        UpcomingDrives currentUpcomingDrive = driveDetailsBundle.getParcelable("currentUpcomingDrives");
    }
}
