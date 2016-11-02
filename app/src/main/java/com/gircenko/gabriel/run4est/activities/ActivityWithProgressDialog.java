package com.gircenko.gabriel.run4est.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by Gabriel Gircenko on 27-Oct-16.
 */

public class ActivityWithProgressDialog extends AppCompatActivity {

    private ProgressDialog progressDialog;
    protected FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progressDialog = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();
    }

    protected void showProgressDialog(String message) {
        progressDialog.setMessage(message);
        progressDialog.show();
    }

    protected void dismissProgressDialogAndShowToast(String toastMessage) {
        progressDialog.dismiss();
        if (toastMessage != null) Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT).show();
    }
}
