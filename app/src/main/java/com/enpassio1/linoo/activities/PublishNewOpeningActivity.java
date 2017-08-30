package com.enpassio1.linoo.activities;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.enpassio1.linoo.R;
import com.enpassio1.linoo.fragments.DatePickerFragment;
import com.enpassio1.linoo.models.UpcomingDrives;
import com.enpassio1.linoo.utils.InternetConnectivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PublishNewOpeningActivity extends AppCompatActivity implements DatePickerFragment.OnDateSetListener {

    EditText companyNameEditText;
    EditText hiringDateEditText;
    EditText hiringPlaceEditText;
    EditText jobPositionEditText;
    EditText jobDescriptionEditText;
    Button publishButton;

    String companyName;
    String recruitmentDate;
    String recruitmentPlace;
    String jobPosition;
    String driveDetails;

    /*
    * Firebase instance variables
    **/
    /*
    * firebase code referenced from: https://github.com/udacity/and-nd-firebase
    */

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDrivesDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_new_opening);

        companyNameEditText = (EditText) findViewById(R.id.company_name_edit_text);
        hiringDateEditText = (EditText) findViewById(R.id.recruitment_date_edit_text);

        hiringDateEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    DialogFragment newFragment = new DatePickerFragment();
                    newFragment.show(getSupportFragmentManager(), "datePicker");
                } else {
                    //do next step if focus is not on edittext
                }
            }
        });
        hiringPlaceEditText = (EditText) findViewById(R.id.recruitment_place_edit_text);
        jobPositionEditText = (EditText) findViewById(R.id.job_position_edit_text);
        jobDescriptionEditText = (EditText) findViewById(R.id.drive_details__edit_text);
        publishButton = (Button) findViewById(R.id.publish_button);

        if (InternetConnectivity.isInternetConnected(PublishNewOpeningActivity.this)) {
            mFirebaseDatabase = FirebaseDatabase.getInstance();
            mDrivesDatabaseReference = mFirebaseDatabase.getReference().child(getResources().getString(R.string.firebase_database_child_drives));

            publishButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    companyName = companyNameEditText.getText().toString().trim();
                    recruitmentDate = hiringDateEditText.getText().toString().trim();
                    recruitmentPlace = hiringPlaceEditText.getText().toString().trim();
                    jobPosition = jobPositionEditText.getText().toString().trim();
                    driveDetails = jobDescriptionEditText.getText().toString().trim();

                    if (TextUtils.isEmpty(companyName)) {
                        companyNameEditText.setError(getResources().getString(R.string.error_enter_company_name));
                        return;
                    }
                    if (TextUtils.isEmpty(recruitmentPlace)) {
                        hiringPlaceEditText.setError(getResources().getString(R.string.error_enter_recruitment_location));
                        return;
                    }
                    if (TextUtils.isEmpty(jobPosition)) {
                        jobPositionEditText.setError(getResources().getString(R.string.error_enter_job_profile));
                        return;
                    }
                    if (TextUtils.isEmpty(driveDetails)) {
                        jobDescriptionEditText.setError(getResources().getString(R.string.erroe_enter_job_description));
                        return;
                    }

                    UpcomingDrives upcomingDrives = new UpcomingDrives(companyName, recruitmentDate,
                            recruitmentPlace, jobPosition, driveDetails);
                    mDrivesDatabaseReference.push().setValue(upcomingDrives);

                /* Clear input field */
                    companyNameEditText.setText("");
                    hiringDateEditText.setText("");
                    hiringPlaceEditText.setText("");
                    jobPositionEditText.setText("");
                    jobDescriptionEditText.setText("");
                }
            });
        } else {
            Toast.makeText(PublishNewOpeningActivity.this, getResources()
                    .getString(R.string.check_internet_connectivity), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {

        hiringDateEditText.setText(getResources().getString(R.string.formatted_date, day, month, year));
        hiringDateEditText.setEnabled(false);
        hiringDateEditText.setFocusable(true);
        hiringDateEditText.setClickable(true);

    }
}