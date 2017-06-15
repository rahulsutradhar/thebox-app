package one.thebox.android.util;

import android.content.Context;
import android.content.Intent;

import one.thebox.android.Helpers.RealmController;
import one.thebox.android.activity.RequestOtpActivity;
import one.thebox.android.activity.SplashActivity;
import one.thebox.android.app.TheBox;

/**
 * Created by vaibhav on 07/01/17.
 */

public class AccountManager {

    private Context context;

    public AccountManager(Context context) {
        this.context = context;
    }

    public void deleteAccountData() {
        PrefUtils.removeAll(TheBox.getInstance());
        RealmController.clean_db();

        //<TODO> Move this to a central class
        context.startActivity(new Intent(context, SplashActivity.class));
    }

    public void deleteAndNavigateToLogin() {
        PrefUtils.removeAll(TheBox.getInstance());
        RealmController.clean_db();

        //<TODO> Move this to a central class
        context.startActivity(new Intent(context, RequestOtpActivity.class));
    }
}
