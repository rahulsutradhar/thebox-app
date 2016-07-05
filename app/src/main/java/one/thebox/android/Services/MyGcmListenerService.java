package one.thebox.android.Services;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

import one.thebox.android.app.MyApplication;
import one.thebox.android.util.ActionExecuter;
import one.thebox.android.util.Constants;
import one.thebox.android.util.CoreGsonUtils;
import one.thebox.android.util.NotificationHelper;
import one.thebox.android.util.NotificationInfo;
import one.thebox.android.util.PrefUtils;

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
        Log.d("message received", data.toString());
        String notificationInfoString = data.getString("notification_info");
        NotificationInfo notificationInfo = CoreGsonUtils.fromJson(notificationInfoString, NotificationInfo.class);
        if (notificationInfo.getNotificationActions().get(0).getActionId() < 10) {
            //Updating Prefrences
            PrefUtils.putBoolean(MyApplication.getInstance(), Constants.PREF_IS_ORDER_IS_LOADING, true);
            ActionExecuter.performAction(this, notificationInfo.getNotificationActions().get(0).getActionId(), notificationInfo.getNotificationActions().get(0).getActionExrta());
        } else {
//            notificationInfo.TYPE_FLAG_AUTO_CANCEL =
            new NotificationHelper(this, notificationInfo).show();
        }
    }
}