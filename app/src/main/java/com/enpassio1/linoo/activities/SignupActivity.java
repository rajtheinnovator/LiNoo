package com.enpassio1.linoo.activities;

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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static com.enpassio1.linoo.R.id.sign_in_button;

/* code below referenced from: https://firebase.google.com/docs/auth/android/start/ */

public class SignupActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private EditText emailEditText;
    private EditText passwordEditText;
    private Button signUpButton;
    private Button forgotPasswordButton;
    private Button logInButton;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        mAuth = FirebaseAuth.getInstance();
        emailEditText = (EditText) findViewById(R.id.email_id);
        passwordEditText = (EditText) findViewById(R.id.password);
        signUpButton = (Button) findViewById(R.id.sign_up_button);
        forgotPasswordButton = (Button) findViewById(R.id.reset_password);
        logInButton = (Button) findViewById(sign_in_button);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        signUpButton.setOnClickListener(SignupActivity.this);
        forgotPasswordButton.setOnClickListener(SignupActivity.this);
        logInButton.setOnClickListener(SignupActivity.this);

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            //if user is signed in, then go to the HiringListActivity
            startActivity(new Intent(SignupActivity.this, HiringListActivity.class));
            finish();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sign_up_button:
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                if (TextUtils.isEmpty(email)) {
                    emailEditText.setError(getResources().getString(R.string.error_enter_valid_email));
                    return;
                }
                if (password.length() < 6) {
                    passwordEditText.setError(getResources().getString(R.string
                            .error_password_less_than_six_character_long));
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    progressBar.setVisibility(View.GONE);
                                    startActivity(new Intent(SignupActivity.this, HiringListActivity.class));
                                    finish();
                                } else {
                                    // If sign in fails, display a message to the user.

                                    Toast.makeText(SignupActivity.this, getResources()
                                                    .getString(R.string.toast_problem_creating_account),
                                            Toast.LENGTH_SHORT).show();

                                }
                            }
                        });
                break;
            case R.id.reset_password:
                startActivity(new Intent(SignupActivity.this, ResetPasswordActivity.class));
                finish();
                break;
            case R.id.sign_in_button:
                startActivity(new Intent(SignupActivity.this, SignInActivity.class));
                finish();
                break;
        }
    }
}
