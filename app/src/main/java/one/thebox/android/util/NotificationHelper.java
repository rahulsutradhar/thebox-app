package one.thebox.android.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.squareup.picasso.Picasso;

import java.io.IOException;

import one.thebox.android.R;
import one.thebox.android.activity.MainActivity;

/**
 * Created by Ajeet Kumar Meena on 11-02-2016.
 */
public class NotificationHelper {
    private static final int BACKGROUND_COLOR = R.color.md_red_800;
    private static final int MAX_PROGRESS = 100;
    private NotificationInfo notificationInfo;
    private Context context;
    private NotificationCompat.Builder builder;
    private Bitmap largeIconBitmap, contentBitmap;
    private NotificationManager notificationManager;

    public NotificationHelper(Context context, NotificationInfo notificationInfo) {
        this.notificationInfo = notificationInfo;
        this.context = context;
        this.builder = new NotificationCompat.Builder(context);
        this.notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public static Intent getNotificationExtraIfThere(Intent receivingIntent, Intent passingIntent) {
        boolean hasExtra = receivingIntent.getBooleanExtra(ActionExecuter.FLAG_NOTIFICATION, false);
        if (hasExtra) {
            int actionId = receivingIntent.getIntExtra(ActionExecuter.ACTION_ID, -1);
            if (actionId == -1) {
                return passingIntent;
            }
            String actionExtra = receivingIntent.getStringExtra(ActionExecuter.ACTION_EXTRA);
            passingIntent.putExtra(ActionExecuter.FLAG_NOTIFICATION, true);
            passingIntent.putExtra(ActionExecuter.ACTION_ID, actionId);
            passingIntent.putExtra(ActionExecuter.ACTION_EXTRA, actionExtra);
            return passingIntent;
        }
        return passingIntent;
    }

    public static void cancelNotification(Context context, int id) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(id);
    }

    public void show() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    if (notificationInfo == null) {
                        return;
                    }
                    if (notificationInfo.getLargeIcon() != null && !notificationInfo.getLargeIcon().isEmpty())
                        largeIconBitmap = Picasso.with(context).load(notificationInfo.getLargeIcon()).get();
                    if (notificationInfo.getContentImageUrl() != null && !notificationInfo.getContentImageUrl().isEmpty()) {
                        contentBitmap = Picasso.with(context).load(notificationInfo.getContentImageUrl()).get();
                    }
                    setupBuilderAndShowNotification();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setupBuilderAndShowNotification() {
        setBuilderType();
        setBaseParameter();
        setBitmaps();
        setPendingIntent();
        setNotificationSoundAndLights();
        showNotification();

    }

    public void setupBuilder() {
        setBuilderType();
        setBaseParameter();
        setBitmaps();
        setPendingIntent();
    }

    public void publishProgress(int progress) {
        builder.setProgress(MAX_PROGRESS, progress, false);
        setupBuilderAndShowNotification();
    }

    public void setProgress(int progress) {
        builder.setProgress(MAX_PROGRESS, progress, false);
    }

    public void publishProgress(int progress, int maxProgress) {
        builder.setProgress(maxProgress, progress, false);
        setupBuilderAndShowNotification();
    }

    private void showNotification() {
        Notification notification = builder.build();
        notification.sound = Uri.parse("android.resource://com.listup.android/" + R.raw.custom_notification_sound);
        notification.ledARGB = context.getResources().getColor(R.color.primary);
        this.notificationManager.notify(notificationInfo.getNotificationId(), notification);
    }

    public Notification getNotificaiton() {
        return builder.build();
    }

    private void setPendingIntent() {
        if (notificationInfo.getContentImageUrl() == null
                && notificationInfo.getPositiveButtonText() == null
                && notificationInfo.getNegativeButtonText() == null) {
            if (Build.VERSION.SDK_INT >= 21) {
                builder.setFullScreenIntent(getActionPendingIntent(NotificationInfo.INDEX_ACTION_NOTIFICATION), true);
            }
        }

        builder.setContentIntent(getActionPendingIntent(NotificationInfo.INDEX_ACTION_NOTIFICATION));

        if (notificationInfo.getPositiveButtonText() != null && !notificationInfo.getPositiveButtonText().isEmpty()) {
            builder.addAction(0
                    , notificationInfo.getPositiveButtonText(),
                    getActionPendingIntent(NotificationInfo.INDEX_ACTION_POSITIVE_BUTTON));
        }

        if (notificationInfo.getNegativeButtonText() != null && !notificationInfo.getNegativeButtonText().isEmpty()) {
            builder.addAction(0
                    , notificationInfo.getNegativeButtonText()
                    , getActionPendingIntent(NotificationInfo.INDEX_ACTION_NEGATIVE_BUTTON));
        }

    }

    private void setNotificationSoundAndLights() {
        getNotificaiton().sound = Uri.parse("android.resource://com.listup.android/" + R.raw.custom_notification_sound);
        getNotificaiton().defaults = Notification.DEFAULT_VIBRATE;
        getNotificaiton().ledARGB = context.getResources().getColor(R.color.primary);
    }

    private void setBitmaps() {
        if (largeIconBitmap == null) {
            builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.ic_box));
        } else {
            builder.setLargeIcon(largeIconBitmap);
        }
        if (contentBitmap != null) {
            NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle();
            bigPictureStyle.bigPicture(contentBitmap);
            if (notificationInfo.getContentText() != null && !notificationInfo.getContentText().isEmpty()) {
                bigPictureStyle.setSummaryText(notificationInfo.getContentText());
            }
            builder.setStyle(bigPictureStyle);
        }
    }

    private void setBaseParameter() {
        builder.setPriority(NotificationCompat.PRIORITY_MAX);
        builder.setColor(context.getResources().getColor(BACKGROUND_COLOR));
        builder.setSmallIcon(NotificationInfo.ICON_IDS[NotificationInfo.INDEX_ICON_NOTIFICATION]);
        builder.setVibrate(new long[]{1, 1, 1});
        if (notificationInfo.getContentTitle() != null && !notificationInfo.getContentTitle().isEmpty()) {
            builder.setContentTitle(notificationInfo.getContentTitle());
        }
        if (notificationInfo.getContentText() != null && !notificationInfo.getContentText().isEmpty()) {
            builder.setContentText(notificationInfo.getContentText());
            if (contentBitmap == null) {
                builder.setStyle(new NotificationCompat.BigTextStyle().bigText(notificationInfo.getContentText()));
            }
        }
    }

    private void setBuilderType() {
        switch (notificationInfo.getType()) {
            case NotificationInfo.TYPE_FLAG_AUTO_CANCEL: {
                builder.setAutoCancel(true);
                break;
            }
            case NotificationInfo.TYPE_FLAG_ONGOING: {
                builder.setOngoing(true);
                break;
            }
            default: {
                builder.setAutoCancel(true);
            }
        }
    }

    private PendingIntent getActionPendingIntent(int index) {
        if (notificationInfo.getNotificationActions() != null && !notificationInfo.getNotificationActions().isEmpty()
                && notificationInfo.getNotificationActions().size() >= index + 1) {
            Intent intent = new Intent(context, MainActivity.class);
            intent.putExtra("extra_tab_no", notificationInfo.getNotificationActions().get(index).getActionId());
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);

            int iUniqueId = (int) (System.currentTimeMillis() & 0xfffffff);
            return PendingIntent.getActivity(context, iUniqueId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }
        return null;
    }

    public NotificationInfo getNotificationInfo() {
        return notificationInfo;
    }

    public void setNotificationInfo(NotificationInfo notificationInfo) {
        this.notificationInfo = notificationInfo;
    }
}
