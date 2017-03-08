package one.thebox.android.app;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.core.CrashlyticsCore;
import com.facebook.stetho.Stetho;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.freshdesk.hotline.Hotline;
import com.freshdesk.hotline.HotlineConfig;
import com.uphyca.stetho_realm.RealmInspectorModulesProvider;

import io.branch.referral.Branch;
import io.fabric.sdk.android.Fabric;

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
import one.thebox.android.R;
import one.thebox.android.ViewHelper.FontsOverride;
import one.thebox.android.api.APIService;
import one.thebox.android.api.RestClient;
import retrofit2.Retrofit;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by harsh on 10/12/15.
 */
@ReportsCrashes(buildConfigClass = TheBox.class)
public class TheBox extends MultiDexApplication {

    private static final int READ_TIMEOUT = 60 * 1000;
    private static final int CONNECTION_TIMEOUT = 60 * 1000;
    private static TheBox theBox;
    private static RestClient restClient;
    private static Retrofit retrofit;
    private static OkHttpClient okHttpClient;
    private static Realm realm;
    private static Context mContext;
    private static RealmConfiguration realmConfiguration;
    public final String TAG = TheBox.class.getSimpleName();

    private static RestClient getRestClient() {
        if (restClient == null) {
            restClient = new RestClient();
            retrofit = restClient.getRetrofit();
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
        TheBox.realm = realm;
    }

    public static RealmConfiguration getRealmConfiguration() {
        realmConfiguration = new RealmConfiguration.Builder(getInstance()).name("thebox.realm")
                .deleteRealmIfMigrationNeeded().schemaVersion(8).build();
        return realmConfiguration;
    }

    public static synchronized TheBox getInstance() {
        return theBox;
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
                    }).addNetworkInterceptor(new StethoInterceptor()).build();

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
        try {
            theBox = this;
            mContext = getApplicationContext();

            FontsOverride.setDefaultFont(this, "MONOSPACE", "fonts/Montserrat-Regular.otf");

            /*Local database*/
            getRealm();
            RealmChangeManager.getInstance();

            /*Branch.io*/
            Branch.getAutoInstance(this);

            /*debuger tools*/
            if (BuildConfig.enableStetho) {
                Stetho.initialize(
                        Stetho.newInitializerBuilder(this)
                                .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                                .enableWebKitInspector(RealmInspectorModulesProvider.builder(this).build())
                                .build());
            }

             /* initialize Calligraphy*/
            CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                    .setDefaultFontPath("fonts/Montserrat-Bold.ttf")
                    .setFontAttrId(R.attr.fontPath)
                    .build());

            /*Crash report analytics tool*/
            if (BuildConfig.enableCrashlytics == true) {
                Crashlytics crashlytics = new Crashlytics.Builder()
                        .core(new CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build())
                        .build();
                Fabric.with(this, crashlytics);
            }

            /*hotline*/
            HotlineConfig hlConfig = new HotlineConfig("28239649-48c6-4d9c-89e8-f69b6b67e22c", "e183d3ec-b70b-4833-8ff1-ad93f4b017da");
            hlConfig.setVoiceMessagingEnabled(true);
            hlConfig.setCameraCaptureEnabled(true);
            hlConfig.setPictureMessagingEnabled(true);
            Hotline.getInstance(getApplicationContext()).init(hlConfig);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public static Context getAppContext() {
        return mContext;
    }

    public static Retrofit getRetrofit() {
        return retrofit;
    }

    public static void setRetrofit(Retrofit retrofit) {
        TheBox.retrofit = retrofit;
    }
}
