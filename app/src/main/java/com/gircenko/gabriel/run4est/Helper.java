package com.gircenko.gabriel.run4est;

import android.util.Log;

import com.gircenko.gabriel.run4est.adapters.JogPagerAdapter;
import com.gircenko.gabriel.run4est.intefaces.OnUserTypeReceivedListener;
import com.gircenko.gabriel.run4est.models.JogModel;
import com.gircenko.gabriel.run4est.models.JogModelWithId;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Gabriel Gircenko on 30-Oct-16.
 */

public class Helper {

    private static final long DAY = 1000 * 60 * 60 * 24;
    private static final String TAG = "Helper";

    public static String getAverageSpeed(String time, long distance) {
        SimpleDateFormat sdf = new SimpleDateFormat(Constants.TIME_FORMAT);
        try {
            Date date = sdf.parse(time);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            int hours = calendar.get(Calendar.HOUR_OF_DAY);
            int minutes = calendar.get(Calendar.MINUTE);
            float totalHours = (float) minutes / 60 + hours;
            float speed = distance / totalHours;
            return String.format("%.2f", speed);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static JogModelWithId getJog(DataSnapshot dataSnapshot) {
        return new JogModelWithId(
                dataSnapshot.getValue(JogModel.class),
                dataSnapshot.getKey());
    }

    public static List<JogModelWithId> getJogList(DataSnapshot dataSnapshot) {
        List<JogModelWithId> jogs = new ArrayList<>();
        Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
        while (iterator.hasNext()) {
            DataSnapshot child = iterator.next();
            jogs.add(new JogModelWithId(
                    child.getValue(JogModel.class),
                    child.getKey()));
        }

        return jogs;
    }

    public static int getDayInLastWeekByDate(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_FORMAT);
        Date dateDate = null;
        try {
            dateDate = sdf.parse(date);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (dateDate != null) {
            Calendar todayDateCalendar = getTodaysCalendar();
            Date now = new Date();
            long todaysDiff = now.getTime() - todayDateCalendar.getTime().getTime();
            long diff = now.getTime() - dateDate.getTime();
            if (todaysDiff < diff) {
                return JogPagerAdapter.PAGE_COUNT - 1 - millisecondsToDays(todayDateCalendar.getTime().getTime() - dateDate.getTime());

            } else if (todaysDiff == diff) {
                return JogPagerAdapter.PAGE_COUNT - 1;

            } else {
                return -1;
            }

        } else {
            return -1;
        }
    }

    public static Calendar getTodaysCalendar() {
        // today
        Calendar gregorianCalendar = new GregorianCalendar();

        // reset hour, minutes, seconds and millis
        gregorianCalendar.set(Calendar.HOUR_OF_DAY, 0);
        gregorianCalendar.set(Calendar.MINUTE, 0);
        gregorianCalendar.set(Calendar.SECOND, 0);
        gregorianCalendar.set(Calendar.MILLISECOND, 0);

        return gregorianCalendar;
    }

    private static int millisecondsToDays(long milliseconds) {
        return (int) (milliseconds / DAY);
    }

    public static void getUserList(FirebaseDatabase firebaseDatabase, final OnUserTypeReceivedListener listener) {
        DatabaseReference databaseReference = firebaseDatabase.getReference(Constants.USER_DATA);
        databaseReference.orderByKey().addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.i(TAG, "onChildAdded");
                listener.onUserAdded(dataSnapshot.getKey(), dataSnapshot.child(Constants.NAME).getValue(String.class));
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.i(TAG, "onChildChanged");
                listener.onUserRemoved(dataSnapshot.getKey());
                listener.onUserAdded(dataSnapshot.getKey(), dataSnapshot.child(Constants.NAME).getValue(String.class));
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.i(TAG, "onChildRemoved");
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
}
