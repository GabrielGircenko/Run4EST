package com.gircenko.gabriel.run4est;

import android.app.Application;

import com.firebase.client.Firebase;
import com.gircenko.gabriel.run4est.models.UserType;

/**
 * Created by Gabriel Gircenko on 27-Oct-16.
 */
public class App extends Application {

    public static UserType userType = UserType.USER;

    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);
    }
}
