package one.thebox.android.util;

import java.util.List;

import one.thebox.android.R;

public class NotificationInfo {
    public final static int TYPE_FLAG_AUTO_CANCEL = 0;
    public final static int TYPE_FLAG_ONGOING = 1;
    public final static int TYPE_PROGRESS = 2;
    public final static int INDEX_ACTION_NOTIFICATION = 0;
    public final static int INDEX_ACTION_POSITIVE_BUTTON = 1;
    public final static int INDEX_ACTION_NEGATIVE_BUTTON = 2;
    public final static int INDEX_ICON_NOTIFICATION = 0;
    public final static int[] ICON_IDS = {R.drawable.ic_box};
    List<NotificationAction> notificationActions;
    private int notificationId;
    private int positiveButtonIconId;
    private int negativeButtonIconId;
    private int type;
    private String contentTitle;
    private String largeIcon;
    private String contentImageUrl;
    private String contentText;
    private String negativeButtonText;
    private String positiveButtonText;

    public NotificationInfo() {
    }

    public NotificationInfo(int notificationId, String contentTitle, String positiveButtonText, String negativeButtonText, List<NotificationAction> notificationActions) {
        this.notificationId = notificationId;
        this.contentTitle = contentTitle;
        this.notificationActions = notificationActions;
        this.positiveButtonText = positiveButtonText;
        this.negativeButtonText = negativeButtonText;
    }

    public NotificationInfo(int notificationId, String contentTitle, String contentText, String largeIcon, String positiveButtonText, String negativeButtonText) {
        this.notificationId = notificationId;
        this.positiveButtonText = positiveButtonText;
        this.negativeButtonText = negativeButtonText;
        this.contentTitle = contentTitle;
        this.largeIcon = largeIcon;
        this.contentText = contentText;
    }


    public NotificationInfo(int notificationId, String contentTitle, String contentText) {
        this.notificationId = notificationId;
        this.contentTitle = contentTitle;
        this.contentText = contentText;
    }

    public NotificationInfo(int notificationId, String contentTitle, String titleText, String largeIcon) {
        this.notificationId = notificationId;
        this.contentTitle = contentTitle;
        this.largeIcon = largeIcon;
    }

    public NotificationInfo(int positiveButtonIconId, int negativeButtonIconId,
                            String contentTitle, String largeIcon, String contentImageUrl,
                            String titleText, String contentText, String negativeButtonText, String positiveButtonText,
                            String prevActivity, List<NotificationAction> notificationActions) {
        this.positiveButtonIconId = positiveButtonIconId;
        this.negativeButtonIconId = negativeButtonIconId;
        this.contentTitle = contentTitle;
        this.largeIcon = largeIcon;
        this.contentImageUrl = contentImageUrl;
        this.contentText = contentText;
        this.negativeButtonText = negativeButtonText;
        this.positiveButtonText = positiveButtonText;
        this.notificationActions = notificationActions;
    }

    public NotificationInfo(String contentTitle) {
        this.contentTitle = contentTitle;
    }

    public NotificationInfo(String contentTitle, String contentText) {
        this.contentTitle = contentTitle;
        this.contentText = contentText;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getPositiveButtonIconId() {
        return positiveButtonIconId;
    }

    public void setPositiveButtonIconId(int positiveButtonIconId) {
        this.positiveButtonIconId = positiveButtonIconId;
    }

    public int getNegativeButtonIconId() {
        return negativeButtonIconId;
    }

    public void setNegativeButtonIconId(int negativeButtonIconId) {
        this.negativeButtonIconId = negativeButtonIconId;
    }

    public String getLargeIcon() {
        return largeIcon;
    }

    public void setLargeIcon(String largeIcon) {
        this.largeIcon = largeIcon;
    }

    public String getContentImageUrl() {
        return contentImageUrl;
    }

    public void setContentImageUrl(String contentImageUrl) {
        this.contentImageUrl = contentImageUrl;
    }

    public String getContentText() {
        return contentText;
    }

    public void setContentText(String contentText) {
        this.contentText = contentText;
    }

    public String getNegativeButtonText() {
        return negativeButtonText;
    }

    public void setNegativeButtonText(String negativeButtonText) {
        this.negativeButtonText = negativeButtonText;
    }

    public String getPositiveButtonText() {
        return positiveButtonText;
    }

    public void setPositiveButtonText(String positiveButtonText) {
        this.positiveButtonText = positiveButtonText;
    }

    public List<NotificationAction> getNotificationActions() {
        return notificationActions;
    }

    public void setNotificationActions(List<NotificationAction> notificationActions) {
        this.notificationActions = notificationActions;
    }

    public String getContentTitle() {
        return contentTitle;
    }

    public void setContentTitle(String contentTitle) {
        this.contentTitle = contentTitle;
    }

    public int getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(int notificationId) {
        this.notificationId = notificationId;
    }

    enum NotificationType {
        TEXT_CONTENT(0),
        IMAGE_CONTENT(1),
        LARGE_CONTENT(2);

        private final int notificationTypeId;

        NotificationType(int notificationTypeId) {
            this.notificationTypeId = notificationTypeId;
        }

        public int getNotificationTypeId() {
            return notificationTypeId;
        }
    }

    public static class NotificationAction {
        private int actionId;
        private ActionExecuter.ActionName actionName;
        private String actionExrta;

        public NotificationAction(ActionExecuter.ActionName actionName, String actionExrta) {
            this.actionName = actionName;
            this.actionExrta = actionExrta;
        }

        public NotificationAction(int actionId, String actionExrta) {
            this.actionId = actionId;
            this.actionExrta = actionExrta;
        }

        public ActionExecuter.ActionName getActionName() {
            return actionName;
        }

        public void setActionName(ActionExecuter.ActionName actionName) {
            this.actionName = actionName;
        }

        public String getActionExrta() {
            return actionExrta;
        }

        public void setActionExrta(String actionExrta) {
            this.actionExrta = actionExrta;
        }

        public int getActionId() {
            return actionId;
        }

        public void setActionId(int actionId) {
            this.actionId = actionId;
        }
    }
}