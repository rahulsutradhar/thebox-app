package one.thebox.android.activity;


import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;

import one.thebox.android.R;
import one.thebox.android.app.TheBox;
import one.thebox.android.util.PrefUtils;

/**
 * Created by Developers on 10/12/15.
 */
public class OnBoardingActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboard);

        try {
            setStatusBarTranslucent(true);
            setStatusBarColor(getResources().getColor(R.color.white));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}