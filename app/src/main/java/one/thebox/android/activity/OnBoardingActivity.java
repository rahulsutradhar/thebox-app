package one.thebox.android.activity;


import android.os.Bundle;
import android.support.annotation.Nullable;

import one.thebox.android.R;

/**
 * Created by Developers on 10/12/15.
 */
public class OnBoardingActivity extends BaseActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboard);
        setStatusBarTranslucent(true);
        setStatusBarColor(getResources().getColor(R.color.white));
    }


}