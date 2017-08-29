package com.enpassio1.linoo.sync;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import com.enpassio1.linoo.data.DriveContract;
import com.enpassio1.linoo.models.UpcomingDrives;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by ABHISHEK RAJ on 8/29/2017.
 */

public class FirebaseDataFetchJobService extends JobService {

    private AsyncTask mBackgroundTask;


    // COMPLETED (4) Override onStartJob

    /**
     * The entry point to your Job. Implementations should offload work to another thread of
     * execution as soon as possible.
     * <p>
     * This is called by the Job Dispatcher to tell us we should start our job. Keep in mind this
     * method is run on the application's main thread, so we need to offload work to a background
     * thread.
     *
     * @return whether there is more work remaining.
     */
    @Override
    public boolean onStartJob(final JobParameters jobParameters) {

        // COMPLETED (5) By default, jobs are executed on the main thread, so make an anonymous class extending
        //  AsyncTask called mBackgroundTask.
        // Here's where we make an AsyncTask so that this is no longer on the main thread
        mBackgroundTask = new AsyncTask() {

            // COMPLETED (6) Override doInBackground
            @Override
            protected Object doInBackground(Object[] params) {
                // COMPLETED (7) Use ReminderTasks to execute the new charging reminder task you made, use
                // this service as the context (WaterReminderFirebaseJobService.this) and return null
                // when finished.
                Context context = FirebaseDataFetchJobService.this;

                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("drives");

                /* code below referenced from: https://stackoverflow.com/a/38493214/5770629 */
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Is better to use a List, because you don't know the size
                        // of the iterator returned by dataSnapshot.getChildren() to
                        // initialize the array

                        for (DataSnapshot areaSnapshot : dataSnapshot.getChildren()) {
                            UpcomingDrives upcomingDrive = areaSnapshot.getValue(UpcomingDrives.class);

                            //code below referenced from: https://stackoverflow.com/a/6233664/5770629

                            Cursor cursor = getContentResolver().query(DriveContract.DriveEntry.CONTENT_URI, null, DriveContract.DriveEntry.COLUMN_DRIVE_KEY + "=" + areaSnapshot.getKey(), null, null);
                            if (cursor.getCount() == 0) {
                                //as this data doesn't exist, so write that in, into the SQLite database

                                ContentValues contentValues = new ContentValues();
                                contentValues.put(DriveContract.DriveEntry.COLUMN_COMPANY_NAME, upcomingDrive.getCompanyName());
                                contentValues.put(DriveContract.DriveEntry.COLUMN_DRIVE_DATE, upcomingDrive.getDriveDate());
                                contentValues.put(DriveContract.DriveEntry.COLUMN_DRIVE_LOCATION, upcomingDrive.getPlace());
                                contentValues.put(DriveContract.DriveEntry.COLUMN_JOB_POSITION, upcomingDrive.getJobPosition());
                                contentValues.put(DriveContract.DriveEntry.COLUMN_JOB_DESCRIPTION, upcomingDrive.getDetailedDescription());
                                Uri uri = getContentResolver().insert(DriveContract.DriveEntry.CONTENT_URI, contentValues);
                            }

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                // COMPLETED (8) Override onPostExecute and called jobFinished. Pass the job parameters
                // and false to jobFinished. This will inform the JobManager that your job is done
                // and that you do not want to reschedule the job.

                /*
                 * Once the AsyncTask is finished, the job is finished. To inform JobManager that
                 * you're done, you call jobFinished with the jobParamters that were passed to your
                 * job and a boolean representing whether the job needs to be rescheduled. This is
                 * usually if something didn't work and you want the job to try running again.
                 */

                jobFinished(jobParameters, false);
            }
        };

        // COMPLETED (9) Execute the AsyncTask
        mBackgroundTask.execute();
        // COMPLETED (10) Return true
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        // COMPLETED (12) If mBackgroundTask is valid, cancel it
        // COMPLETED (13) Return true to signify the job should be retried
        if (mBackgroundTask != null) mBackgroundTask.cancel(true);
        return true;
    }
}