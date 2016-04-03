package one.thebox.android.app;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.analytics.Tracker;

import one.thebox.android.data.DatabaseHandler;
import one.thebox.android.util.AccountUtil;
import one.thebox.android.util.LruBitmapCache;
import one.thebox.android.util.PrefManager;
import one.thebox.android.util.SyncUtil;

/**
 * Created by harsh on 10/12/15.
 */
public class MyApplication extends Application {

    public final String TAG = MyApplication.class.getSimpleName();

    private static MyApplication myApplication;

    private Tracker mTracker;

    private PrefManager pref;
    private AccountUtil accountUtil;
    private SyncUtil syncUtil;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;

    @Override
    public void onCreate() {
        super.onCreate();
        myApplication = this;
    }

    public static synchronized MyApplication getInstance() {
        return myApplication;
    }

/*TODO: Google Analytics
    synchronized public Tracker getGoogleAnalyticsTracker() {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
            mTracker = analytics.newTracker(R.xml.global_tracker);
        }
        return mTracker;
    }
*/

    public PrefManager getPrefManager() {
        if (pref == null)
            pref = new PrefManager(this);

        return pref;
    }

    public AccountUtil getAccountUtil() {
        if (accountUtil == null)
            accountUtil = new AccountUtil(this);

        return accountUtil;
    }

    public SyncUtil getSyncUtil() {
        if (syncUtil == null)
            syncUtil = new SyncUtil(this);

        return syncUtil;
    }

    public void clearUserData(SQLiteDatabase db) {
        // cancel all volley requests
        getRequestQueue().cancelAll(this);

        // Clearing all the shared preferences
        getPrefManager().resetPreferences();

        // Clear all the database tables
        DatabaseHandler.getInstance(this).dropAllTables(db);
    }

    //    Volley Method
    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(this);
        }

        return mRequestQueue;
    }

    public ImageLoader getImageLoader() {
        getRequestQueue();
        if (mImageLoader == null) {
            mImageLoader = new ImageLoader(this.mRequestQueue,
                    new LruBitmapCache());
        }
        return this.mImageLoader;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    //TODO Google Analyics methods
    /*
    public void trackException(Exception e) {
        if(e != null){
            Tracker t = getGoogleAnalyticsTracker();

            t.send(new HitBuilders.ExceptionBuilder()
                            .setDescription(
                                    new StandardExceptionParser(this, null)
                                            .getDescription(Thread.currentThread().getName(), e))
                            .setFatal(false)
                            .build()
            );
        }
    }

    public void trackScreenView(String screenName) {
        Tracker t = getGoogleAnalyticsTracker();

        // Set screen name.
        t.setScreenName(screenName);

        // Send a screen view.
        t.send(new HitBuilders.ScreenViewBuilder().build());

        GoogleAnalytics.getInstance(this).dispatchLocalHits();
    }

    public void trackEvent(String category, String action, String label) {
        Tracker t = getGoogleAnalyticsTracker();

        // Build and send an Event.
        t.send(new HitBuilders.EventBuilder().setCategory(category).setAction(action).setLabel(label).build());
    }
    */
}
