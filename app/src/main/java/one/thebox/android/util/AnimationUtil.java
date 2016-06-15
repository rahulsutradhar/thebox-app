package one.thebox.android.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import com.eftimoff.androidplayer.actions.property.PropertyAction;


/**
 * Created by Ajeet Kumar Meena on 13-12-2015.
 */
public class AnimationUtil {

    public static PropertyAction actionAppIcon(Activity context, View view) {
        Display display = context.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int length = size.y;
        return PropertyAction.newPropertyAction(view).
                scaleX(width).scaleY(length).
                duration(600).
                interpolator(new DecelerateInterpolator()).
                build();
    }

    public static PropertyAction actionReveal(View view) {
        return PropertyAction.newPropertyAction(view).
                scaleX(0).
                scaleY(0).
                duration(250).
                interpolator(new AccelerateDecelerateInterpolator()).
                build();
    }

    public static PropertyAction actionFade(View view) {
        return PropertyAction.newPropertyAction(view).
                duration(1000).alpha(0).
                interpolator(new AccelerateDecelerateInterpolator()).
                build();
    }

    public static PropertyAction actionLargeToSmall(View view) {
        return PropertyAction.newPropertyAction(view).
                scaleX(0).
                scaleY(0).
                duration(750).
                interpolator(new AccelerateDecelerateInterpolator()).
                build();
    }

    public static PropertyAction actionShowUp(View view) {
        return PropertyAction.newPropertyAction(view).
                scaleX(0).
                scaleY(0).
                duration(750).
                interpolator(new AccelerateDecelerateInterpolator()).
                build();
    }

    public static PropertyAction actionUpToDown(View view) {
        return PropertyAction.newPropertyAction(view).
                scaleX(0).
                scaleY(0).
                duration(750).
                interpolator(new AccelerateDecelerateInterpolator()).
                build();
    }

    public static PropertyAction actionLeftToRight(Activity context, View view) {
        Display display = context.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        return PropertyAction.newPropertyAction(view).
                translationY(width).
                duration(750).
                interpolator(new AccelerateDecelerateInterpolator()).
                build();
    }

    public static PropertyAction actionRightToLeft(View view) {
        return PropertyAction.newPropertyAction(view).
                scaleX(0).
                scaleY(0).
                duration(750).
                interpolator(new AccelerateDecelerateInterpolator()).
                build();
    }

    public static PropertyAction actionInfiniteBlink(View view) {
        return PropertyAction.newPropertyAction(view).
                duration(250).
                alpha(1).
                interpolator(new AccelerateDecelerateInterpolator())
                .build();
    }


}
