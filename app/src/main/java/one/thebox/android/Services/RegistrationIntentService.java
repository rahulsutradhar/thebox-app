package one.thebox.android.Services;

/**
 * Created by saurabhjain on 30/11/15.
 */

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.freshdesk.hotline.Hotline;
import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;

import one.thebox.android.R;
import one.thebox.android.api.ApiResponse;
import one.thebox.android.api.RequestBodies.MergeCartToOrderRequestBody;
import one.thebox.android.api.RequestBodies.RegistrationIdRequestBody;
import one.thebox.android.app.MyApplication;
import one.thebox.android.util.Constants;
import one.thebox.android.util.PrefUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class RegistrationIntentService extends IntentService {

    private static final String TAG = "RegIntentService";
    private static final String[] TOPICS = {"global"};
    private String gcm_token;

    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        try {
            // [START register_for_gcm]
            // Initially this call goes out to the network to retrieve the gcm_token, subsequent calls
            // are local.
            // R.string.gcm_defaultSenderId (the Sender ID) is typically derived from google-services.json.
            // See https://developers.google.com/cloud-messaging/android/start for details on this file.
            // [START get_gcm_token]
            InstanceID instanceID = InstanceID.getInstance(this);
            String gcm_token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            this.gcm_token = gcm_token;
            // [END get_gcm_token]
            Log.i(TAG, "GCM Registration GcmToken: " + gcm_token);

            // TODO: Implement this method to send any registration to your app's servers.
            sendRegistrationToServer(gcm_token);
            Hotline.getInstance(this).updateGcmRegistrationToken(gcm_token);

            // Subscribe to topic channels
            subscribeTopics(gcm_token);

            // You should store a boolean that indicates whether the generated gcm_token has been
            // sent to your server. If the boolean is false, send the gcm_token to your server,
            // otherwise your server should have already received the gcm_token.
            sharedPreferences.edit().putBoolean(Constants.SENT_TOKEN_TO_SERVER, true).apply();
            // [END register_for_gcm]
        } catch (Exception e) {
            Log.d(TAG, "Failed to complete gcm_token refresh", e);
            // If an exception happens while fetching the new gcm_token or updating our registration data
            // on a third-party server, this ensures that we'll attempt the update at a later time.
            sharedPreferences.edit().putBoolean(Constants.SENT_TOKEN_TO_SERVER, false).apply();
        }
        // Notify UI that registration has completed, so the progress indicator can be hidden.
        Intent registrationComplete = new Intent(Constants.REGISTRATION_COMPLETE);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    /**
     * Persist registration to third-party servers.
     * <p/>
     * Modify this method to associate the user's GCM registration gcm_token with any server-side account
     * maintained by your application.
     *
     * @param gcm_token The new gcm_token.
     */
    private void sendRegistrationToServer(String gcm_token) {
        MyApplication.getAPIService().postRegistrationId(
                PrefUtils.getToken(MyApplication.getInstance()), new RegistrationIdRequestBody(gcm_token))
                .enqueue(new Callback<ApiResponse>() {
                    @Override
                    public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {

                    }

                    @Override
                    public void onFailure(Call<ApiResponse> call, Throwable t) {

                    }
                });
    }

    /**
     * Subscribe to any GCM topics of interest, as defined by the TOPICS constant.
     *
     * @param gcm_token GCM gcm_token
     * @throws IOException if unable to reach the GCM PubSub service
     */
    // [START subscribe_topics]
    private void subscribeTopics(String gcm_token) throws IOException {
        GcmPubSub pubSub = GcmPubSub.getInstance(this);
        for (String topic : TOPICS) {
            pubSub.subscribe(gcm_token, "/topics/" + topic, null);
        }
    }
    // [END subscribe_topics]

}