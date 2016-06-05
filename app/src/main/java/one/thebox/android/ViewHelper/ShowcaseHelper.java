package one.thebox.android.ViewHelper;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.os.Build;
import android.text.TextPaint;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Toast;

import com.github.amlcurran.showcaseview.OnShowcaseEventListener;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;

import one.thebox.android.R;
import one.thebox.android.app.MyApplication;
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
    private static final int EXTRA_PADDING = DisplayUtil.dpToPx(MyApplication.getInstance(), 16);
    private OnCompleteListener onCompleteListener;

    public ShowcaseHelper(Activity context, int id) {
        this.context = context;
        this.id = id;
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
                        .setContentText(description)
                        .setShowcaseDrawer(new CustomShowcaseView
                                (context.getResources(), view.getMeasuredWidth() + EXTRA_PADDING,
                                        view.getMeasuredHeight() + EXTRA_PADDING))
                        .setContentTitlePaint(paintTitle)
                        .setContentTextPaint(paintDetail)
                        .hideOnTouchOutside()
                        .build();
                showcaseView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (onCompleteListener != null) {
                            onCompleteListener.onComplete();
                        }
                        showcaseView.hide();
                        return false;
                    }
                });
                showcaseView.hideButton();
            }
        });
    }

    public interface OnCompleteListener {
        void onComplete();
    }

    public static void removeAllTutorial() {
        for (int i = 0; i < 10; i++)
            PrefUtils.removeSharedPref(MyApplication.getInstance(), PREF_SHOW_CASE_STATUS + i);
    }
}
