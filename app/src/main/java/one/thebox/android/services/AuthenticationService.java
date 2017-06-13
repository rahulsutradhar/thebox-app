package one.thebox.android.services;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;

import java.util.HashMap;

import io.fabric.sdk.android.Fabric;
import one.thebox.android.BuildConfig;
import one.thebox.android.Models.user.User;
import one.thebox.android.ViewHelper.BoxLoader;
import one.thebox.android.api.Responses.authentication.LogoutResponse;
import one.thebox.android.app.Keys;
import one.thebox.android.app.TheBox;
import one.thebox.android.util.AccountManager;
import one.thebox.android.util.PrefUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
                    Crashlytics.setString("user_uuid", user.getUuid());
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
            profileUpdate.put("User Uuid", user.getUuid());
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
                    Crashlytics.setString("user_uuid", user.getUuid());
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
     * Log out
     */
    public void logOut(final Context context, final boolean showLoader) {
        final BoxLoader dialog = new BoxLoader(context);
        if (showLoader) {
            dialog.show();
        }
        TheBox.getAPIService()
                .logOut(PrefUtils.getToken(TheBox.getAppContext()))
                .enqueue(new Callback<LogoutResponse>() {
                    @Override
                    public void onResponse(Call<LogoutResponse> call, Response<LogoutResponse> response) {
                        if (showLoader) {
                            dialog.dismiss();
                        }
                        try {
                            if (response.isSuccessful()) {
                                if (response.body().isStatus()) {
                                    navigateToSplash(context);
                                } else {
                                    Toast.makeText(TheBox.getAppContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                if (response.code() == 401) {
                                    navigateToLogin(context);
                                }
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(TheBox.getAppContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onFailure(Call<LogoutResponse> call, Throwable t) {
                        if (showLoader) {
                            dialog.dismiss();
                        }
                        Toast.makeText(TheBox.getAppContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void navigateToSplash(Context context) {
        AccountManager accountManager = new AccountManager(context);
        accountManager.deleteAccountData();
    }

    public void navigateToLogin(Context context) {
        AccountManager accountManager = new AccountManager(context);
        accountManager.deleteAndNavigateToLogin();
    }


}
