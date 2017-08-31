package com.enpassio1.linoo.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.enpassio1.linoo.R;
import com.enpassio1.linoo.fragments.HiringListFragment;

public class HiringListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hiring_list);
        HiringListFragment hiringListFragment = new HiringListFragment();
        
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_hiring_list, hiringListFragment)
                .commit();
    }
}