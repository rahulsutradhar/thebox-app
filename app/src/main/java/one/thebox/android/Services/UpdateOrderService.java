package one.thebox.android.Services;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

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
        Toast.makeText(this, "Congrats! MyService Created", Toast.LENGTH_LONG).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(UpdateOrderService.this, "7 Sec Done.", Toast.LENGTH_LONG).show();
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
        Toast.makeText(this, "My Service Started", Toast.LENGTH_LONG).show();
        Log.d(TAG, "onStart");
        //Note: You can start a new thread and use it for long background processing from here.
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "MyService Stopped", Toast.LENGTH_LONG).show();
        Log.d(TAG, "onDestroy");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }
}

