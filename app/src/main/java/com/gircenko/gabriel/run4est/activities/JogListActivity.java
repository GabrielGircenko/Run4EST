package com.gircenko.gabriel.run4est.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.gircenko.gabriel.run4est.Constants;
import com.gircenko.gabriel.run4est.Helper;
import com.gircenko.gabriel.run4est.R;
import com.gircenko.gabriel.run4est.adapters.JogPagerAdapter;
import com.gircenko.gabriel.run4est.adapters.JogsListAdapter;
import com.gircenko.gabriel.run4est.models.JogModelWithId;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Gabriel Gircenko on 27-Oct-16.
 */

public class JogListActivity extends ActivityWithProgressDialog {

    @BindView(R.id.lv_jog_list)
    ListView lv_jog_list;
    @BindView(R.id.tv_user)
    TextView tv_user;
    @BindView(R.id.tv_total_distance)
    TextView tv_total_distance;

    private JogsListAdapter adapter;
    /** String key is the jogId */
    private Map<String, JogModelWithId> map = new TreeMap<>();
    private int totalDistance = 0;
    private String date;
    private String userId = null;

    private FirebaseDatabase firebaseDatabase;

    private static final String TAG = "JogListActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jogs_list);
        ButterKnife.bind(this);

        firebaseDatabase = FirebaseDatabase.getInstance();

        adapter = new JogsListAdapter(this);
        lv_jog_list.setAdapter(adapter);
        lv_jog_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(JogListActivity.this, EditJogActivity.class);
                JogModelWithId jog = (JogModelWithId) adapterView.getItemAtPosition(i);
                intent.putExtra(Constants.BUNDLE_KEY_JOG_ID, jog.getJogId());
                intent.putExtra(Constants.BUNDLE_KEY_DATE, jog.getJog().getDate());
                intent.putExtra(Constants.BUNDLE_KEY_UID, userId);
                startActivity(intent);
            }
        });

        String date = null;
        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            date = bundle.getString(Constants.BUNDLE_KEY_DATE);
            userId = bundle.getString(Constants.BUNDLE_KEY_UID);
        }

        if (userId != null) {
            if (date != null) {
                setTitle(date);
                this.date = date;
                getJogsByUserAndDate(userId, date);
            }
        }

        setUserEmail(firebaseAuth.getCurrentUser().getEmail());
    }

    private void setJogs(List<JogModelWithId> jogs) {
        adapter.setItemList(jogs);
    }

    private void addJogToList(JogModelWithId jogModelWithId) {
        adapter.addItem(jogModelWithId);
    }

    private void removeItemFromTheList(String jogId) {
        adapter.removeItem(jogId);
    }

    private void setTotalDistance(String totalDistance) {
        tv_total_distance.setText(totalDistance);
    }

    private void setUserEmail(String email) {
        tv_user.setText(email);
    }

    private void getJogsByUserAndDate(String userId, String date) {
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
        if (jog != null) {
            if (jog.getJog().getDate().equals(date)) {
                map.put(jog.getJogId(), jog);
                totalDistance += jog.getJog().getDistance();

                addJogToList(jog);
                setTotalDistance(totalDistance + " km");
            }
        }
    }

    private void removeJog(JogModelWithId jog) {
        if (jog != null) {
            if (map.containsKey(jog.getJogId())) {
                JogModelWithId retrievedJog = map.get(jog.getJogId());
                totalDistance -= retrievedJog.getJog().getDistance();
                map.remove(jog.getJogId());
                removeItemFromTheList(jog.getJogId());
                setTotalDistance(totalDistance + " km");
            }
        }
    }

    private void onJogsChanged(List<JogModelWithId> jogs) {
        if (jogs != null && !jogs.isEmpty()) {
            int day = Helper.getDayInLastWeekByDate(jogs.get(0).getJog().getDate());
            if (day >= 0 && date != null && date.equals(jogs.get(0).getJog().getDate())) {
                map = new TreeMap<>();
                totalDistance = 0;

                Iterator<JogModelWithId> iterator = jogs.iterator();
                while (iterator.hasNext()) {
                    JogModelWithId jog = iterator.next();
                    map.put(jog.getJogId(), jog);
                    totalDistance += jog.getJog().getDistance();
                }

                setJogs(jogs);
                setTotalDistance(totalDistance + " km");
            }

        } else {
            if (jogs == null) {
                jogs = new ArrayList<>();
            }

            map = new TreeMap<>();
            totalDistance = 0;
            setJogs(jogs);
            setTotalDistance(totalDistance + " km");
        }
    }
}
