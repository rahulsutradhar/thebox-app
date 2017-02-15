package one.thebox.android.Services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.freshdesk.hotline.Hotline;
import com.freshdesk.hotline.HotlineNotificationConfig;
import com.google.android.gms.gcm.GcmListenerService;

import one.thebox.android.R;
import one.thebox.android.activity.SplashActivity;
import one.thebox.android.activity.MainActivity;
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

        Hotline instance = Hotline.getInstance(this);
        HotlineNotificationConfig notificationConfig = new HotlineNotificationConfig()
                .setNotificationSoundEnabled(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(R.mipmap.ic_launcher)
                .launchDeepLinkTargetOnNotificationClick(true)
                .launchActivityOnFinish(MainActivity.class.getName())
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        Hotline.getInstance(getApplicationContext()).setNotificationConfig(notificationConfig);

        if (instance.isHotlineNotification(data)) {
            instance.handleGcmMessage(data);
            return;
        } else {
            Log.d("message received", data.toString());
            String notificationInfoString = data.getString("notification_info");
            NotificationInfo notificationInfo = CoreGsonUtils.fromJson(notificationInfoString, NotificationInfo.class);
            if (notificationInfo.getNotificationActions().get(0).getActionId() < 10) {
                //Updating Prefrences
                PrefUtils.putBoolean(MyApplication.getInstance(), Constants.PREF_IS_ORDER_IS_LOADING, true);
                ActionExecuter.performAction(this, notificationInfo.getNotificationActions().get(0).getActionId(), notificationInfo.getNotificationActions().get(0).getActionExrta());
            } else {
                new NotificationHelper(this, notificationInfo).show();
            }

        }
    }

}