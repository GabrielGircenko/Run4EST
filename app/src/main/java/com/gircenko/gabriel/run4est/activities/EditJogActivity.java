package com.gircenko.gabriel.run4est.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;

import com.gircenko.gabriel.run4est.App;
import com.gircenko.gabriel.run4est.Constants;
import com.gircenko.gabriel.run4est.R;
import com.gircenko.gabriel.run4est.adapters.UserSpinnerAdapter;
import com.gircenko.gabriel.run4est.models.JogModel;
import com.gircenko.gabriel.run4est.models.JogModelWithId;
import com.gircenko.gabriel.run4est.models.UserType;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Gabriel Gircenko on 27-Oct-16.
 */

public class EditJogActivity extends ActivityWithPermissions implements
        DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    @BindView(R.id.sp_user)
    protected Spinner sp_user;
    @BindView(R.id.et_distance)
    protected EditText et_distance;
    @BindView(R.id.et_date)
    protected EditText et_date;
    @BindView(R.id.et_time)
    protected EditText et_time;

    private Calendar dateCalendar;
    private Date date;
    private Calendar timeCalendar;
    private Date time;

    private JogModelWithId jogModelWithId;
    private String userId;
    private String jogId;
    private String jogDate;
    private boolean showDelete = true;

    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT);
    private final SimpleDateFormat timeFormat = new SimpleDateFormat(Constants.TIME_FORMAT);
    private final String TAG = "EditJogActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_edit_jog);
        ButterKnife.bind(this);

        super.onCreate(savedInstanceState);

        initializeDateAndTime();

        userId = getIntent().getStringExtra(Constants.BUNDLE_KEY_UID);
        jogId = getIntent().getStringExtra(Constants.BUNDLE_KEY_JOG_ID);
        jogDate = getIntent().getStringExtra(Constants.BUNDLE_KEY_DATE);

        if (userId == null) {
            userId = firebaseAuth.getCurrentUser().getUid();
        }

        if (jogId != null && jogDate != null && userId != null) {
            if (jogModelWithId == null) {
                jogModelWithId = new JogModelWithId(null, null);
            }

            jogModelWithId.setJogId(jogId);
            jogModelWithId.setJog(new JogModel(jogDate, null, 0));
            getJog(userId, jogDate, jogId);

        } else {
            showDelete = false;
            invalidateOptionsMenu();
        }
    }

    @Override
    public void setAdapter(UserSpinnerAdapter adapter) {
        sp_user.setAdapter(adapter);
    }

    @Override
    public void showSpinner() {
        if (!showDelete) {
            sp_user.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_jog, menu);
        if (!showDelete) {
            MenuItem item = menu.findItem(R.id.action_delete);
            item.setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                showProgressDialog("Deleting... Please, wait.");
                if (jogModelWithId != null && jogModelWithId.getJogId() != null) {
                    String user = userId;
                    if (App.userType == UserType.ADMIN && !showDelete) {
                        user = getSelectedUserId(sp_user.getSelectedItemPosition());
                    }
                    DatabaseReference databaseReference = firebaseDatabase.getReference(Constants.USER_JOGS).child(user).child(jogModelWithId.getJogId());
                    databaseReference.removeValue(new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError == null) onJogDeleteSuccessful();
                            else onJogDeleteFailed();
                        }
                    });
                }

                return true;

            case R.id.action_save:
                showProgressDialog("Saving... Please, wait.");
                String distance = et_distance.getText().toString();
                String date = et_date.getText().toString();
                String time = et_time.getText().toString();

                if (!time.equals("00:00") && !distance.equals("0")) {

                    if (App.userType == UserType.ADMIN && !showDelete) {
                        String user = getSelectedUserId(sp_user.getSelectedItemPosition());
                        attemptToSaveJog(user, distance, date, time);

                    } else {
                        attemptToSaveJog(userId, distance, date, time);
                    }
                } else {
                    onRunSaveFailedDueToIncorrectInput();
                }

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void getJog(String userId, final String date, String jogId) {
        DatabaseReference databaseReference = firebaseDatabase.getReference(Constants.USER_JOGS).child(userId);
        databaseReference.orderByKey().equalTo(jogId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.i(TAG, "onChildAdded");
                if (date.equals(dataSnapshot.getValue(JogModel.class).getDate())) {
                    onGotNewJog(new JogModelWithId(
                            dataSnapshot.getValue(JogModel.class),
                            dataSnapshot.getKey()));
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.i(TAG, "onChildChanged");
                if (date.equals(dataSnapshot.getValue(JogModel.class).getDate())) {
                    onJogChanged(new JogModelWithId(
                            dataSnapshot.getValue(JogModel.class),
                            dataSnapshot.getKey()));
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.i(TAG, "onChildRemoved");
                if (date.equals(dataSnapshot.getValue(JogModel.class).getDate())) {
                    onJogRemoved(dataSnapshot.getKey());
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                Log.i(TAG, "onChildMoved");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.i(TAG, "onCancelled");
            }
        });
    }

    private void initializeDateAndTime() {
        dateCalendar = Calendar.getInstance();
        date = dateCalendar.getTime();
        setEditDateField(dateFormat.format(date));

        timeCalendar = Calendar.getInstance();
        timeCalendar.set(Calendar.HOUR_OF_DAY, 0);
        timeCalendar.set(Calendar.MINUTE, 0);
        time = timeCalendar.getTime();
        setEditTimeField(timeFormat.format(time));
    }

    private void attemptToSaveJog(String userId, String distance, String date, String time) {
        if (distance.isEmpty() || date.isEmpty() || time.isEmpty()) {
            onRunSaveFailedDueToIncorrectInput();
            return;
        }

        JogModel jogModel = new JogModel();
        jogModel.setDistance(Integer.valueOf(distance));
        jogModel.setTime(time);
        jogModel.setDate(date);

        if (jogModelWithId == null) {
            jogModelWithId = new JogModelWithId(jogModel, null);

        } else {
            jogModelWithId.setJog(jogModel);
        }

        if (this.userId == null || this.userId.isEmpty()) {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            this.userId = user != null ? user.getUid() : "";
        }

        if (jogModelWithId.getJogId() != null) {
            DatabaseReference databaseReference = firebaseDatabase.getReference(Constants.USER_JOGS).child(userId).child(jogModelWithId.getJogId());
            databaseReference.setValue(jogModel, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    onSaveSuccess(databaseError == null);
                }
            });

        } else {
            DatabaseReference databaseReference = firebaseDatabase.getReference(Constants.USER_JOGS).child(userId);
            databaseReference.push().setValue(jogModel, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    onSaveSuccess(databaseError == null);
                }
            });
        }
    }

    private void setEditDistanceField(String calories) {
        et_distance.setText(calories);
    }

    private void setEditDateField(String date) {
        et_date.setText(date);
    }

    private void setEditTimeField(String time) {
        et_time.setText(time);
    }

    private void onRunSaveSuccessful() {
        dismissProgressDialogAndShowToast("Save successful.");
        finish();
    }

    private void onRunSaveFailed() {
        dismissProgressDialogAndShowToast("Save failed. Please, try again.");
    }

    private void onRunSaveFailedDueToIncorrectInput() {
        dismissProgressDialogAndShowToast("Please, fill all fields");
    }

    private void onJogDeleteSuccessful() {
        dismissProgressDialogAndShowToast("Delete successful.");
        finish();
    }

    private void onJogDeleteFailed() {
        dismissProgressDialogAndShowToast("Delete failed. Please, try again.");
    }

    private void onSaveSuccess(boolean success) {
        if (success) onRunSaveSuccessful();
        else onRunSaveFailed();
    }

    @OnClick(R.id.et_date)
    public void onDateClicked() {
        dateCalendar.setTime(date);
        datePickerDialog = new DatePickerDialog(
                this,
                this,
                dateCalendar.get(Calendar.YEAR),
                dateCalendar.get(Calendar.MONTH),
                dateCalendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    @OnClick(R.id.et_time)
    public void onTimeClicked() {
        timeCalendar.setTime(time);
        timePickerDialog = new TimePickerDialog(
                this,
                this,
                timeCalendar.get(Calendar.HOUR_OF_DAY),
                timeCalendar.get(Calendar.MINUTE),
                true);
        timePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        dateCalendar.set(year, month, day);
        date = dateCalendar.getTime();
        setEditDateField(dateFormat.format(date));
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
        timeCalendar.set(Calendar.HOUR_OF_DAY, hour);
        timeCalendar.set(Calendar.MINUTE, minute);
        time = timeCalendar.getTime();
        setEditTimeField(timeFormat.format(time));
    }

    private void onGotNewJog(JogModelWithId jog) {
        add(jog);
    }

    private void onJogChanged(JogModelWithId jog) {
        remove(jog.getJogId());
        add(jog);
    }

    private void onJogRemoved(String jogId) {
        remove(jogId);
    }

    private void add(JogModelWithId jog) {
        if (this.jogModelWithId == null || this.jogModelWithId.getJogId() == null || this.jogModelWithId.getJogId().equals(jog.getJogId())) {
            this.jogModelWithId = jog;

            setEditDistanceField(String.valueOf(jog.getJog().getDistance()));
            setEditDateField(jog.getJog().getDate());
            setEditTimeField(jog.getJog().getTime());

            try {
                date = dateFormat.parse(jog.getJog().getDate());
                time = timeFormat.parse(jog.getJog().getTime());
            } catch (Exception e) {}
        }
    }

    private void remove(String jogId) {
        if (this.jogModelWithId != null && this.jogModelWithId.getJogId() != null && this.jogModelWithId.getJogId().equals(jogId)) {
            this.jogModelWithId = null;
            initializeDateAndTime();
            setEditDistanceField("");
        }
    }
}
