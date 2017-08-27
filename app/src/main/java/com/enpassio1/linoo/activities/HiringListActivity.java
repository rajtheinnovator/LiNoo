package com.enpassio1.linoo.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.enpassio1.linoo.R;
import com.enpassio1.linoo.adapters.UpcomingHiresListAdapter;
import com.enpassio1.linoo.models.UpcomingDrives;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class HiringListActivity extends AppCompatActivity {

    RecyclerView drivesListRecyclerView;
    LinearLayoutManager drivesListLinearLayoutManager;
    UpcomingHiresListAdapter upcomingHiresListAdapter;
    ArrayList<UpcomingDrives> upcomingDrivesArrayList;
    private ChildEventListener mChildEventListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDrivesDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
                Log.v("my_tag", "name is: " + upcomingDrive.getCompanyName());
                Log.v("my_tag", "date is: " + upcomingDrive.getDriveDate());
                Log.v("my_tag", "place is: " + upcomingDrive.getPlace());
                Log.v("my_tag", "position is: " + upcomingDrive.getJobPosition());
                Log.v("my_tag", "description is: " + upcomingDrive.getDetailedDescription());
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
