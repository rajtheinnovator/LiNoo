package com.enpassio1.linoo.fragments;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.enpassio1.linoo.R;
import com.enpassio1.linoo.activities.HiringListActivity;
import com.enpassio1.linoo.activities.PublishNewOpeningActivity;
import com.enpassio1.linoo.activities.SignInActivity;
import com.enpassio1.linoo.adapters.UpcomingHiresListAdapter;
import com.enpassio1.linoo.data.DriveContract;
import com.enpassio1.linoo.data.DriveContract.DriveEntry;
import com.enpassio1.linoo.models.UpcomingDrives;
import com.enpassio1.linoo.models.UserProfile;
import com.enpassio1.linoo.utils.InternetConnectivity;
import com.enpassio1.linoo.utils.NotificationUtilities;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;

/**
 * Created by ABHISHEK RAJ on 8/31/2017.
 */

public class HiringListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    // Which loader is running can be known using this static variable
    private static final int LOADER_ID = 0;
    RecyclerView drivesListRecyclerView;
    LinearLayoutManager drivesListLinearLayoutManager;
    UpcomingHiresListAdapter upcomingHiresListAdapter;
    ArrayList<UpcomingDrives> upcomingDrivesArrayList;
    NotificationManager notificationManager;
    String userStatus;
    private ChildEventListener mChildEventListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDrivesDatabaseReference;
    private FirebaseAuth auth;
    private ArrayList<UpcomingDrives> mUpcomingDrivesArrayList;
    //logic for two pane layout
    private boolean mTwoPane;
    private UserProfile mUserProfile;

    public HiringListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_hiring_list, container, false);
        Bundle bundle = getArguments();
        String mTwoPaneString = bundle.getString("mTwoPane");

        userStatus = bundle.getString("userStatus");

        if (mTwoPaneString.equals("true")) {
            mTwoPane = true;
        } else mTwoPane = false;

        auth = FirebaseAuth.getInstance();
        notificationManager =
                (NotificationManager)
                        getContext().getSystemService(Context.NOTIFICATION_SERVICE);

        //code below referenced from: https://firebase.google.com/docs/cloud-messaging/android/send-multiple
        FirebaseMessaging.getInstance().subscribeToTopic(getResources()
                .getString(R.string.subscribe_to_topic_drives));

        if (savedInstanceState == null) {
            upcomingDrivesArrayList = new ArrayList<UpcomingDrives>();
        }
        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent publishNewOpeningIntent = new Intent(getContext(),
                        PublishNewOpeningActivity.class);
                startActivity(publishNewOpeningIntent);
            }
        });

        NotificationUtilities.scheduleNewDriveAddedReminder(getContext());
        /* code below referenced from my previous project at the following link:
        * https://github.com/rajtheinnovator/Project-2-ShowMyShow/
        */
        drivesListRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view_upcoming_drive_list);
        drivesListLinearLayoutManager = new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false);

        drivesListRecyclerView.setLayoutManager(drivesListLinearLayoutManager);

        //adding data for testing purposes

        upcomingHiresListAdapter = new UpcomingHiresListAdapter(getContext(), upcomingDrivesArrayList, mTwoPane);
        drivesListRecyclerView.setAdapter(upcomingHiresListAdapter);
        if (InternetConnectivity.isInternetConnected(getContext())) {
            mFirebaseDatabase = FirebaseDatabase.getInstance();
            mDrivesDatabaseReference = mFirebaseDatabase.getReference().child(getResources()
                    .getString(R.string.firebase_database_child_drives));

            if (userStatus.equals("newUser")) {
                mUserProfile = new UserProfile("nameeee", "cityyyy", "emailllll");
                FirebaseDatabase.getInstance().getReference().child("userProfile").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).push().setValue(mUserProfile);

            } else {

                //code below referenced from: https://stackoverflow.com/a/38022714/5770629
                FirebaseDatabase.getInstance().getReference().child("userProfile").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
                        //do what you want with the userProfile
                        Log.v("my_tag", "email is: " + userProfile.getUsersEmail());
                        Log.v("my_tag", "" + FirebaseAuth.getInstance().getCurrentUser().getUid());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }

     /* code below referenced from: https://developer.android.com/training/basics/data-storage/shared-preferences.html */

            SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
            if (!sharedPreferences.contains(getResources().getString(R.string.shared_preference_key))) {

                //that means, the app is getting started for the first time
                //so, load directly from the firebase database

            /* code below referenced from: https://stackoverflow.com/a/38493214/5770629 */

                mDrivesDatabaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Is better to use a List, because you don't know the size
                        // of the iterator returned by dataSnapshot.getChildren() to
                        // initialize the array
                        for (DataSnapshot areaSnapshot : dataSnapshot.getChildren()) {
                            UpcomingDrives upcomingDrive = areaSnapshot.getValue(UpcomingDrives.class);

                            ContentValues contentValues = new ContentValues();
                            contentValues.put(DriveEntry.COLUMN_COMPANY_NAME, upcomingDrive.getCompanyName());
                            contentValues.put(DriveEntry.COLUMN_DRIVE_DATE, upcomingDrive.getDriveDate());
                            contentValues.put(DriveEntry.COLUMN_DRIVE_LOCATION, upcomingDrive.getPlace());
                            contentValues.put(DriveEntry.COLUMN_JOB_POSITION, upcomingDrive.getJobPosition());
                            contentValues.put(DriveEntry.COLUMN_JOB_DESCRIPTION, upcomingDrive.getDetailedDescription());
                            contentValues.put(DriveEntry.COLUMN_DRIVE_KEY, areaSnapshot.getKey());
                            Uri uri = getContext().getContentResolver().insert(DriveContract.DriveEntry.CONTENT_URI, contentValues);

                            upcomingDrivesArrayList.add(upcomingDrive);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            /* code below referenced from: https://developer.android.com/training/basics/data-storage/shared-preferences.html */
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(getResources().getString(R.string.shared_preference_key),
                        getResources().getString(R.string.shared_preference_value));
                editor.apply();
            } else {
                mChildEventListener = new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        UpcomingDrives upcomingDrive = dataSnapshot.getValue(UpcomingDrives.class);
                        upcomingDrivesArrayList.add(upcomingDrive);
                        upcomingHiresListAdapter.setDriveData(upcomingDrivesArrayList);
                        createNotificationForNewUpcomingDrive(upcomingDrive);

                        ContentValues contentValues = new ContentValues();
                        contentValues.put(DriveEntry.COLUMN_COMPANY_NAME, upcomingDrive.getCompanyName());
                        contentValues.put(DriveEntry.COLUMN_DRIVE_DATE, upcomingDrive.getDriveDate());
                        contentValues.put(DriveEntry.COLUMN_DRIVE_LOCATION, upcomingDrive.getPlace());
                        contentValues.put(DriveEntry.COLUMN_JOB_POSITION, upcomingDrive.getJobPosition());
                        contentValues.put(DriveEntry.COLUMN_JOB_DESCRIPTION, upcomingDrive.getDetailedDescription());
                        contentValues.put(DriveEntry.COLUMN_DRIVE_KEY, dataSnapshot.getKey());
                        Uri uri = getContext().getContentResolver().insert(DriveContract.DriveEntry.CONTENT_URI, contentValues);
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                };

                NotificationUtilities.scheduleNewDriveAddedReminder(getContext());
                mDrivesDatabaseReference.addChildEventListener(mChildEventListener);
            }
        } else {
            Toast.makeText(getContext(), getResources()
                    .getString(R.string.check_internet_connectivity), Toast.LENGTH_SHORT).show();
        }
        //finally, start the loader and load data from it
        getActivity().getSupportLoaderManager().initLoader(LOADER_ID, null, this);


        return rootView;
    }

    private void createNotificationForNewUpcomingDrive(UpcomingDrives upcomingDrives) {
        /*
        code below referenced from: https://www.survivingwithandroid.com/2016/09/android-firebase-push-notification.html
        */
        //  Create Notification
        Intent intent = new Intent(getActivity(), HiringListActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), 1410,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notificationBuilder = new
                NotificationCompat.Builder(getContext())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(getResources().getString(R.string.notification_title))
                .setContentText(upcomingDrives.getDetailedDescription())
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);


        notificationManager.notify(1410, notificationBuilder.build());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_sign_out) {
            auth.signOut();
            upcomingHiresListAdapter.setDriveData(null);
            startActivity(new Intent(getContext(), SignInActivity.class));
            getActivity().finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                DriveEntry._ID,
                DriveEntry.COLUMN_COMPANY_NAME,
                DriveEntry.COLUMN_DRIVE_DATE,
                DriveEntry.COLUMN_DRIVE_LOCATION,
                DriveEntry.COLUMN_JOB_POSITION,
                DriveEntry.COLUMN_JOB_DESCRIPTION
        };

        // this method will execute the Content Providers query method on a background thread
        return new CursorLoader(
                getContext(),   // Parent activity context
                DriveEntry.CONTENT_URI,        // Table to query
                projection,     // Projection to return
                null,            // No selection clause
                null,            // No selection arguments
                null             // Default sort order
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        ArrayList<UpcomingDrives> mUpcomingDrivesArrayList = new ArrayList<>();
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                while (cursor.moveToNext()) {

                    int nameColumnIndex = cursor.getColumnIndex(DriveContract.DriveEntry.COLUMN_COMPANY_NAME);
                    int dateColumnIndex = cursor.getColumnIndex(DriveContract.DriveEntry.COLUMN_COMPANY_NAME);
                    int locationColumnIndex = cursor.getColumnIndex(DriveContract.DriveEntry.COLUMN_COMPANY_NAME);
                    int jobPositionColumnIndex = cursor.getColumnIndex(DriveContract.DriveEntry.COLUMN_COMPANY_NAME);
                    int jobDescriptionColumnIndex = cursor.getColumnIndex(DriveContract.DriveEntry.COLUMN_COMPANY_NAME);

                    String name = cursor.getString(nameColumnIndex);
                    String date = cursor.getString(dateColumnIndex);
                    String location = cursor.getString(locationColumnIndex);
                    String jobPosition = cursor.getString(jobPositionColumnIndex);
                    String description = cursor.getString(jobDescriptionColumnIndex);
                    UpcomingDrives upcomingDrives = new UpcomingDrives(name, date, location, jobPosition, description);
                    mUpcomingDrivesArrayList.add(upcomingDrives);
                }
            }
        }
        notificationManager.cancelAll();
        upcomingHiresListAdapter.setDriveData(mUpcomingDrivesArrayList);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
