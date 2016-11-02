package com.gircenko.gabriel.run4est.activities;

import android.os.Bundle;
import android.util.Log;
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
import com.sun.jersey.api.client.Client;

import javax.ws.rs.core.MediaType;

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

    private FirebaseSdkJersey firebaseSdkJersey;

    private static final String TAG = "SettingsActivity";

    private final String API_URL = "https://run4est-f7ab6.firebaseio.com/" + Constants.USER_DATA + "/";
    private final String API_KEY = "rn9qXcajH5N6QmMavhWRn32kUuKR5fUKfkiXFC82";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
        isSettings = true;

        super.onCreate(savedInstanceState);

        Client client = Client.create();
        firebaseSdkJersey = new FirebaseSdkJersey(API_URL, API_KEY, client);

        if (App.userType == UserType.USER) {
            getNameFromDatabase();
        }

//        String userId = firebaseAuth.getCurrentUser().getUid();
//        DatabaseReference databaseReference = firebaseDatabase.getReference(Constants.USER_DATA).child(userId).child(Constants.NAME);
//        databaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                if (dataSnapshot.getValue() != null) onNameRetrieved(dataSnapshot.getValue().toString());
//                else onNameError();
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                onNameError();
//            }
//        });

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

                    setNameInDatabase(userId, name);

//                    DatabaseReference databaseReference = firebaseDatabase.getReference(Constants.USER_DATA).child(userId).child(Constants.NAME);
//                    databaseReference.setValue(name, new DatabaseReference.CompletionListener() {
//                        @Override
//                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
//                            onSuccess(databaseError == null);
//                        }
//                    });
                }

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void wrongInput() {
        dismissProgressDialogAndShowToast("Please, write the name");
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

    private void onNameRetrieved(String name) {
        setName(name);
    }

    private void onNameError() {
        // TODO
    }

    private void setNameInDatabase(final String userId, final String name) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String jsonString = "{ \"" + Constants.NAME + "\": \"" + name + "\"}";
                    firebaseSdkJersey.setValue(userId, jsonString);
                    onSuccess(true);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void getNameFromDatabase() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final String returnValue = firebaseSdkJersey.getValue(firebaseAuth.getCurrentUser().getUid() + "/" + Constants.NAME);
                    Log.d(TAG, "value = " + returnValue);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            onNameRetrieved(!returnValue.equals("null") ? returnValue.replace("\"", "") : "");
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    class FirebaseSdkJersey {
        private static final String DEFAULT_AUTH_PARAM_NAME = "auth";
        private static final String DEFAULT_PATH_FORMAT = "%s.json";

        private final String credentials;
        private final String url;
        private final Client client;

        private String authParamName = DEFAULT_AUTH_PARAM_NAME;
        private String pathFormat = DEFAULT_PATH_FORMAT;

        public FirebaseSdkJersey(String url, String credentials, Client client) {
            this.url = url;
            this.credentials = credentials;
            this.client = client;
        }

        public void setValue(String path, String value) throws Exception {
            client.resource(url).path(String.format(pathFormat, path))
                    .queryParam(authParamName, credentials)
                    .type(MediaType.APPLICATION_JSON).entity(value)
                    .put(String.class);
        }

        public String getValue(String path) throws Exception {
            return client.resource(url).path(String.format(pathFormat, path))
                    .queryParam(authParamName, credentials)
                    .get(String.class);
        }

        public void deleteValue(String path) throws Exception {
            client.resource(url).path(String.format(pathFormat, path))
                    .queryParam(authParamName, credentials).delete(String.class);
        }
    }
}
