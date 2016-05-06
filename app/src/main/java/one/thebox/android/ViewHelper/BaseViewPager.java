package one.thebox.android.ViewHelper;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

public class BaseViewPager extends ViewPager {
    PagerAdapter mPagerAdapter;

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mPagerAdapter != null) {
            super.setAdapter(mPagerAdapter);
        }
    }

    @Override
    public void setAdapter(PagerAdapter adapter) {
        super.setAdapter(mPagerAdapter);
    }

    public void storeAdapter(PagerAdapter pagerAdapter) {
        mPagerAdapter = pagerAdapter;
    }

    public BaseViewPager(Context context) {
        super(context);
    }

    public BaseViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

}