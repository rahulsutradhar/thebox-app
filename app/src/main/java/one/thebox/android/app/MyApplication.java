package one.thebox.android.app;

import android.app.Application;
import android.content.Context;

import org.acra.ACRA;
import org.acra.ErrorReporter;
import org.acra.annotation.ReportsCrashes;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import one.thebox.android.BuildConfig;
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
    private static ErrorReporter errorReporterSingleton;
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

    public static RealmConfiguration getRealmConfiguration() {
        if (realmConfiguration == null) {
            realmConfiguration = new RealmConfiguration.Builder(getInstance())
                    .deleteRealmIfMigrationNeeded().schemaVersion(1).build();
            return realmConfiguration;
        }
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

    private static Cache getCache(int size_in_mb) {
        int cacheSize = size_in_mb * 1024 * 1024; //10MB
        Cache cache = new Cache(mContext.getCacheDir(), cacheSize);
        return cache;
    }

    public static APIService getAPIService() {
        return getRestClient().getApiService();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        myApplication = this;
        mContext = getApplicationContext();
        ACRA.init(this);
        ACRA.getErrorReporter().setReportSender(new HockeySenderHelper());
    }
}
