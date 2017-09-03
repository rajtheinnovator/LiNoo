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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/* code below referenced from: https://firebase.google.com/docs/auth/android/start/ */

public class SignInActivity extends AppCompatActivity {

    @BindView(R.id.email_id)
    EditText emailEditText;
    @BindView(R.id.password)
    EditText passwordEditText;
    @BindView(R.id.sign_up_button)
    Button signUpButton;
    @BindView(R.id.reset_password)
    Button forgotPasswordButton;
    @BindView(R.id.sign_in_button)
    Button signInButton;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        ButterKnife.bind(this);
        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null) {
            startActivity(new Intent(SignInActivity.this, HiringListActivity.class));
            finish();
        }

    }

    @OnClick(R.id.sign_in_button)
    public void signInButtonClicked() {
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
                                SharedPreferences sharedPreferences = getSharedPreferences("MY_USERS_PROFILE_PREFERENCE", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("email", "email");
                                editor.putString("uId", FirebaseAuth.getInstance().getCurrentUser().getUid());
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
    }

    @OnClick(R.id.reset_password)
    public void resetPasswordButtonClicked() {
        startActivity(new Intent(SignInActivity.this, ResetPasswordActivity.class));
        finish();
    }

    @OnClick(R.id.sign_up_button)
    public void signUpButtonClicked() {
        startActivity(new Intent(SignInActivity.this, SignupActivity.class));
        finish();
    }
}
