package com.enpassio.linoo.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.enpassio.linoo.R;
import com.enpassio.linoo.models.UpcomingDrives;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ABHISHEK RAJ on 8/31/2017.
 */

public class DriveDetailsFragment extends Fragment {

    @BindView(R.id.company_name_text_view)
    TextView companyNameTextView;

    @BindView(R.id.hiring_date_text_view)
    TextView hiringDateTextView;

    @BindView(R.id.hiring_place_text_view)
    TextView hiringLocationTextView;

    @BindView(R.id.job_title_text_view)
    TextView hiringPositionTextView;

    @BindView(R.id.job_description_text_view)
    TextView driveDetailsTextView;

    public DriveDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_drive_details, container, false);
        ButterKnife.bind(this, rootView);
        Bundle driveDetailsBundle = getArguments();
        UpcomingDrives currentUpcomingDrive = driveDetailsBundle.getParcelable
                (getResources().getString(R.string.current_upcoming_drive_key));

        //inflate the views
        companyNameTextView.setText(currentUpcomingDrive.getCompanyName());
        hiringDateTextView.setText(currentUpcomingDrive.getDriveDate());
        hiringLocationTextView.setText(currentUpcomingDrive.getPlace());
        hiringPositionTextView.setText(currentUpcomingDrive.getJobPosition());
        driveDetailsTextView.setText(currentUpcomingDrive.getDetailedDescription());
        return rootView;
    }
}