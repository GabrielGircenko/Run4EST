package com.gircenko.gabriel.run4est.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.gircenko.gabriel.run4est.Constants;
import com.gircenko.gabriel.run4est.Helper;
import com.gircenko.gabriel.run4est.R;
import com.gircenko.gabriel.run4est.adapters.JogsWithHeadersListAdapter;
import com.gircenko.gabriel.run4est.models.JogModelWithId;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Gabriel Gircenko on 27-Oct-16.
 */

public class SearchResultActivity extends AppCompatActivity {

    @BindView(R.id.lv_jog_list)
    ListView lv_meal_list;

    private JogsWithHeadersListAdapter adapter;

    private String userId;
    private String dateStart = "";
    private String dateEnd = "";
    private FirebaseDatabase firebaseDatabase;

    private final String TAG = "SearchResultActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        ButterKnife.bind(this);

        firebaseDatabase = FirebaseDatabase.getInstance();

        adapter = new JogsWithHeadersListAdapter(this);
        lv_meal_list.setAdapter(adapter);
        lv_meal_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(SearchResultActivity.this, EditJogActivity.class);
                JogModelWithId jogModelWithId = (JogModelWithId) adapterView.getItemAtPosition(i);
                intent.putExtra(Constants.BUNDLE_KEY_JOG_ID, jogModelWithId.getJogId());
                intent.putExtra(Constants.BUNDLE_KEY_UID, userId);
                intent.putExtra(Constants.BUNDLE_KEY_DATE, jogModelWithId.getJog().getDate());
                startActivity(intent);
            }
        });

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            userId = bundle.getString(Constants.BUNDLE_KEY_UID);
            dateStart = bundle.getString(Constants.BUNDLE_KEY_DATE_START);
            dateEnd = bundle.getString(Constants.BUNDLE_KEY_DATE_END);

            search();
        }
    }

    private void search() {
        DatabaseReference databaseReference = firebaseDatabase.getReference(Constants.USER_JOGS).child(userId);
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.i(TAG, "onChildAdded");
                addJog(Helper.getJog(dataSnapshot));
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.i(TAG, "onChildChanged");
                onJogChanged(Helper.getJog(dataSnapshot));
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.i(TAG, "onChildRemoved");
                removeJog(Helper.getJog(dataSnapshot));
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

    private void onJogChanged(JogModelWithId jog) {
        removeJog(jog);
        addJog(jog);
    }

    private void addJog(JogModelWithId jog) {
        if (jog != null && isDateInRange(jog.getJog().getDate(), dateStart, dateEnd)) {
            adapter.addItem(jog);
        }
    }

    private void removeJog(JogModelWithId jog) {
        adapter.removeItem(jog);
    }

    private boolean isDateInRange(String date, String dateStart, String dateEnd) {
        SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_FORMAT);
        Date parameterDate = null;
        Date parameterDateStart = null;
        Date parameterDateEnd = null;
        try {
            parameterDate = sdf.parse(date);
            parameterDateStart = sdf.parse(dateStart);
            parameterDateEnd = sdf.parse(dateEnd);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return (parameterDate.before(parameterDateEnd) || parameterDate.equals(parameterDateEnd))
                && (parameterDate.after(parameterDateStart) || parameterDate.equals(parameterDateStart));
    }
}
