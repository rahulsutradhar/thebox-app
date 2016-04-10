package one.thebox.android.activity;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import one.thebox.android.R;

/**
 * Created by Ajeet Kumar Meena on 8/10/15.
 */
public class SplashActivity extends AppCompatActivity {

    private static final int DELAY = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_activty);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, OnBoardingActivity.class));
                finish();
            }
        }, DELAY);
    }
}
