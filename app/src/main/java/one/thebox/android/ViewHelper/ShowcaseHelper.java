package one.thebox.android.ViewHelper;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Paint;
import android.os.Build;
import android.text.TextPaint;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;

import com.github.amlcurran.showcaseview.OnShowcaseEventListener;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;

import one.thebox.android.R;
import one.thebox.android.app.TheBox;
import one.thebox.android.util.DisplayUtil;
import one.thebox.android.util.PrefUtils;


/**
 * Created by 32 on 30-05-2016.
 */

public class ShowcaseHelper {

    private Activity context;
    private int id;
    private ShowcaseView showcaseView;
    private static final String PREF_SHOW_CASE_STATUS = "show_case_status";
    private static final int EXTRA_PADDING = DisplayUtil.dpToPx(TheBox.getInstance(), 16);
    private OnCompleteListener onCompleteListener;
    private boolean shouldBeOnMiddle;
    private int topPadding;
    private int leftPadding;
    private int bottomPadding;
    private int rightPadding;

    public ShowcaseHelper(Activity context, int id) {
        this.context = context;
        this.id = id;
    }


    public ShowcaseHelper setTopPadding(int topPadding) {
        this.topPadding = DisplayUtil.dpToPx(TheBox.getInstance(), topPadding);
        return this;
    }

    public ShowcaseHelper setLeftPadding(int leftPadding) {
        this.leftPadding = DisplayUtil.dpToPx(TheBox.getInstance(), leftPadding);
        return this;
    }

    public ShowcaseHelper setBottomPadding(int bottomPadding) {
        this.bottomPadding = DisplayUtil.dpToPx(TheBox.getInstance(), bottomPadding);
        return this;
    }


    public ShowcaseHelper setRightPadding(int rightPadding) {
        this.rightPadding = DisplayUtil.dpToPx(TheBox.getInstance(), rightPadding);
        return this;
    }

    public boolean isShouldBeOnMiddle() {
        return shouldBeOnMiddle;
    }

    public void setShouldBeOnMiddle(boolean shouldBeOnMiddle) {
        this.shouldBeOnMiddle = shouldBeOnMiddle;
    }

    public ShowcaseHelper show(final String title, final String description, final View view) {

        if (PrefUtils.getBoolean(context, PREF_SHOW_CASE_STATUS + id, true)) {
            build(title, description, view);
        }
        return this;
    }

    public void setOnCompleteListener(OnCompleteListener onCompleteListener) {
        this.onCompleteListener = onCompleteListener;
    }

    private void build(final String title, final String description, final View view) {
        PrefUtils.putBoolean(context, PREF_SHOW_CASE_STATUS + id, false);
        final TextPaint paintTitle = new TextPaint(Paint.LINEAR_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
        paintTitle.setTextSize(DisplayUtil.dpToPx(context, 28));
        paintTitle.setColor(context.getResources().getColor(R.color.white));
        final TextPaint paintDetail = new TextPaint(Paint.LINEAR_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
        paintDetail.setTextSize(DisplayUtil.dpToPx(context, 20));
        paintDetail.setColor(context.getResources().getColor(R.color.primary_inverse_text_color));
        final ViewTarget target = new ViewTarget(view);
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onGlobalLayout() {
                view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                showcaseView = new ShowcaseView.Builder(context)
                        .setTarget(target)
                        .setStyle(R.style.CustomShowcaseTheme3)
                        .setContentTitle(title)
                        .replaceEndButton(R.layout.layout_showcase_skip_button)
                        .setContentText(description)
                        .setShowcaseDrawer(new CustomShowcaseView
                                (context.getResources(), view.getMeasuredWidth() + EXTRA_PADDING,
                                        view.getMeasuredHeight() + EXTRA_PADDING + topPadding))
                        .setContentTitlePaint(paintTitle)
                        .setContentTextPaint(paintDetail)
                        .setShowcaseEventListener(new OnShowcaseEventListener() {
                            @Override
                            public void onShowcaseViewHide(ShowcaseView showcaseView) {
                                if(onCompleteListener!=null) {
                                    onCompleteListener.onComplete();
                                }
                            }

                            @Override
                            public void onShowcaseViewDidHide(ShowcaseView showcaseView) {

                            }

                            @Override
                            public void onShowcaseViewShow(ShowcaseView showcaseView) {

                            }

                            @Override
                            public void onShowcaseViewTouchBlocked(MotionEvent motionEvent) {

                            }
                        })
                        .build();
                showcaseView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        return true;
                    }
                });
                RelativeLayout.LayoutParams lps = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                lps.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                if(shouldBeOnMiddle) {
                    lps.addRule(RelativeLayout.CENTER_HORIZONTAL);
                }else {
                    lps.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                }
                int margin = ((Number) (context.getResources().getDisplayMetrics().density * 36)).intValue();
                lps.setMargins(margin, margin, margin, margin);
                showcaseView.setButtonPosition(lps);

            }
        });
    }

    public interface OnCompleteListener {
        void onComplete();
    }

    public static boolean hasNotBeenViewed(int id){
        return PrefUtils.getBoolean(TheBox.getInstance(), PREF_SHOW_CASE_STATUS + id, false);
    }

    public static void removeAllTutorial() {
        for (int i = 0; i < 10; i++)
            PrefUtils.removeSharedPref(TheBox.getInstance(), PREF_SHOW_CASE_STATUS + i);
    }
}
