package com.enpassio.linoo.activities;

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

import com.enpassio.linoo.R;
import com.enpassio.linoo.utils.InternetConnectivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

//code below referenced from: https://firebase.google.com/docs/auth/android/manage-users?

public class ResetPasswordActivity extends AppCompatActivity {

    @BindView(R.id.email_id)
    EditText emailEditText;
    @BindView(R.id.sign_up_button)
    Button signUpButton;
    @BindView(R.id.reset_password)
    Button resetPasswordButton;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
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
            startActivity(new Intent(ResetPasswordActivity.this, HiringListActivity.class));
            finish();
        }
    }

    @OnClick(R.id.reset_password)
    public void resetButtonClicked() {
        String email = emailEditText.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            emailEditText.setError(getResources().getString(R.string.error_enter_valid_email));
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        if (InternetConnectivity.isInternetConnected(ResetPasswordActivity.this)) {
            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                emailEditText.setText("");
                                Toast.makeText(ResetPasswordActivity.this, getResources()
                                        .getString(R.string.toast_password_reset_instruction_sent), Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                                startActivity(new Intent(ResetPasswordActivity.this, SignInActivity.class));
                                finish();
                            } else {
                                emailEditText.setError(getResources().getString(R.string.error_email_not_registered));
                            }
                        }
                    });
        } else {
            Toast.makeText(ResetPasswordActivity.this, getResources()
                    .getString(R.string.check_internet_connectivity), Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.sign_up_button)
    public void signUpButtonClick() {
        startActivity(new Intent(ResetPasswordActivity.this, SignupActivity.class));
        finish();
    }
}
