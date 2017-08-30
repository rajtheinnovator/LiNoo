package com.enpassio1.linoo.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.enpassio1.linoo.R;
import com.enpassio1.linoo.models.UpcomingDrives;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PublishNewOpeningActivity extends AppCompatActivity {

    EditText companyNameEditText;
    EditText hiringDateEditText;
    EditText hiringPlaceEditText;
    EditText jobPositionEditText;
    EditText jobDescriptionEditText;
    Button publishButton;

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
        hiringPlaceEditText = (EditText) findViewById(R.id.recruitment_place_edit_text);
        jobPositionEditText = (EditText) findViewById(R.id.job_position_edit_text);
        jobDescriptionEditText = (EditText) findViewById(R.id.drive_details__edit_text);
        publishButton = (Button) findViewById(R.id.publish_button);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDrivesDatabaseReference = mFirebaseDatabase.getReference().child("drives");

        publishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String companyName = companyNameEditText.getText().toString().trim();
                String recruitmentDate = hiringDateEditText.getText().toString().trim();
                String recruitmentPlace = hiringPlaceEditText.getText().toString().trim();
                String jobPosition = jobPositionEditText.getText().toString().trim();
                String driveDetails = jobDescriptionEditText.getText().toString().trim();

                if (TextUtils.isEmpty(companyName)) {
                    companyNameEditText.setError("Enter the company name");
                    return;
                }
                if (TextUtils.isEmpty(recruitmentDate)) {
                    hiringDateEditText.setError("Enter date of recruitment");
                    return;
                }
                if (TextUtils.isEmpty(recruitmentPlace)) {
                    hiringPlaceEditText.setError("Where is this drive being conducted?");
                    return;
                }
                if (TextUtils.isEmpty(jobPosition)) {
                    jobPositionEditText.setError("Enter the job profile");
                    return;
                }
                if (TextUtils.isEmpty(driveDetails)) {
                    jobDescriptionEditText.setError("Give a detailed description about the drive");
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
    }
}