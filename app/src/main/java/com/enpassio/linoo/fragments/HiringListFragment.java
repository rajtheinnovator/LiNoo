package com.enpassio.linoo.fragments;

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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.enpassio.linoo.R;
import com.enpassio.linoo.activities.HiringListActivity;
import com.enpassio.linoo.activities.PublishNewOpeningActivity;
import com.enpassio.linoo.activities.SignInActivity;
import com.enpassio.linoo.activities.UserActivity;
import com.enpassio.linoo.adapters.UpcomingHiresListAdapter;
import com.enpassio.linoo.data.DriveContract;
import com.enpassio.linoo.data.DriveContract.DriveEntry;
import com.enpassio.linoo.models.UpcomingDrives;
import com.enpassio.linoo.models.UserProfile;
import com.enpassio.linoo.utils.InternetConnectivity;
import com.enpassio.linoo.utils.NotificationUtilities;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.Collections;

import static android.content.Context.MODE_PRIVATE;

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
    SharedPreferences.Editor editor;
    String areaSnapshotKey;
    int iterator;
    ArrayList<UpcomingDrives> arrayList;
    private ChildEventListener mChildEventListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDrivesDatabaseReference;
    private FirebaseAuth auth;
    private ArrayList<UpcomingDrives> mUpcomingDrivesArrayList;
    //logic for two pane layout
    private boolean mTwoPane;
    private UserProfile mUserProfile;
    private Context context;

    public HiringListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getActivity();
        View rootView = inflater.inflate(R.layout.fragment_hiring_list, container, false);
        /* set that it has a menu */
        setHasOptionsMenu(true);
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
        FirebaseMessaging.getInstance().subscribeToTopic("drives1");

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
                SharedPreferences sharedPreferences = context.getSharedPreferences("MY_USERS_PROFILE_PREFERENCE", MODE_PRIVATE);
                String name = sharedPreferences.getString("name", "");
                String city = sharedPreferences.getString("city", "");
                String email = sharedPreferences.getString("email", "");
                mUserProfile = new UserProfile(name, city, email);
                FirebaseDatabase.getInstance().getReference().child("userProfile").child(FirebaseAuth
                        .getInstance().getCurrentUser().getUid()).push().setValue(mUserProfile);

            } else {
                //code below referenced from: https://stackoverflow.com/a/38022714/5770629
                FirebaseDatabase.getInstance().getReference().child("userProfile").child(FirebaseAuth
                        .getInstance().getCurrentUser().getUid())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                DataSnapshot childSnapshot = dataSnapshot.getChildren().iterator().next();
                                UserProfile userProfile = childSnapshot.getValue(UserProfile.class);
                                SharedPreferences sharedPreferences = context.getSharedPreferences("MY_USERS_PROFILE_PREFERENCE", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("email", userProfile.getUsersEmail());
                                editor.putString("city", userProfile.getUsersCity());
                                editor.putString("name", userProfile.getUsersName());
                                editor.apply();

                        /* do what you want with the userProfile like showing a user profile
                         * through navigation drawer or others
                         */
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

            }

            //that means, the app is getting started for the first time
            //so, load directly from the firebase database

            /* code below referenced from: https://stackoverflow.com/a/38493214/5770629 */

            mDrivesDatabaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // Is better to use a List, because you don't know the size
                    // of the iterator returned by dataSnapshot.getChildren() to
                    // initialize the array

                    arrayList = new ArrayList<UpcomingDrives>();

                    for (DataSnapshot areaSnapshot : dataSnapshot.getChildren()) {
                        //if we're fetching data again, then delete the old data to avoid data redundancy
                        context.getContentResolver().delete(DriveEntry.CONTENT_URI, null, null);
                        Cursor cursor = context.getContentResolver().query(DriveEntry.CONTENT_URI, null, null, null, null);
                        if (cursor.getCount() < (int) dataSnapshot.getChildrenCount()) {
                            UpcomingDrives upcomingDrive = areaSnapshot.getValue(UpcomingDrives.class);
                            iterator += 1;
                            arrayList.add(new UpcomingDrives(upcomingDrive.getCompanyName(), upcomingDrive.getDriveDate(), upcomingDrive.getPlace(), upcomingDrive.getJobPosition(), upcomingDrive.getDetailedDescription()));
                            areaSnapshotKey = areaSnapshot.getKey();
                            upcomingDrivesArrayList.add(upcomingDrive);

                        }

                        cursor.close();
                    }
                    Collections.reverse(arrayList);

                    int i = 0;
                    while (i < iterator) {
                        if (i == arrayList.size()) {
                            return;
                        }
                        UpcomingDrives upcomingDrive = arrayList.get(i);
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(DriveEntry.COLUMN_COMPANY_NAME, upcomingDrive.getCompanyName());
                        contentValues.put(DriveEntry.COLUMN_DRIVE_DATE, upcomingDrive.getDriveDate());
                        contentValues.put(DriveEntry.COLUMN_DRIVE_LOCATION, upcomingDrive.getPlace());
                        contentValues.put(DriveEntry.COLUMN_JOB_POSITION, upcomingDrive.getJobPosition());
                        contentValues.put(DriveEntry.COLUMN_JOB_DESCRIPTION, upcomingDrive.getDetailedDescription());
                        contentValues.put(DriveEntry.COLUMN_DRIVE_KEY, areaSnapshotKey);

                        createNotificationForNewUpcomingDrive(upcomingDrive);
                        Uri uri = context.getContentResolver().insert(DriveContract.DriveEntry.CONTENT_URI, contentValues);
                        i += 1;
                    }
                    iterator = 0;

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
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
        if (getActivity() != null) {
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
            SharedPreferences sharedPreferences = context.getSharedPreferences("MY_USERS_PROFILE_PREFERENCE", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove("email");
            editor.remove("city");
            editor.remove("name");
            editor.apply();
            upcomingHiresListAdapter.setDriveData(null);
            startActivity(new Intent(getContext(), SignInActivity.class));
            getActivity().finish();
        }
        if (id == R.id.action_user_profile) {
            Intent userProfileIntent = new Intent(getActivity(), UserActivity.class);
            Bundle userStatusBundle = new Bundle();
            userStatusBundle.putString("userStatus", userStatus);
            userProfileIntent.putExtra("userStatusBundle", userStatusBundle);
            startActivity(userProfileIntent);
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
                do {
                    int nameColumnIndex = cursor.getColumnIndex(DriveContract.DriveEntry.COLUMN_COMPANY_NAME);
                    int dateColumnIndex = cursor.getColumnIndex(DriveEntry.COLUMN_DRIVE_DATE);
                    int locationColumnIndex = cursor.getColumnIndex(DriveEntry.COLUMN_DRIVE_LOCATION);
                    int jobPositionColumnIndex = cursor.getColumnIndex(DriveEntry.COLUMN_JOB_POSITION);
                    int jobDescriptionColumnIndex = cursor.getColumnIndex(DriveEntry.COLUMN_JOB_DESCRIPTION);

                    String name = cursor.getString(nameColumnIndex);
                    String date = cursor.getString(dateColumnIndex);
                    String location = cursor.getString(locationColumnIndex);
                    String jobPosition = cursor.getString(jobPositionColumnIndex);
                    String description = cursor.getString(jobDescriptionColumnIndex);
                    UpcomingDrives upcomingDrives = new UpcomingDrives(name, date, location, jobPosition, description);
                    mUpcomingDrivesArrayList.add(upcomingDrives);
                } while (cursor.moveToNext());
            }
        }
        notificationManager.cancelAll();
        Collections.reverse(mUpcomingDrivesArrayList);
        upcomingHiresListAdapter.setDriveData(mUpcomingDrivesArrayList);
        notificationManager.cancelAll();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        upcomingHiresListAdapter.setDriveData(new ArrayList<UpcomingDrives>());
    }
}