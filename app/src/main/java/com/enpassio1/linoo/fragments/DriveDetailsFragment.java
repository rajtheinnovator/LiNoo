package com.enpassio1.linoo.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.enpassio1.linoo.R;
import com.enpassio1.linoo.models.UpcomingDrives;

/**
 * Created by ABHISHEK RAJ on 8/31/2017.
 */

public class DriveDetailsFragment extends Fragment {
    TextView companyNameTextView;
    TextView hiringDateTextView;
    TextView hiringLocationTextView;
    TextView hiringPositionTextView;
    TextView driveDetailsTextView;

    public DriveDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_drive_details, container, false);

        Bundle driveDetailsBundle = getArguments();
        UpcomingDrives currentUpcomingDrive = driveDetailsBundle.getParcelable
                (getResources().getString(R.string.current_upcoming_drive_key));

        //instantiate the views
        companyNameTextView = (TextView) rootView.findViewById(R.id.company_name_text_view);
        hiringDateTextView = (TextView) rootView.findViewById(R.id.hiring_date_text_view);
        hiringLocationTextView = (TextView) rootView.findViewById(R.id.hiring_place_text_view);
        hiringPositionTextView = (TextView) rootView.findViewById(R.id.job_title_text_view);
        driveDetailsTextView = (TextView) rootView.findViewById(R.id.job_description_text_view);

        //inflate the views
        companyNameTextView.setText(currentUpcomingDrive.getCompanyName());
        hiringDateTextView.setText(currentUpcomingDrive.getDriveDate());
        hiringLocationTextView.setText(currentUpcomingDrive.getPlace());
        hiringPositionTextView.setText(currentUpcomingDrive.getJobPosition());
        driveDetailsTextView.setText(currentUpcomingDrive.getDetailedDescription());
        return rootView;
    }
}