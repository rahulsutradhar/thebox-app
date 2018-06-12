package one.thebox.android.util;

import android.support.design.widget.Snackbar;
import android.view.View;

/**
 * Created by developers on 02/05/17.
 */

public class CustomSnackBar {

    public static void showLongSnackBar(View view, String message) {
        try {
            Snackbar.make(view, message, Snackbar.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
