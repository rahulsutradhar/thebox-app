package one.thebox.android.Services;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import one.thebox.android.Helpers.OrderHelper;
import one.thebox.android.util.Constants;
import one.thebox.android.util.PrefUtils;

/**
 * Created by Ajeet Kumar Meena on 16-06-2016.
 */

public class UpdateOrderService extends Service {

    private static final String TAG = "UpdateOrderService";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (PrefUtils.getBoolean(UpdateOrderService.this, Constants.PREF_IS_ORDER_IS_LOADING, false)) {
                    PrefUtils.putBoolean(UpdateOrderService.this, Constants.PREF_IS_ORDER_IS_LOADING,
                            false);
                    OrderHelper.getOrderAndNotify();
                }
                stopSelf();
            }
        }, 7000);
        Log.d(TAG, "onCreate");
    }

    @Override
    public void onStart(Intent intent, int startId) {
        Log.d(TAG, "onStart");
        //Note: You can start a new thread and use it for long background processing from here.
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }
}

