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
import com.enpassio1.linoo.utils.InternetConnectivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

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
                    emailEditText.setError(getResources().getString(R.string.error_enter_valid_email));
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    passwordEditText.setError(getResources().getString(R.string.error_enter_password));
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                if (InternetConnectivity.isInternetConnected(SignInActivity.this)) {
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        progressBar.setVisibility(View.GONE);

                                        Intent hiringListActivityIntent = new Intent(SignInActivity.this, HiringListActivity.class);
                                        Bundle bundleFromAuthenticatingActivity = new Bundle();
                                        bundleFromAuthenticatingActivity.putString("userStatus", "oldUser");

                                        hiringListActivityIntent.putExtra("bundleFromAuthenticatingActivity", bundleFromAuthenticatingActivity);

                                        startActivity(hiringListActivityIntent);
                                        finish();

                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Toast.makeText(SignInActivity.this, getResources()
                                                        .getString(R.string.toast_authentication_failed),
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    Toast.makeText(SignInActivity.this, getResources()
                            .getString(R.string.check_internet_connectivity), Toast.LENGTH_SHORT).show();
                }
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
