package one.thebox.android.app;

import android.app.Application;
import android.content.Context;

//import com.squareup.leakcanary.LeakCanary;
import com.crashlytics.android.Crashlytics;
import com.facebook.stetho.Stetho;
import com.freshdesk.hotline.Hotline;
import com.freshdesk.hotline.HotlineConfig;
import com.squareup.okhttp.Cache;
import com.squareup.picasso.LruCache;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;
import com.uphyca.stetho_realm.RealmInspectorModulesProvider;

import io.fabric.sdk.android.Fabric;

import org.acra.ACRA;
import org.acra.ErrorReporter;
import org.acra.annotation.ReportsCrashes;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import one.thebox.android.BuildConfig;
import one.thebox.android.Helpers.RealmChangeManager;
import one.thebox.android.ViewHelper.FontsOverride;
import one.thebox.android.api.APIService;
import one.thebox.android.api.RestClient;
import one.thebox.android.util.HockeySenderHelper;

/**
 * Created by harsh on 10/12/15.
 */
@ReportsCrashes(buildConfigClass = MyApplication.class)
public class MyApplication extends Application {

    private static final int READ_TIMEOUT = 60 * 1000;
    private static final int CONNECTION_TIMEOUT = 60 * 1000;
    private static MyApplication myApplication;
    private static RestClient restClient;
    private static OkHttpClient okHttpClient;
    private static Realm realm;
    private static Context mContext;
    private static RealmConfiguration realmConfiguration;
    public final String TAG = MyApplication.class.getSimpleName();

    private static RestClient getRestClient() {
        if (restClient == null) {
            restClient = new RestClient();
        }
        return restClient;
    }

    public static Realm getRealm() {
        if (realm == null) {
            realm = Realm.getInstance(getRealmConfiguration());
            return realm;
        }
        return realm;
    }

    public static void setRealm(Realm realm) {
        MyApplication.realm = realm;
    }

    public static RealmConfiguration getRealmConfiguration() {
        realmConfiguration = new RealmConfiguration.Builder(getInstance()).name("thebox.realm")
                .deleteRealmIfMigrationNeeded().schemaVersion(6).build();
        return realmConfiguration;
    }

    public static synchronized MyApplication getInstance() {
        return myApplication;
    }

    public static OkHttpClient getOkHttpClient() {
        if (okHttpClient == null) {
            HttpLoggingInterceptor logger = new HttpLoggingInterceptor();
            logger.setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE);
            okHttpClient = new OkHttpClient.Builder().cache(getCache(4)).retryOnConnectionFailure(true).readTimeout(READ_TIMEOUT, TimeUnit.MILLISECONDS)
                    .connectTimeout(CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS).addInterceptor(logger).addInterceptor(new Interceptor() {
                        @Override
                        public okhttp3.Response intercept(Chain chain) throws IOException {
                            okhttp3.Request request = chain.request().newBuilder()
                                    .addHeader("Accept", "application/json")
                                    .addHeader("Content-type", "application/json").build();
                            return chain.proceed(request);
                        }
                    }).build();

        }
        return okHttpClient;
    }

    private static okhttp3.Cache getCache(int size_in_mb) {
        int cacheSize = size_in_mb * 1024 * 1024; //10MB
        okhttp3.Cache cache = new okhttp3.Cache(mContext.getCacheDir(), cacheSize);
        return cache;
    }

    public static APIService getAPIService() {
        return getRestClient().getApiService();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        myApplication = this;
        mContext = getApplicationContext();

        FontsOverride.setDefaultFont(this, "MONOSPACE", "fonts/Montserrat-Regular.otf");

        getRealm();

        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(RealmInspectorModulesProvider.builder(this).build())
                        .build());


        RealmChangeManager.getInstance();

        HotlineConfig hlConfig=new HotlineConfig("28239649-48c6-4d9c-89e8-f69b6b67e22c","e183d3ec-b70b-4833-8ff1-ad93f4b017da");
        hlConfig.setVoiceMessagingEnabled(true);
        hlConfig.setCameraCaptureEnabled(true);
        hlConfig.setPictureMessagingEnabled(true);
        Hotline.getInstance(getApplicationContext()).init(hlConfig);

    }


    public static Context getAppContext() {
        return mContext;
    }
}
