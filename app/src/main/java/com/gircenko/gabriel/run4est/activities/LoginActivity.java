package com.gircenko.gabriel.run4est.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

public class LoginActivity extends ActivityWithProgressDialog {

    @BindView(R.id.et_email)
    EditText et_email;
    @BindView(R.id.et_password)
    EditText et_password;

    public static final int REQUEST_SIGNUP = 101;
    public static final int SIGNED_UP = 201;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP && resultCode == SIGNED_UP) {
            this.finish();
        }
    }

    @OnClick(R.id.btn_login)
    public void loginTapped() {
        showProgressDialog("Logging in... Please, wait.");

        String email = et_email.getText().toString().trim();
        String password = et_password.getText().toString().trim();

        if (!email.isEmpty() && !password.isEmpty()) {
            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) loginSuccessAndNavigateToMainActivity();
                    else dismissProgressDialogAndShowToast("Couldn't login. Please, try again.");
                }
            });

        } else {
            dismissProgressDialogAndShowToast("Please, fill all the fields");
        }
    }

    private void loginSuccessAndNavigateToMainActivity() {
        dismissProgressDialogAndShowToast("Login successful");

        finish();
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
    }

    @OnClick(R.id.tv_signup)
    public void navigateToSignup() {
        startActivityForResult(new Intent(this, SignUpActivity.class), REQUEST_SIGNUP);
    }
}
