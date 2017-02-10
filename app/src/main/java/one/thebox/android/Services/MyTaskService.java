package one.thebox.android.Services;

import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.TaskParams;

/**
 * Created by developers on 10/02/2017 AD.
 */

public class MyTaskService extends GcmTaskService {

    @Override
    public int onRunTask(TaskParams taskParams) {
        return 0;
    }
}
