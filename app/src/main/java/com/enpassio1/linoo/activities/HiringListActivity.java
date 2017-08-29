package com.enpassio1.linoo.activities;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.enpassio1.linoo.R;
import com.enpassio1.linoo.adapters.UpcomingHiresListAdapter;
import com.enpassio1.linoo.models.UpcomingDrives;
import com.enpassio1.linoo.utils.NotificationUtilities;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;

public class HiringListActivity extends AppCompatActivity {

    RecyclerView drivesListRecyclerView;
    LinearLayoutManager drivesListLinearLayoutManager;
    UpcomingHiresListAdapter upcomingHiresListAdapter;
    ArrayList<UpcomingDrives> upcomingDrivesArrayList;
    private ChildEventListener mChildEventListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDrivesDatabaseReference;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();


        //code below referenced from: https://firebase.google.com/docs/cloud-messaging/android/send-multiple
        FirebaseMessaging.getInstance().subscribeToTopic("drives");

        if (savedInstanceState == null) {
            upcomingDrivesArrayList = new ArrayList<UpcomingDrives>();
        }
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent publishNewOpeningIntent = new Intent(HiringListActivity.this, PublishNewOpeningActivity.class);
                startActivity(publishNewOpeningIntent);
            }
        });

        NotificationUtilities.scheduleNewDriveAddedReminder(HiringListActivity.this);
        /* code below referenced from my previous project at the following link:
        * https://github.com/rajtheinnovator/Project-2-ShowMyShow/
        */
        drivesListRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_upcoming_drive_list);
        drivesListLinearLayoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);

        drivesListRecyclerView.setLayoutManager(drivesListLinearLayoutManager);

        //adding data for testing purposes

        upcomingHiresListAdapter = new UpcomingHiresListAdapter(this, upcomingDrivesArrayList);
        drivesListRecyclerView.setAdapter(upcomingHiresListAdapter);
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDrivesDatabaseReference = mFirebaseDatabase.getReference().child("drives");
        mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                UpcomingDrives upcomingDrive = dataSnapshot.getValue(UpcomingDrives.class);
                upcomingDrivesArrayList.add(upcomingDrive);
                upcomingHiresListAdapter.setDriveData(upcomingDrivesArrayList);

                createNotificationForNewUpcomingDrive(upcomingDrive);
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
        mDrivesDatabaseReference.addChildEventListener(mChildEventListener);
    }

    private void createNotificationForNewUpcomingDrive(UpcomingDrives upcomingDrives) {
        /*
        code below referenced from: https://www.survivingwithandroid.com/2016/09/android-firebase-push-notification.html
        */
        //  Create Notification
        Intent intent = new Intent(this, HiringListActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1410,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notificationBuilder = new
                NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Message")
                .setContentText(upcomingDrives.getDetailedDescription())
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);


        NotificationManager notificationManager =
                (NotificationManager)
                        getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(1410, notificationBuilder.build());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            auth.signOut();
            upcomingHiresListAdapter.setDriveData(null);
            startActivity(new Intent(HiringListActivity.this, SignInActivity.class));
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}