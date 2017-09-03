package com.enpassio.linoo.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.enpassio.linoo.R;
import com.enpassio.linoo.utils.InternetConnectivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/* code below referenced from: https://firebase.google.com/docs/auth/android/start/ */

public class SignupActivity extends AppCompatActivity {

    @BindView(R.id.email_id)
    EditText emailEditText;
    @BindView(R.id.password)
    EditText passwordEditText;
    @BindView(R.id.sign_up_button)
    Button signUpButton;
    @BindView(R.id.reset_password)
    Button forgotPasswordButton;
    @BindView(R.id.sign_in_button)
    Button logInButton;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);
        mAuth = FirebaseAuth.getInstance();
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

    @OnClick(R.id.sign_up_button)
    public void signUpButtonClicked() {
        final String email = emailEditText.getText().toString().trim();
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
        if (InternetConnectivity.isInternetConnected(SignupActivity.this)) {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                progressBar.setVisibility(View.GONE);
                                Intent hiringListActivityIntent = new Intent(SignupActivity.this,
                                        HiringListActivity.class);
                                Bundle bundleFromAuthenticatingActivity = new Bundle();
                                bundleFromAuthenticatingActivity.putString("userStatus", "newUser");

                                hiringListActivityIntent.putExtra("bundleFromAuthenticatingActivity",
                                        bundleFromAuthenticatingActivity);
                                SharedPreferences sharedPreferences = getSharedPreferences("MY_USERS_PROFILE_PREFERENCE", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("email", email);
                                editor.putString("uId", FirebaseAuth.getInstance().getCurrentUser().getUid());
                                editor.apply();
                                startActivity(hiringListActivityIntent);
                                finish();
                            } else {
                                // If sign up fails, display a message to the user.

                                Toast.makeText(SignupActivity.this, getResources()
                                                .getString(R.string.toast_problem_creating_account),
                                        Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
        } else {
            Toast.makeText(SignupActivity.this, getResources()
                    .getString(R.string.check_internet_connectivity), Toast.LENGTH_SHORT).show();
        }

    }

    @OnClick(R.id.reset_password)
    public void resetButtonClicked() {
        startActivity(new Intent(SignupActivity.this, ResetPasswordActivity.class));
        finish();
    }

    @OnClick(R.id.sign_in_button)
    public void signInButtonClicked() {
        startActivity(new Intent(SignupActivity.this, SignInActivity.class));
        finish();
    }
}
