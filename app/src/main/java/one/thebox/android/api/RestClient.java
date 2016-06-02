package one.thebox.android.api;


import one.thebox.android.app.MyApplication;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestClient {

    private static final boolean IS_LOCAL_URL = false;
    private static final String BASE_URL = "https://theboxprod.herokuapp.com/";
    private static final String LOCAL_URL = "http://50ef0c1e.ngrok.io/";
    private APIService apiService;

    public RestClient() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(IS_LOCAL_URL?LOCAL_URL:BASE_URL)
                .client(MyApplication.getOkHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(APIService.class);

    }

    public APIService getApiService(){
        return apiService;
    }
}
