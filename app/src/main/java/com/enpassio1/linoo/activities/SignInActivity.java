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
import static com.enpassio1.linoo.R.id.sign_up_button;

/* code below referenced from: https://firebase.google.com/docs/auth/android/start/ */

public class SignInActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private EditText emailEditText;
    private EditText passwordEditText;
    private Button signUpButton;
    private Button forgotPasswordButton;
    private Button signInButton;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        mAuth = FirebaseAuth.getInstance();
        emailEditText = (EditText) findViewById(R.id.email_id);
        passwordEditText = (EditText) findViewById(R.id.password);
        signUpButton = (Button) findViewById(sign_up_button);
        forgotPasswordButton = (Button) findViewById(R.id.reset_password);
        signInButton = (Button) findViewById(sign_in_button);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        signUpButton.setOnClickListener(SignInActivity.this);
        forgotPasswordButton.setOnClickListener(SignInActivity.this);
        signInButton.setOnClickListener(SignInActivity.this);

        if (mAuth.getCurrentUser() != null) {
            startActivity(new Intent(SignInActivity.this, HiringListActivity.class));
            finish();
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sign_in_button:
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                if (TextUtils.isEmpty(email)) {
                    emailEditText.setError("Enter a valid email");
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    passwordEditText.setError("Enter your password");
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    progressBar.setVisibility(View.GONE);
                                    startActivity(new Intent(SignInActivity.this, HiringListActivity.class));
                                    finish();
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(SignInActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                break;
            case R.id.reset_password:
                startActivity(new Intent(SignInActivity.this, ResetPasswordActivity.class));
                finish();
                break;
            case sign_up_button:
                startActivity(new Intent(SignInActivity.this, SignupActivity.class));
                finish();
                break;
        }
    }
}
