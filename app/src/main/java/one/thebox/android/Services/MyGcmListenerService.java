package one.thebox.android.Services;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

import one.thebox.android.util.CoreGsonUtils;
import one.thebox.android.util.NotificationHelper;
import one.thebox.android.util.NotificationInfo;

public class MyGcmListenerService extends GcmListenerService {

    private static final String TAG = "MyGcmListenerService";

    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(String from, Bundle data) {
        Log.d("message received",data.toString());
        String notificationInfoString = data.getString("notification_info");
        NotificationInfo notificationInfo = CoreGsonUtils.fromJson(notificationInfoString, NotificationInfo.class);
        new NotificationHelper(this, notificationInfo).show();

        if (from.startsWith("/topics/")) {

        } else {

        }
    }
}