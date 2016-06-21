package one.thebox.android.api;


import one.thebox.android.BuildConfig;
import one.thebox.android.app.MyApplication;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestClient {

    private static final boolean IS_LOCAL_URL = false;
    private static final String STAGING_URL = "https://tranquil-springs-65978.herokuapp.com/";
    private static final String BASE_URL = "https://theboxprod.herokuapp.com/";
    private static final String LOCAL_URL = "http://f4497853.ngrok.io";
    private APIService apiService;

    public RestClient() {

        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl(BuildConfig.DEBUG ? STAGING_URL : BASE_URL)
//                .baseUrl(LOCAL_URL)
                .baseUrl(STAGING_URL)
                .client(MyApplication.getOkHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(APIService.class);

    }

    public APIService getApiService() {
        return apiService;
    }
}
