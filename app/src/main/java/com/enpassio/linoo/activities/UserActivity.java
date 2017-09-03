package com.enpassio.linoo.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.enpassio.linoo.R;
import com.enpassio.linoo.models.UserProfile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class UserActivity extends AppCompatActivity {
    private EditText nameEdieText;
    private EditText cityEditText;
    private Button save_button;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDrivesDatabaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        Bundle userStatusBundle = getIntent().getBundleExtra("userStatusBundle");
        String userStatus = userStatusBundle.getString("userStatus");
        save_button = (Button) findViewById(R.id.save_button);
        nameEdieText = (EditText) findViewById(R.id.user_name_edit_text);
        cityEditText = (EditText) findViewById(R.id.user_city_edit_text);
        if (userStatus.equals("newUser")) {
            nameEdieText.setVisibility(View.VISIBLE);
            cityEditText.setVisibility(View.VISIBLE);
        } else {
            nameEdieText.setVisibility(View.GONE);
            cityEditText.setVisibility(View.GONE);
            save_button.setVisibility(View.GONE);
            TextView nameTextView = (TextView) findViewById(R.id.user_name);
            TextView emailTextView = (TextView) findViewById(R.id.user_email);
            TextView cityTextView = (TextView) findViewById(R.id.user_city);
            SharedPreferences sharedPreferences = getSharedPreferences("MY_USERS_PROFILE_PREFERENCE", MODE_PRIVATE);
            String name = sharedPreferences.getString("name", "");
            String email = sharedPreferences.getString("email", "");
            String city = sharedPreferences.getString("city", "");
            nameTextView.setText(name);
            emailTextView.setText(email);
            cityTextView.setText(city);

        }

        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String name = nameEdieText.getText().toString().trim();
                final String city = cityEditText.getText().toString().trim();
                nameEdieText.setEnabled(false);
                cityEditText.setEnabled(false);

                mFirebaseDatabase = FirebaseDatabase.getInstance();
                mDrivesDatabaseReference = mFirebaseDatabase.getReference().child("userProfile");

                SharedPreferences sharedPreferences = getSharedPreferences("MY_USERS_PROFILE_PREFERENCE", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("name", name);
                editor.putString("city", city);
                editor.apply();

                //code below referenced from: https://stackoverflow.com/a/38022714/5770629
                FirebaseDatabase.getInstance().getReference().child("userProfile").child(FirebaseAuth
                        .getInstance().getCurrentUser().getUid())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                DataSnapshot childSnapshot = dataSnapshot.getChildren().iterator().next();
                                UserProfile userProfile = childSnapshot.getValue(UserProfile.class);
                                SharedPreferences sharedPreferences = getSharedPreferences("MY_USERS_PROFILE_PREFERENCE", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("email", userProfile.getUsersEmail());
                                editor.putString("name", name);
                                editor.putString("city", city);
                                editor.apply();
                                String uId = sharedPreferences.getString("uId", "");
                                Map<String, Object> hopperUpdates = new HashMap<String, Object>();
                                hopperUpdates.put("usersCity", city);
                                hopperUpdates.put("usersName", name);
                                mDrivesDatabaseReference.child(uId).child(childSnapshot.getKey()).updateChildren(hopperUpdates);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                Toast.makeText(UserActivity.this, "Thanks for updating your details!", Toast.LENGTH_SHORT).show();

            }
        });
    }
}
