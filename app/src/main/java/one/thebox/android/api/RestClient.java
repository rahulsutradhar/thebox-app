package one.thebox.android.api;


import one.thebox.android.BuildConfig;
import one.thebox.android.app.TheBox;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestClient {

    private static final String STAGING_URL_AWS = "http://staging.thebox.one/";
    private static final String PROD_URL_AWS = "http://api.thebox.one/";

    public static final boolean is_in_development = true;
    private APIService apiService;
    private Retrofit retrofit;

    public RestClient() {

        retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.DEBUG ? STAGING_URL_AWS : PROD_URL_AWS)
                .client(TheBox.getOkHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(APIService.class);
    }

    public APIService getApiService() {
        return apiService;
    }

    public boolean Is_in_Development() {
        return is_in_development;
    }

    public Retrofit getRetrofit() {
        return retrofit;
    }

    public void setRetrofit(Retrofit retrofit) {
        this.retrofit = retrofit;
    }
}
