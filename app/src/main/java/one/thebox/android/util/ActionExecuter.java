package one.thebox.android.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

import one.thebox.android.Events.UpdateOrderItemEvent;
import one.thebox.android.Events.UpdateUpcomingDeliveriesEvent;
import one.thebox.android.Helpers.OrderHelper;
import one.thebox.android.app.MyApplication;


/**
 * Action executer is an easy to use action performer which can perform all app action.
 */

public class ActionExecuter {

    public static final String ACTION_ID = "action_id";
    public static final String ACTION_EXTRA = "action_extra_string";
    public static final String ACTION_EXECUTER_BROADCAST = "action_executer_broadcast";
    public static final String FLAG_NOTIFICATION = "flag_notification";

    public static void performAction(Context context, Intent intent) {
        boolean hasExtra = intent.getBooleanExtra(FLAG_NOTIFICATION, false);
        if (hasExtra) {
            int actionId = intent.getIntExtra(ACTION_ID, -1);
            if (actionId == -1) {
                return;
            }
            String actionExtra = intent.getStringExtra(ACTION_EXTRA);
            performAction(context, actionId, actionExtra);
        }
    }

    private static void showErrorMessage(final Context context) {
        if (context instanceof Activity) {
            ((Activity) context).runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(context, "An error occurred", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public static void performAction(Context context, String deepLink) {
        if (deepLink.contains("actionId") && deepLink.contains("actionExtra")) {
            int start = deepLink.lastIndexOf("actionId") + 9;
            String actionIdString = "";
            while (deepLink.charAt(start) != '&' || start >= deepLink.length()) {
                actionIdString = actionIdString + deepLink.charAt(start);
                start++;
            }
            int actionId = Integer.parseInt(actionIdString);
            start = deepLink.lastIndexOf("actionExtra") + 12;
            String actionExtra = "";
            while (start < deepLink.length()) {
                actionExtra = actionExtra + deepLink.charAt(start);
                start++;
            }
            ActionExecuter.performAction(context, actionId, actionExtra);
        }
    }

    public static void performAction(final Context context, int actionId, String actionExtra) {

        ActionName actionName = null;
        switch (actionId) {
            case 0: {
                actionName = ActionName.ORDER_HAS_LOADED;
                break;
            }
            case 1: {
                actionName = ActionName.MAIN_ACTIVITY;
                break;
            }
        }
        performAction(context, actionName, actionExtra);
    }

    public static void performAction(final Context context, ActionName actionName, String actionExtra) {
        switch (actionName) {
            case MAIN_ACTIVITY: {
                break;
            }
            case ORDER_HAS_LOADED: {
                if(PrefUtils.getBoolean(MyApplication.getInstance(), Constants.PREF_IS_ORDER_IS_LOADING, false)) {
                    OrderHelper.getOrderAndNotify();
                    PrefUtils.putBoolean(MyApplication.getInstance(), Constants.PREF_IS_ORDER_IS_LOADING, false);
                }
                break;
            }
        }
    }

    private static void broadcastIntent(Context context, ActionName actionName, String actionExtra) {
        Intent intent = new Intent();
        intent.putExtra(ACTION_ID, actionName.getActionId());
        intent.putExtra(ACTION_EXTRA, actionExtra);
        intent.setAction(ACTION_EXECUTER_BROADCAST);
        context.sendBroadcast(intent);
    }

    public enum ActionName {
        ORDER_HAS_LOADED(0),
        MAIN_ACTIVITY(1);

        private final int actionId;

        ActionName(int actionId) {
            this.actionId = actionId;
        }

        public int getActionId() {
            return actionId;
        }
    }
}