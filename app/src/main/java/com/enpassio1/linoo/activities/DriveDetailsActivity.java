package com.enpassio1.linoo.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.enpassio1.linoo.R;
import com.enpassio1.linoo.fragments.DriveDetailsFragment;

public class DriveDetailsActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drive_details);

        Bundle driveDetailsBundle = getIntent().getBundleExtra(getResources().getString
                (R.string.drive_details_bundle_key));

        DriveDetailsFragment driveDetailsFragment = new DriveDetailsFragment();
        driveDetailsFragment.setArguments(driveDetailsBundle);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_drive_details_container, driveDetailsFragment)
                .commit();
    }
}
