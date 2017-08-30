package com.enpassio1.linoo.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.enpassio1.linoo.R;
import com.enpassio1.linoo.models.UpcomingDrives;

public class DriveDetailsActivity extends AppCompatActivity {

    TextView companyNameTextView;
    TextView hiringDateTextView;
    TextView hiringLocationTextView;
    TextView hiringPositionTextView;
    TextView driveDetailsTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drive_details);
        Bundle driveDetailsBundle = getIntent().getBundleExtra(getResources().getString
                (R.string.drive_details_bundle_key));
        UpcomingDrives currentUpcomingDrive = driveDetailsBundle.getParcelable
                (getResources().getString(R.string.current_upcoming_drive_key));

        //instantiate the views
        companyNameTextView = (TextView) findViewById(R.id.company_name_text_view);
        hiringDateTextView = (TextView) findViewById(R.id.hiring_date_text_view);
        hiringLocationTextView = (TextView) findViewById(R.id.hiring_place_text_view);
        hiringPositionTextView = (TextView) findViewById(R.id.job_title_text_view);
        driveDetailsTextView = (TextView) findViewById(R.id.job_description_text_view);

        //inflate the views
        companyNameTextView.setText(currentUpcomingDrive.getCompanyName());
        hiringDateTextView.setText(currentUpcomingDrive.getDriveDate());
        hiringLocationTextView.setText(currentUpcomingDrive.getPlace());
        hiringPositionTextView.setText(currentUpcomingDrive.getJobPosition());
        driveDetailsTextView.setText(currentUpcomingDrive.getDetailedDescription());
    }
}
