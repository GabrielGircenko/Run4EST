package com.gircenko.gabriel.run4est.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.gircenko.gabriel.run4est.App;
import com.gircenko.gabriel.run4est.Helper;
import com.gircenko.gabriel.run4est.adapters.UserSpinnerAdapter;
import com.gircenko.gabriel.run4est.intefaces.OnUserTypeReceivedListener;
import com.gircenko.gabriel.run4est.models.UserType;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Gabriel Gircenko on 31-Oct-16.
 */

public abstract class ActivityWithPermissions extends ActivityWithProgressDialog {

    protected FirebaseDatabase firebaseDatabase;
    private UserSpinnerAdapter spinnerArrayAdapter;
    protected boolean isSettings = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firebaseDatabase = FirebaseDatabase.getInstance();

        spinnerArrayAdapter = new UserSpinnerAdapter(this, android.R.layout.simple_spinner_item);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        setAdapter(spinnerArrayAdapter);
        checkForUserType();
    }

    protected abstract void setAdapter(UserSpinnerAdapter adapter);

    private void addUserToSpinner(String userId, String name) {
        spinnerArrayAdapter.addItem(userId, name);
    }

    private void checkForUserType() {
        Helper.getUserList(firebaseDatabase, new OnUserTypeReceivedListener() {

            @Override
            public void onUserAdded(String userId, String name) {
                if (App.userType == UserType.ADMIN || App.userType == UserType.MANAGER && isSettings) {

                    if (name == null || name.isEmpty()) {
                        name = "John Doe";
                    }

                    addUserToSpinner(userId, name);
                    showSpinner();
                }
            }
        });
    }

    protected abstract void showSpinner();

    protected String getSelectedUserId(int position) {
        return spinnerArrayAdapter.getUserId(position);
    }
}
