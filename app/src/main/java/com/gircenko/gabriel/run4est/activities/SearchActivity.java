package com.gircenko.gabriel.run4est.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;

import com.gircenko.gabriel.run4est.App;
import com.gircenko.gabriel.run4est.Constants;
import com.gircenko.gabriel.run4est.Helper;
import com.gircenko.gabriel.run4est.R;
import com.gircenko.gabriel.run4est.adapters.UserSpinnerAdapter;
import com.gircenko.gabriel.run4est.intefaces.OnUserTypeReceivedListener;
import com.gircenko.gabriel.run4est.models.UserType;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Gabriel Gircenko on 27-Oct-16.
 */

public class SearchActivity extends ActivityWithPermissions implements
        DatePickerDialog.OnDateSetListener {

    @BindView(R.id.sp_user)
    Spinner sp_user;
    @BindView(R.id.tv_date_start)
    TextView tv_date_start;
    @BindView(R.id.tv_date_end)
    TextView tv_date_end;

    private Calendar calendarStart;
    private Date dateStart;
    private Calendar calendarEnd;
    private Date dateEnd;
    private FirebaseAuth firebaseAuth;
    private DatePickerDialog datePickerDialog;
    private boolean isStart = false;

    private final SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_FORMAT);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);

        super.onCreate(savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();

        calendarStart = Calendar.getInstance();
        dateStart = calendarStart.getTime();
        setDateStart(sdf.format(dateStart));

        calendarEnd = Calendar.getInstance();
        dateEnd = calendarEnd.getTime();
        setDateEnd(sdf.format(dateEnd));
    }

    @Override
    protected void setAdapter(UserSpinnerAdapter adapter) {
        sp_user.setAdapter(adapter);
    }

    @Override
    protected void showSpinner() {
        sp_user.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                Intent intent = new Intent(this, SearchResultActivity.class);
                String userId;
                if (App.userType == UserType.ADMIN) {
                    userId = getSelectedUserId(sp_user.getSelectedItemPosition());

                } else {
                    userId = firebaseAuth.getCurrentUser().getUid();
                }

                intent.putExtra(Constants.BUNDLE_KEY_UID, userId);
                intent.putExtra(Constants.BUNDLE_KEY_DATE_START, tv_date_start.getText().toString());
                intent.putExtra(Constants.BUNDLE_KEY_DATE_END, tv_date_end.getText().toString());
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setDateStart(String date) {
        tv_date_start.setText(date);
    }

    private void setDateEnd(String date) {
        tv_date_end.setText(date);
    }

    @OnClick(R.id.tv_date_start)
    public void onDateStart() {
        isStart = true;
        calendarStart.setTime(dateStart);
        datePickerDialog = new DatePickerDialog(
                this,
                this,
                calendarStart.get(Calendar.YEAR),
                calendarStart.get(Calendar.MONTH),
                calendarStart.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    @OnClick(R.id.tv_date_end)
    public void onDateEnd() {
        isStart = false;
        calendarEnd.setTime(dateEnd);
        datePickerDialog = new DatePickerDialog(
                this,
                this,
                calendarEnd.get(Calendar.YEAR),
                calendarEnd.get(Calendar.MONTH),
                calendarEnd.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        if (isStart) {
            calendarStart.set(year, month, day);
            dateStart = calendarStart.getTime();
            setDateStart(sdf.format(dateStart));

        } else {
            calendarEnd.set(year, month, day);
            dateEnd = calendarEnd.getTime();
            setDateStart(sdf.format(dateEnd));
        }
    }
}
