package one.thebox.android.util;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;

import one.thebox.android.Models.Size;

/**
 * Created by Ajeet Kumar Meena on 11-04-2016.
 */
public class DisplayUtil {
    public static Size getDisplaySize(Activity context) {
        DisplayMetrics metrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return new Size(metrics.heightPixels, metrics.widthPixels);
    }

    public static int dpToPx(Context context, int dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

    public static int pxToDp(Context context, int px) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int dp = Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return dp;
    }
}
