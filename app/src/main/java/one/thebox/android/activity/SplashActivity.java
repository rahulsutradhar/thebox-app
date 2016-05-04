package one.thebox.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import one.thebox.android.Models.User;
import one.thebox.android.R;
import one.thebox.android.util.PrefUtils;

/**
 * Created by Ajeet Kumar Meena on 8/10/15.
 */
public class SplashActivity extends BaseActivity {

    private static final int DELAY = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_activty);
        setStatusBarColor(getResources().getColor(R.color.md_black_1000));
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                String token = PrefUtils.getToken(SplashActivity.this);
                User user = PrefUtils.getUser(SplashActivity.this);
                if ((user == null || user.getName() == null || user.getName().isEmpty()) && token.isEmpty()) {
                    startActivity(new Intent(SplashActivity.this, OnBoardingActivity.class));
                } else if ((user == null || user.getName() == null || user.getName().isEmpty()) && !token.isEmpty()) {
                    startActivity(new Intent(SplashActivity.this, FillUserInfoActivity.class));
                } else if (!(user == null || user.getName() == null || user.getName().isEmpty()) && !token.isEmpty()) {
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                }
                finish();
            }
        }, DELAY);
    }
}
