package com.enpassio.linoo.sync;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;

import com.enpassio.linoo.R;
import com.enpassio.linoo.data.DriveContract;
import com.enpassio.linoo.models.UpcomingDrives;
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


    // Override onStartJob

    /**
     * The entry point to my Job. Implementations should offload work to another thread of
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

        // Here's where we make an AsyncTask so that this is no longer on the main thread
        mBackgroundTask = new AsyncTask() {

            //Override doInBackground
            @Override
            protected Object doInBackground(Object[] params) {
                Context context = FirebaseDataFetchJobService.this;

                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                        .child(context.getResources().getString(R.string.firebase_database_child_drives));

                /* code below referenced from: https://stackoverflow.com/a/38493214/5770629 */
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        /*
                        Is better to use a List, because one doesn't know the size
                        of the iterator returned by dataSnapshot.getChildren() to
                        initialize the array
                        */
                        int rowsDeleted = getContentResolver().delete(DriveContract.DriveEntry.CONTENT_URI, null, null);
                        for (DataSnapshot areaSnapshot : dataSnapshot.getChildren()) {
                            UpcomingDrives upcomingDrive = areaSnapshot.getValue(UpcomingDrives.class);

                            //code below referenced from: https://stackoverflow.com/a/6233664/5770629

                            //as this data doesn't exist, so write that in, into the SQLite database

                            ContentValues contentValues = new ContentValues();
                            contentValues.put(DriveContract.DriveEntry.COLUMN_COMPANY_NAME, upcomingDrive.getCompanyName());
                            contentValues.put(DriveContract.DriveEntry.COLUMN_DRIVE_DATE, upcomingDrive.getDriveDate());
                            contentValues.put(DriveContract.DriveEntry.COLUMN_DRIVE_LOCATION, upcomingDrive.getPlace());
                            contentValues.put(DriveContract.DriveEntry.COLUMN_JOB_POSITION, upcomingDrive.getJobPosition());
                            contentValues.put(DriveContract.DriveEntry.COLUMN_JOB_DESCRIPTION, upcomingDrive.getDetailedDescription());
                            contentValues.put(DriveContract.DriveEntry.COLUMN_DRIVE_KEY, areaSnapshot.getKey());
                            Uri uri = getContentResolver().insert(DriveContract.DriveEntry.CONTENT_URI, contentValues);

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
                // Override onPostExecute and called jobFinished. Pass the job parameters
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

        mBackgroundTask.execute();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        // If mBackgroundTask is valid, cancel it
        // Return true to signify the job should be retried
        if (mBackgroundTask != null) mBackgroundTask.cancel(true);
        return true;
    }
}