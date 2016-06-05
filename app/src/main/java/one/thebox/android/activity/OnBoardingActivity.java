package one.thebox.android.activity;


import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.github.paolorotolo.appintro.AppIntro;

import one.thebox.android.R;
import one.thebox.android.ViewHelper.ViewPageTransformer;
import one.thebox.android.fragment.IntroSlide;

/**
 * Created by Ajeet Kumar Meena on 10/12/15.
 */
public class OnBoardingActivity extends AppIntro {

    @Override
    public void init(Bundle savedInstanceState) {
        showSkipButton(false);
        ((Button) doneButton).setTextColor(getResources().getColor(R.color.primary_text_color));
        //doneButton.isVisible(View.GONE);
        setImageNextButton(null);
        addSlide(IntroSlide.newInstance(R.layout.intro_slide1));
        addSlide(IntroSlide.newInstance(R.layout.intro_slide2));
        addSlide(IntroSlide.newInstance(R.layout.intro_slide3));
        //  addSlide(IntroSlide.newInstance(R.layout.intro_slide4));
        setSeparatorColor(getResources().getColor(R.color.transparent));
        setIndicatorColor(getResources().getColor(R.color.transparent_black), getResources().getColor(R.color.transparent_black));
        initStatusBarColor();
        setCustomTransformer(new ViewPageTransformer(ViewPageTransformer.TransformType.FLOW));
    }

    private void initStatusBarColor() {
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.transparent_black));
        }
    }

    @Override
    public void onSkipPressed() {
    }

    @Override
    public void onNextPressed() {

    }

    @Override
    public void onDonePressed() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    @Override
    public void onSlideChanged() {

    }
}