package com.gircenko.gabriel.run4est.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.gircenko.gabriel.run4est.R;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.ButterKnife;

/**
 * Created by Gabriel Gircenko on 27-Oct-16.
 */

public class SplashActivity extends AppCompatActivity {

    private int SPLASH_DISPLAY_LENGTH = 2300;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);

        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() == null) waitThanStartActivity(LoginActivity.class);
        else waitThanStartActivity(MainActivity.class);
    }

    private void waitThanStartActivity(final Class activityClass) {
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                SplashActivity.this.finish();
                startActivity(new Intent(SplashActivity.this, activityClass));
            }

        }, SPLASH_DISPLAY_LENGTH);
    }
}
