package com.enpassio.linoo.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.enpassio.linoo.R;
import com.enpassio.linoo.adapters.UpcomingHiresListAdapter;
import com.enpassio.linoo.models.UpcomingDrives;

import java.util.ArrayList;

public class HiringListActivity extends AppCompatActivity {

    RecyclerView drivesListRecyclerView;
    LinearLayoutManager drivesListLinearLayoutManager;
    UpcomingHiresListAdapter upcomingHiresListAdapter;
    ArrayList<UpcomingDrives> upcomingDrivesArrayList;

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

        upcomingDrivesArrayList.add(new UpcomingDrives("21/01/2017", "details details"));
        upcomingDrivesArrayList.add(new UpcomingDrives("21/01/2017", "details details"));
        upcomingDrivesArrayList.add(new UpcomingDrives("21/01/2017", "details details"));


        upcomingHiresListAdapter = new UpcomingHiresListAdapter(this, upcomingDrivesArrayList);
        drivesListRecyclerView.setAdapter(upcomingHiresListAdapter);
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
