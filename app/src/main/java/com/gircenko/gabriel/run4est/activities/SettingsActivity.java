package com.gircenko.gabriel.run4est.activities;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;

import com.gircenko.gabriel.run4est.App;
import com.gircenko.gabriel.run4est.Constants;
import com.gircenko.gabriel.run4est.R;
import com.gircenko.gabriel.run4est.adapters.UserSpinnerAdapter;
import com.gircenko.gabriel.run4est.models.UserType;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Gabriel Gircenko on 27-Oct-16.
 */

public class SettingsActivity extends ActivityWithPermissions {

    @BindView(R.id.et_name)
    EditText et_name;
    @BindView(R.id.sp_user)
    Spinner sp_user;

    private static final String TAG = "SettingsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
        isSettings = true;

        super.onCreate(savedInstanceState);

        String userId = firebaseAuth.getCurrentUser().getUid();
        DatabaseReference databaseReference = firebaseDatabase.getReference(Constants.USER_DATA).child(userId).child(Constants.NAME);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) onNameRetrieved(dataSnapshot.getValue().toString());
                else onNameError();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                onNameError();
            }
        });

        sp_user.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                setName(sp_user.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    protected void setAdapter(UserSpinnerAdapter adapter) {
        sp_user.setAdapter(adapter);
    }

    public void showSpinner() {
        sp_user.setVisibility(View.VISIBLE);
        if (sp_user.getSelectedItem() != null) {
            setName(sp_user.getSelectedItem().toString());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                showProgressDialog("Saving... Please, wait.");
                String name = et_name.getText().toString();
                if (name.isEmpty()) {
                    wrongInput();

                } else {
                    String userId;
                    if (App.userType != UserType.USER) {
                        userId = getSelectedUserId(sp_user.getSelectedItemPosition());

                    } else {
                        userId = firebaseAuth.getCurrentUser().getUid();
                    }

                    DatabaseReference databaseReference = firebaseDatabase.getReference(Constants.USER_DATA).child(userId).child(Constants.NAME);
                    databaseReference.setValue(name, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            onSuccess(databaseError == null);
                        }
                    });
                }

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void wrongInput() {
        dismissProgressDialogAndShowToast(null);
    }

    private void onSuccess(boolean isSuccess) {
        if (isSuccess) {
            dismissProgressDialogAndShowToast("Saved successfully.");

        } else {
            dismissProgressDialogAndShowToast("Save unsuccessful. Please, try again.");
        }
    }

    private void setName(String name) {
        et_name.setText(name);
    }

    public void onNameRetrieved(String name) {
        setName(name);
    }

    public void onNameError() {}
}
