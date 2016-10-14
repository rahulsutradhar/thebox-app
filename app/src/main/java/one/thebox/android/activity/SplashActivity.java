package one.thebox.android.activity;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import one.thebox.android.Models.User;
import one.thebox.android.R;
import one.thebox.android.ViewHelper.MutedVideoView;
import one.thebox.android.util.PrefUtils;

/**
 * Created by Ajeet Kumar Meena on 8/10/15.
 */
public class SplashActivity extends Activity {

    private static final int DELAY = 0;
    private MutedVideoView vidHolder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        try {
            setContentView(R.layout.video_splash);
            vidHolder = (MutedVideoView) findViewById(R.id.splash_video);
            Uri video = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.splash_video);
            vidHolder.setVideoURI(video);
            vidHolder.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    setContentView(R.layout.activity_splash_activty);
                    jump();
                    return true;
                }
            });
            vidHolder.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                public void onCompletion(MediaPlayer mp) {
                    jump();
                }
            });
            vidHolder.start();

        } catch (Exception ex) {
            setContentView(R.layout.activity_splash_activty);
            jump();
        }

    }

    private void jump() {
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
}
/*
Testing commit
 */
