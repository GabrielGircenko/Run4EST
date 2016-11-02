package com.gircenko.gabriel.run4est.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.widget.EditText;

import com.gircenko.gabriel.run4est.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Gabriel Gircenko on 27-Oct-16.
 */

public class SignUpActivity extends ActivityWithProgressDialog {

    @BindView(R.id.et_email)
    EditText et_email;
    @BindView(R.id.et_password)
    EditText et_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
    }

    @OnClick(R.id.btn_signup)
    public void signupTapped() {
        showProgressDialog("Signing up... Please, wait.");

        String email = et_email.getText().toString().trim();
        String password = et_password.getText().toString().trim();

        if (!email.isEmpty() && !password.isEmpty()) {
            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) signupSuccessAndNavigateToMainActivity();
                    else dismissProgressDialogAndShowToast("Couldn't sign up the user. Please, try again.");
                }
            });

        } else {
            dismissProgressDialogAndShowToast("Please, fill all the fields");
        }
    }

    private void signupSuccessAndNavigateToMainActivity() {
        dismissProgressDialogAndShowToast("Sign up successful");

        setResult(LoginActivity.SIGNED_UP, null);
        finish();

        startActivity(new Intent(SignUpActivity.this, MainActivity.class));
    }
}
