package one.thebox.android.services.notification;

import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.freshdesk.hotline.Hotline;
import com.freshdesk.hotline.HotlineNotificationConfig;
import com.google.android.gms.gcm.GcmListenerService;

import one.thebox.android.R;
import one.thebox.android.activity.MainActivity;
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
            String notificationInfoString = data.getString("notification_info");
            NotificationInfo notificationInfo = CoreGsonUtils.fromJson(notificationInfoString, NotificationInfo.class);

            if (notificationInfo != null && notificationInfo.getNotificationActions().size() > 0) {
                new NotificationHelper(this, notificationInfo).show();
            } else {
                new NotificationHelper(this, notificationInfo).show();
            }

        }
    }

}