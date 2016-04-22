package one.thebox.android.api;


import com.google.android.gms.common.api.Api;

import one.thebox.android.api.RequestBodies.CreateUserRequestBody;
import one.thebox.android.api.RequestBodies.OtpRequestBody;
import one.thebox.android.api.RequestBodies.SignUpRequestBody;
import one.thebox.android.api.RequestBodies.StoreUserInfoRequestBody;
import one.thebox.android.api.Responses.ExploreItemResponse;
import one.thebox.android.api.Responses.LocalitiesResponse;
import one.thebox.android.api.Responses.SearchAutoCompleteResponse;
import one.thebox.android.api.Responses.UserSignInSignUpResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface APIService {

    @POST("/users")
    Call<UserSignInSignUpResponse> createNewUser(
            @Body CreateUserRequestBody createUserRequestBody
    );

    @POST("/sign_up/verify_otp")
    Call<UserSignInSignUpResponse> verifyOtp(
            @Body OtpRequestBody otpRequestBody
    );

    @POST("/sign_up/store_user_details")
    Call<ApiResponse> signUpUser(
            @Body SignUpRequestBody signUpRequestBody
    );

    @POST("/users/sign_in")
    Call<UserSignInSignUpResponse> signIn(
            @Body CreateUserRequestBody createUserRequestBody
    );

    @POST("/users")
    Call<ApiResponse> test(@Header("authtoken") String authToken);

    @POST("/sign_up/store_user_details")
    Call<UserSignInSignUpResponse>
    storeUserInfo(@Header("authtoken") String authToken,
                  @Body StoreUserInfoRequestBody storeUserInfoRequestBody);

    @GET("/get_all_localities")
    Call<LocalitiesResponse> getAllLocalities(
            @Header("authtoken") String authToken,
            @Query("query") String query
    );

    @GET("/autocomplete")
    Call<SearchAutoCompleteResponse> searchAutoComplete(@Header("authtoken") String authToken,
                                                        @Query("query") String query);

    @GET("/get_all_boxes")
    Call<ExploreItemResponse> getAllExploreBoxes(@Header("authtoken") String authToken);

    @GET("/searchresults")
    Call<ApiResponse> getSearchResults(@Header("authtoken") String authToken,
                               @Query("query") String query);

}
