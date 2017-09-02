package com.enpassio.linoo.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.enpassio.linoo.R;
import com.enpassio.linoo.fragments.HiringListFragment;
import com.google.firebase.auth.FirebaseAuth;

public class HiringListActivity extends AppCompatActivity {

    String userStatus;
    String mTwoPane;
    String usersUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hiring_list);
        Bundle bundleFromAuthenticatingActivity = getIntent().getBundleExtra("bundleFromAuthenticatingActivity");
        if (bundleFromAuthenticatingActivity != null) {
            if (bundleFromAuthenticatingActivity.getString("userStatus").equals("newUser")) {
                userStatus = "newUser";
            } else if (bundleFromAuthenticatingActivity.getString("userStatus").equals("oldUser")) {
                userStatus = "oldUser";
            }
        } else {
            /* if the activity is getting started after coming from some other activity than SignIn/Up, this must be a oldUSer */
            userStatus = "oldUser";

        }

        //code below referenced from: https://stackoverflow.com/a/43299619/5770629
        usersUid = FirebaseAuth.getInstance().getCurrentUser().getUid();


        HiringListFragment hiringListFragment = new HiringListFragment();

        if (findViewById(R.id.fragment_drive_details_container) != null) {
            mTwoPane = "true";
        } else mTwoPane = "false";

        Bundle bundle = new Bundle();
        bundle.putString("mTwoPane", mTwoPane);
        bundle.putString("userStatus", userStatus);
        hiringListFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_hiring_list, hiringListFragment)
                .commit();
    }
}