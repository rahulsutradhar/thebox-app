package one.thebox.android.services;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;
import one.thebox.android.Models.User;
import one.thebox.android.app.Keys;
import one.thebox.android.app.TheBox;
import one.thebox.android.util.PrefUtils;

/**
 * Created by developers on 18/02/17.
 */

public class AuthenticationService {

    /**
     * Constructor
     */
    public AuthenticationService() {

    }

    /**
     * Indicates whether the user is authenticated or not
     *
     * @return boolean
     */
    public boolean isAuthenticated() {
        String token = PrefUtils.getToken(TheBox.getAppContext());
        boolean isAuthenticated = PrefUtils.getBoolean(TheBox.getAppContext(), Keys.IS_AUTHENTICATED);

        return (isAuthenticated && !token.equals(""));
    }

    public void setUserDataToCrashlytics() {
        if (isAuthenticated()) {
            try {
                User user = PrefUtils.getUser(TheBox.getAppContext());

                if (Fabric.isInitialized() && user != null) {
                    // set user info to crashlytics
                    Crashlytics.setUserIdentifier(String.valueOf(user.getUserId()));
                    Crashlytics.setUserName(user.getName());
                    Crashlytics.setUserEmail(user.getEmail());
                    Crashlytics.setString("phone_number", user.getPhoneNumber());

                    PackageInfo pInfo = null;
                    pInfo = TheBox.getAppContext().getPackageManager().getPackageInfo(
                            TheBox.getAppContext().getPackageName(), 0);

                    Crashlytics.setString("app_version_name", pInfo.versionName);
                    Crashlytics.setInt("app_version_code", pInfo.versionCode);
                }

            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }

}
