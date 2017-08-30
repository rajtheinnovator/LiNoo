package com.enpassio1.linoo.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.enpassio1.linoo.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static com.enpassio1.linoo.R.id.reset_password;
import static com.enpassio1.linoo.R.id.sign_up_button;

//code below referenced from: https://firebase.google.com/docs/auth/android/manage-users?

public class ResetPasswordActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private EditText emailEditText;
    private EditText passwordEditText;
    private Button signUpButton;
    private Button resetPasswordButton;
    private ProgressBar progressBar;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        mAuth = FirebaseAuth.getInstance();
        emailEditText = (EditText) findViewById(R.id.email_id);
        signUpButton = (Button) findViewById(sign_up_button);
        resetPasswordButton = (Button) findViewById(reset_password);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        signUpButton.setOnClickListener(ResetPasswordActivity.this);
        resetPasswordButton.setOnClickListener(ResetPasswordActivity.this);

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            //if user is signed in, then go to the HiringListActivity
            startActivity(new Intent(ResetPasswordActivity.this, HiringListActivity.class));
            finish();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.reset_password:
                String email = emailEditText.getText().toString().trim();
                if (TextUtils.isEmpty(email)) {
                    emailEditText.setError("Enter a valid email");
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                mAuth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    emailEditText.setText("");
                                    Toast.makeText(ResetPasswordActivity.this, "Password sent to" +
                                            " your email. Check that now", Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                                    startActivity(new Intent(ResetPasswordActivity.this, SignInActivity.class));
                                    finish();
                                } else {
                                    emailEditText.setError("Email not registered");
                                }
                            }
                        });
                break;
            case R.id.sign_up_button:
                startActivity(new Intent(ResetPasswordActivity.this, SignupActivity.class));
                finish();
                break;
        }
    }
}