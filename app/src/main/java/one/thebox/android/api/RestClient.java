package one.thebox.android.api;


import one.thebox.android.BuildConfig;
import one.thebox.android.app.MyApplication;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestClient {

    private static final String LOCAL_URL = "http://3cb3f615.ngrok.io";
    private static final String STAGING_URL_HEROKU = "https://tranquil-springs-65978.herokuapp.com/";
    private static final String STAGING_URL_AWS = "http://staging.thebox.one/";
    private static final String PROD_URL_AWS = "http://api.thebox.one/";
    private static final String PROD_URL_HEROKU = "https://theboxprod.herokuapp.com/";

    public static final boolean is_in_development = true;
    private APIService apiService;

    public RestClient() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(STAGING_URL_AWS)
                .client(MyApplication.getOkHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(APIService.class);
    }

    public APIService getApiService() {
        return apiService;
    }

    public boolean Is_in_Development() {return is_in_development;}
}
