package one.thebox.android.activity;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import org.json.JSONObject;

import io.branch.referral.Branch;
import io.branch.referral.BranchError;
import one.thebox.android.Models.User;
import one.thebox.android.R;
import one.thebox.android.ViewHelper.MutedVideoView;
import one.thebox.android.app.Constants;
import one.thebox.android.services.AuthenticationService;
import one.thebox.android.util.PrefUtils;

/**
 * Created by Ajeet Kumar Meena on 8/10/15.
 */
public class SplashActivity extends Activity {

    private static final int DELAY = 0;
    private MutedVideoView vidHolder;
    private AuthenticationService authenticationService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        try {
            setContentView(R.layout.video_splash);
            authenticationService = new AuthenticationService();
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
        try {
            String token = PrefUtils.getToken(SplashActivity.this);
            User user = PrefUtils.getUser(SplashActivity.this);

            if (authenticationService.isAuthenticated()) {

                if (authenticationService.isUserInfoExist()) {
                    /**
                     * Update user information to clevertap and crashlytics everytime users opens the app
                     */
                    authenticationService.setUserDataToCrashlytics();
                    authenticationService.setCleverTapUserProfile();
                }
                startActivity(new Intent(SplashActivity.this, MainActivity.class));

            } else {
                //From next version it should open OnBoardActivity
                //startActivity(new Intent(SplashActivity.this, OnBoardingActivity.class));

                /**
                 * if the key returns false means the key don't exist
                 * so repeat old methord of navigation
                 */
                if ((user == null || user.getName() == null || user.getName().isEmpty()) && token.isEmpty()) {
                    startActivity(new Intent(SplashActivity.this, OnBoardingActivity.class));
                } else if ((user == null || user.getName() == null || user.getName().isEmpty()) && !token.isEmpty()) {
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                } else if (!(user == null || user.getName() == null || user.getName().isEmpty()) && !token.isEmpty()) {

                    /**
                     * Already user loggedin need to push data to crashlytics and clever tap
                     *
                     * Temporarry functions
                     */
                    authenticationService.setUserDataToCrashlyticsTemp();
                    authenticationService.setCleverTapUserProfile();

                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                }
            }
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onStart() {
        super.onStart();

        Branch branch = Branch.getInstance();

        branch.initSession(new Branch.BranchReferralInitListener() {
            @Override
            public void onInitFinished(JSONObject referringParams, BranchError error) {
                if (error == null) {
                    // params are the deep linked params associated with the link that the user clicked -> was re-directed to this app
                    // params will be empty if no data found
                    // ... insert custom logic here ...
                } else {
                    Log.i("TheBox", error.getMessage());
                }
            }
        }, this.getIntent().getData(), this);
    }

    @Override
    public void onNewIntent(Intent intent) {
        this.setIntent(intent);
    }
}