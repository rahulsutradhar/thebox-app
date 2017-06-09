package one.thebox.android.activity;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import org.json.JSONObject;

import io.branch.referral.Branch;
import io.branch.referral.BranchError;
import one.thebox.android.R;
import one.thebox.android.ViewHelper.MutedVideoView;
import one.thebox.android.services.AuthenticationService;
import one.thebox.android.services.SettingService;

/**
 * Created by Ajeet Kumar Meena on 8/10/15.
 */
public class SplashActivity extends Activity {

    private static final int DELAY = 0;
    private MutedVideoView vidHolder;
    private AuthenticationService authenticationService;
    private int requestCounter = 0;


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
            if (authenticationService.isAuthenticated()) {
                fetchSettingFromServer();
            } else {
                /**
                 * Not Authenticated Move to OnBoard Activity
                 */
                startActivity(new Intent(SplashActivity.this, OnBoardingActivity.class));
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Fetch setting from server
     */
    public void fetchSettingFromServer() {
        requestCounter++;
        new SettingService().fetchSettingsFromServer(this, this, 2);
    }

    /**
     * Get Settings Call response From Server
     */
    public void setServerResponseForSettingsCall(boolean isSuccess) {
        if (isSuccess) {
            navigateToHome();
        } else {
            if (requestCounter > 1) {
                Toast.makeText(this, "Something went Wrong", Toast.LENGTH_SHORT).show();
            } else {
                fetchSettingFromServer();
            }
        }
    }

    /**
     * Authenticated navigate to Home
     */
    public void navigateToHome() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
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