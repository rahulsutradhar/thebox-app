package one.thebox.android.services;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.crashlytics.android.Crashlytics;

import java.util.HashMap;

import io.fabric.sdk.android.Fabric;
import one.thebox.android.BuildConfig;
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
        if (isAuthenticated() && BuildConfig.enableCrashlytics) {
            try {
                User user = PrefUtils.getUser(TheBox.getAppContext());

                if (Fabric.isInitialized() && user != null) {
                    // set user info to crashlytics
                    Crashlytics.setUserIdentifier(user.getUuid());
                    Crashlytics.setUserName(user.getName());
                    Crashlytics.setUserEmail(user.getEmail());
                    Crashlytics.setString("phone_number", user.getPhoneNumber());
                    Crashlytics.setInt("user_id", user.getId());
                    Crashlytics.setString("Platform", "Android");

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

    /**
     * Post user information to clever tab
     */
    public void setCleverTapOnLogin() {
        if (isAuthenticated()) {
            TheBox.getCleverTap().onUserLogin(getCleverTabData());
        }
    }

    public void setCleverTapUserProfile() {
        TheBox.getCleverTap().profile.push(getCleverTabData());
    }

    private HashMap<String, Object> getCleverTabData() {
        HashMap<String, Object> profileUpdate = new HashMap<String, Object>();
        try {
            User user = PrefUtils.getUser(TheBox.getAppContext());

            if (user.getName() != null) {
                if (!user.getName().isEmpty()) {
                    profileUpdate.put("Name", user.getName());
                }
            }
            if (user.getEmail() != null) {
                if (!user.getEmail().isEmpty()) {
                    profileUpdate.put("Email", user.getPhoneNumber());
                }
            }
            profileUpdate.put("Unique-Id", user.getUuid());
            profileUpdate.put("Identity", user.getPhoneNumber());
            profileUpdate.put("User Id", user.getId());
            profileUpdate.put("Platform", "Android");

            PackageInfo pInfo = null;
            pInfo = TheBox.getAppContext().getPackageManager().getPackageInfo(
                    TheBox.getAppContext().getPackageName(), 0);

            profileUpdate.put("App Version Name", pInfo.versionName);
            profileUpdate.put("App Version Code", pInfo.versionCode);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return profileUpdate;
    }

    /**
     * Temporary data setup for already loggedin users
     */
    public void setUserDataToCrashlyticsTemp() {
        if (BuildConfig.enableCrashlytics) {
            try {
                User user = PrefUtils.getUser(TheBox.getAppContext());

                if (Fabric.isInitialized() && user != null) {
                    // set user info to crashlytics
                    Crashlytics.setUserIdentifier(user.getUuid());
                    Crashlytics.setUserName(user.getName());
                    Crashlytics.setUserEmail(user.getEmail());
                    Crashlytics.setString("phone_number", user.getPhoneNumber());
                    Crashlytics.setInt("user_id", user.getId());
                    Crashlytics.setString("Platform", "Android");

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
