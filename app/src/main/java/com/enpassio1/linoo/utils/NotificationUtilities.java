package com.enpassio1.linoo.utils;

import android.content.Context;
import android.support.annotation.NonNull;

import com.enpassio1.linoo.sync.FirebaseDataFetchJobService;
import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;

import java.util.concurrent.TimeUnit;

/**
 * Created by ABHISHEK RAJ on 8/29/2017.
 */

public class NotificationUtilities {
/* code below referenced from:
* https://github.com/udacity/ud851-Exercises/blob/T10.04-Solution-PeriodicSyncWithJobDispatcher/app/src/main/java/com/example/android/background/sync/ReminderUtilities.java
* */

    private static final int REMINDER_INTERVAL_MINUTES = 30;
    private static final int REMINDER_INTERVAL_SECONDS = (int) (TimeUnit.MINUTES.toSeconds(REMINDER_INTERVAL_MINUTES));
    private static final int SYNC_FLEXTIME_SECONDS = REMINDER_INTERVAL_SECONDS;

    private static final String REMINDER_JOB_TAG = "reminder_tag";

    private static boolean sInitialized;

    // COMPLETED (16) Create a synchronized, public static method called scheduleNewDriveAddedReminder that takes
    // in a context. This method will use FirebaseJobDispatcher to schedule a job that repeats roughly
    // every REMINDER_INTERVAL_SECONDS when the phone is charging. It will trigger WaterReminderFirebaseJobService
    // Checkout https://github.com/firebase/firebase-jobdispatcher-android for an example
    synchronized public static void scheduleNewDriveAddedReminder(@NonNull final Context context) {


        // COMPLETED (17) If the job has already been initialized, return
        if (sInitialized) return;

        // COMPLETED (18) Create a new GooglePlayDriver
        Driver driver = new GooglePlayDriver(context);
        // COMPLETED (19) Create a new FirebaseJobDispatcher with the driver
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);


        /* Create the Job to periodically create reminders to drink water */
        Job constraintReminderJob = dispatcher.newJobBuilder()
                /* The Service that will be used to write to preferences */
                .setService(FirebaseDataFetchJobService.class)

                .setTag(REMINDER_JOB_TAG)

                .setConstraints(Constraint.DEVICE_CHARGING)

                .setLifetime(Lifetime.FOREVER)

                .setRecurring(true)

                .setTrigger(Trigger.executionWindow(
                        REMINDER_INTERVAL_SECONDS,
                        REMINDER_INTERVAL_SECONDS + SYNC_FLEXTIME_SECONDS))

                .setReplaceCurrent(true)
                /* Once the Job is ready, call the builder's build method to return the Job */
                .build();

        dispatcher.schedule(constraintReminderJob);

        // COMPLETED (22) Set sInitialized to true to mark that we're done setting up the job
        /* The job has been initialized */
        sInitialized = true;
    }

}