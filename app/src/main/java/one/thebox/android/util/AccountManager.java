package one.thebox.android.util;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import one.thebox.android.Helpers.RealmController;
import one.thebox.android.activity.MainActivity;
import one.thebox.android.activity.OtpVerificationActivity;
import one.thebox.android.activity.RequestOtpActivity;
import one.thebox.android.activity.SplashActivity;
import one.thebox.android.app.TheBox;

/**
 * Created by vaibhav on 07/01/17.
 */

public class AccountManager {

    public void deleteAccountData(Context context) {
        PrefUtils.removeAll(TheBox.getInstance());
        RealmController.clean_db();

        //open splash activity
        Intent intent = new Intent(context, SplashActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);

    }

    public void deleteAndNavigateToLogin(Context context) {
        PrefUtils.removeAll(TheBox.getInstance());
        RealmController.clean_db();

        //open request otp activity for login
        Intent intent = new Intent(context, RequestOtpActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);

    }
}
