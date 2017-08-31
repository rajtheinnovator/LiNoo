package com.enpassio1.linoo.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.enpassio1.linoo.R;
import com.enpassio1.linoo.fragments.DatePickerFragment;
import com.enpassio1.linoo.models.UpcomingDrives;
import com.enpassio1.linoo.utils.InternetConnectivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class PublishNewOpeningActivity extends AppCompatActivity implements DatePickerFragment.OnDateSetListener {

    EditText companyNameEditText;
    EditText hiringDateEditText;
    EditText hiringPlaceEditText;
    EditText jobPositionEditText;
    EditText jobDescriptionEditText;

    String companyName;
    String recruitmentDate;
    String recruitmentPlace;
    String jobPosition;
    String driveDetails;
    String selectedCity;
    List<String> cityList;
    private Spinner citySpinner;
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


        jobPositionEditText = (EditText) findViewById(R.id.job_position_edit_text);
        jobDescriptionEditText = (EditText) findViewById(R.id.drive_details__edit_text);
        citySpinner = (Spinner) findViewById(R.id.recruitment_place_spinner);
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
        cityList = new ArrayList<String>();
        cityList.add(getResources().getString(R.string.city_bengaluru));
        cityList.add(getResources().getString(R.string.city_pune));
        cityList.add(getResources().getString(R.string.city_mumbai));
        cityList.add(getResources().getString(R.string.city_new_delhi));
        cityList.add(getResources().getString(R.string.city_hyderabad));
        setupSpinner();

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDrivesDatabaseReference = mFirebaseDatabase.getReference().child(getResources().getString(R.string.firebase_database_child_drives));

        /* bottom navigation view code referenced from:
         * http://www.truiton.com/2017/01/android-bottom-navigation-bar-example/
         */
        bottomNavigationView.setOnNavigationItemSelectedListener
                (new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                        switch (item.getItemId()) {
                            case R.id.action_discard:


                                //show an alert before exiting

                                /* code for alert dialog referenced from:
                                * https://www.mkyong.com/android/android-alert-dialog-example/
                                */
                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                                        PublishNewOpeningActivity.this);

                                // set title
                                alertDialogBuilder.setTitle("Discard Alert!");

                                // set dialog message
                                alertDialogBuilder
                                        .setMessage("Are you sure you want to exit?")
                                        .setCancelable(false)
                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                // if this button is clicked, close
                                                // current activity
                                                startActivity(new Intent(PublishNewOpeningActivity.this, HiringListActivity.class));
                                                finish();
                                            }
                                        })
                                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                // if this button is clicked, just close
                                                // the dialog box and do nothing
                                                dialog.cancel();
                                            }
                                        });

                                // create alert dialog
                                AlertDialog alertDialog = alertDialogBuilder.create();

                                // show it
                                alertDialog.show();
                                break;
                            case R.id.action_save_for_later:
                                //
                                break;
                            case R.id.action_publish:
                                publish();
                                finish();
                                break;
                        }
                        return true;
                    }
                });
    }

    private void publish() {
        if (InternetConnectivity.isInternetConnected(PublishNewOpeningActivity.this)) {
            companyName = companyNameEditText.getText().toString().trim();
            recruitmentDate = hiringDateEditText.getText().toString().trim();
            jobPosition = jobPositionEditText.getText().toString().trim();
            driveDetails = jobDescriptionEditText.getText().toString().trim();

            recruitmentPlace = selectedCity;

            if (TextUtils.isEmpty(companyName)) {
                companyNameEditText.setError(getResources().getString(R.string.error_enter_company_name));
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
            jobPositionEditText.setText("");
            jobDescriptionEditText.setText("");
        } else {
            Toast.makeText(PublishNewOpeningActivity.this, getResources()
                    .getString(R.string.check_internet_connectivity), Toast.LENGTH_SHORT).show();
        }
    }

    private void setupSpinner() {
        //codes below referenced from: https://www.mkyong.com/android/android-spinner-drop-down-list-example/

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, cityList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        citySpinner.setAdapter(dataAdapter);
        citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                selectedCity = adapterView.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

                selectedCity = cityList.get(0);
            }
        });
    }


    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {

        hiringDateEditText.setText(getResources().getString(R.string.formatted_date, day, month, year));
        hiringDateEditText.setEnabled(false);
        hiringDateEditText.setFocusable(true);
        hiringDateEditText.setClickable(true);

    }
}