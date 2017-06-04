package one.thebox.android.services.notification;

import android.content.Intent;

import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.TaskParams;

import one.thebox.android.app.TheBox;

/**
 * Created by developers on 10/02/2017 AD.
 */

public class MyTaskService extends GcmTaskService {

    @Override
    public int onRunTask(TaskParams taskParams) {

        TheBox.getAppContext().sendBroadcast(new Intent("com.google.android.intent.action.GTALK_HEARTBEAT"));
        TheBox.getAppContext().sendBroadcast(new Intent("com.google.android.intent.action.MCS_HEARTBEAT"));

        return GcmNetworkManager.RESULT_SUCCESS;
    }
}
