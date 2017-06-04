package one.thebox.android.services.cart;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import one.thebox.android.app.Constants;

/**
 * Created by developers on 01/06/17.
 */

public class SyncCartService extends Service {
    private static Thread timer;
    private static long INTERVAL = Constants.UPDATE_CART_POLLING_TIME;

    /**
     * Class for clients to access.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with
     * IPC.
     */
    public class CartBinder extends Binder {
        SyncCartService getService() {
            return SyncCartService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        CartHelperService.setCartSyncRunning(true);

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //TODO timer and call the network
        startTimer(this);

        return START_NOT_STICKY;
    }

    // This is the object that receives interactions from clients.  See
    // RemoteService for a more complete example.
    private final IBinder mBinder = new CartBinder();

    @Override
    public void onDestroy() {
        super.onDestroy();
        cancelTimer();
        CartHelperService.setCartSyncRunning(false);
    }

    private void startTimer(final Context context) {
        timer = new Thread() {
            public void run() {
                while (true) {
                    try {
                        sleep(INTERVAL);
                        CartHelperService.updateCartToServer(context,null);
                    } catch (InterruptedException e) {
                        e.printStackTrace();

                        break;
                    } finally {

                    }
                }
            }
        };
        timer.start();

    }

    private void cancelTimer() {
        // kill timer thread
        try {
            timer.interrupt();
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
