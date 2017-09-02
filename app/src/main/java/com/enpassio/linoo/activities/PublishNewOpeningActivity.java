package com.enpassio.linoo.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.enpassio.linoo.R;
import com.enpassio.linoo.fragments.DatePickerFragment;
import com.enpassio.linoo.models.UpcomingDrives;
import com.enpassio.linoo.utils.InternetConnectivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
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
    Button showSavedDataButton;
    int spinnerPosition;
    private Spinner citySpinner;
    /*
    * firebase code referenced from: https://github.com/udacity/and-nd-firebase
    */
        /*
    * Firebase instance variables
    **/
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDrivesDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_new_opening);

        companyNameEditText = (EditText) findViewById(R.id.company_name_edit_text);
        hiringDateEditText = (EditText) findViewById(R.id.recruitment_date_edit_text);
        showSavedDataButton = (Button) findViewById(R.id.retrieve_file_from_saved_database);
        showSavedDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //sharedpreference code referenced from: https://stackoverflow.com/a/23024962/5770629
                SharedPreferences sharedPreferences = getSharedPreferences("MY_PREFS_NAME", MODE_PRIVATE);


                String description = readTextFromFile();

                String companyName = sharedPreferences.getString("companyName", "");
                String recruitmentDate = sharedPreferences.getString("recruitmentDate", "");
                ;
                int location = sharedPreferences.getInt("location", 0);
                String jobProfile = sharedPreferences.getString("jobProfile", "");
                ;
                String jobDescription = readTextFromFile();
                companyNameEditText.setText(companyName);
                hiringDateEditText.setText(recruitmentDate);
                jobPositionEditText.setText(jobProfile);
                citySpinner.setSelection(location);
                jobDescriptionEditText.setText(jobDescription);
            }
        });

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
        mDrivesDatabaseReference = mFirebaseDatabase.getReference().child(getResources()
                .getString(R.string.firebase_database_child_drives));

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
                                alertDialogBuilder.setTitle(getResources().getString(R.string.dialog_alert));

                                // set dialog message
                                alertDialogBuilder
                                        .setMessage(getResources().getString(R.string.sure_to_exit_message))
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
                                String companyName = companyNameEditText.getText().toString().trim();
                                String recruitmentDate = hiringDateEditText.getText().toString().trim();
                                int location = spinnerPosition;
                                String jobProfile = jobPositionEditText.getText().toString().trim();
                                String jobDescription = jobDescriptionEditText.getText().toString().trim();
                                writeToFile(jobDescription);

                                //sharedpreference code referenced from: https://stackoverflow.com/a/23024962/5770629
                                SharedPreferences sharedPreferences = getSharedPreferences("MY_PREFS_NAME", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("companyName", companyName);
                                editor.putString("recruitmentDate", recruitmentDate);
                                editor.putInt("location", location);
                                editor.putString("jobProfile", jobProfile);
                                editor.apply();
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

    /* code below referenced from:  https://stackoverflow.com/a/35481977/5770629 */
    public void writeToFile(String data) {
        // Get the directory for the user's public pictures directory.
        final File path =
                Environment.getExternalStoragePublicDirectory(
                        //Environment.DIRECTORY_PICTURES
                        Environment.DIRECTORY_DCIM + "myFolder"
                );

        // Make sure the path directory exists.
        if (!path.exists()) {
            // Make it, if it doesn't exit
            path.mkdirs();
        }

        final File file = new File(path, "file.txt");

        // Save your stream, don't forget to flush() it before closing it.

        try {
            file.createNewFile();
            FileOutputStream fOut = new FileOutputStream(file);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            myOutWriter.append(data);

            myOutWriter.close();

            fOut.flush();
            fOut.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    /* code below referenced from: https://stackoverflow.com/a/12421888/5770629 */
    private String readTextFromFile() {
        /*
        * Find the directory for the SD Card using the API
        * Don't* hardcode "/sdcard"
        */
        File sdcard = Environment.getExternalStoragePublicDirectory(
                //Environment.DIRECTORY_PICTURES
                Environment.DIRECTORY_DCIM + "myFolder"
        );

        //Get the text file
        File file = new File(sdcard, "file.txt");

        //Read text from file
        StringBuilder text = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        } catch (IOException e) {
            //You'll need to add proper error handling here
        }
        return text.toString();
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
                spinnerPosition = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                selectedCity = cityList.get(0);
                spinnerPosition = 0;
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