package one.thebox.android.services;

import android.app.Activity;
import android.content.Context;

import java.lang.annotation.Annotation;

import okhttp3.ResponseBody;
import one.thebox.android.BuildConfig;
import one.thebox.android.Models.update.Setting;
import one.thebox.android.activity.OtpVerificationActivity;
import one.thebox.android.activity.SplashActivity;
import one.thebox.android.api.Responses.setting.SettingsResponse;
import one.thebox.android.app.Constants;
import one.thebox.android.app.TheBox;
import one.thebox.android.util.AccountManager;
import one.thebox.android.util.PrefUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Converter;
import retrofit2.Response;

/**
 * Created by developers on 02/06/17.
 */

public class SettingService {

    /**
     * Constructor
     */
    public SettingService() {

    }

    public void setSettings(Context context, Setting settings) {
        PrefUtils.saveSettings(context, settings);
    }

    public Setting getSettings(Context context) {
        return PrefUtils.getSettings(context);
    }

    public void fetchSettingsFromServer(final Context context, final Activity activity, final int calledFrom) {

        TheBox.getAPIService()
                .getSettings(PrefUtils.getToken(context), BuildConfig.VERSION_CODE)
                .enqueue(new Callback<SettingsResponse>() {
                    @Override
                    public void onResponse(Call<SettingsResponse> call, Response<SettingsResponse> response) {
                        try {
                            if (response.isSuccessful()) {
                                //update setting data locally
                                if (response.body().getSetting() != null) {
                                    setSettings(context, response.body().getSetting());
                                    PrefUtils.saveUser(context, response.body().getSetting().getUser());
                                }

                                if (activity != null) {
                                    sendResponse(activity, true, calledFrom);
                                }
                            } else {
                                if (response.code() == Constants.UNAUTHORIZED) {
                                    //UnAuthorised; logout and move to Splash
                                    new AuthenticationService().navigateToLogin(context);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            if (activity != null) {
                                sendResponse(activity, false, calledFrom);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<SettingsResponse> call, Throwable t) {
                        if (activity != null) {
                            sendResponse(activity, false, calledFrom);
                        }
                    }
                });
    }

    /**
     * 1: OtpVerificationActivity
     * 2: SplashActivity
     */
    public void sendResponse(Activity activity, boolean status, int calledFrom) {
        if (calledFrom == 1) {
            if (((OtpVerificationActivity) activity) != null) {
                ((OtpVerificationActivity) activity).setServerResponseForSettingsCall(status);
            }
        } else if (calledFrom == 2) {
            if (((SplashActivity) activity) != null) {
                ((SplashActivity) activity).setServerResponseForSettingsCall(status);
            }

        }
    }
}
