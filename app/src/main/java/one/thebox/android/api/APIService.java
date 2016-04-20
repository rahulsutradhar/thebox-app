package one.thebox.android.api;


import one.thebox.android.api.RequestBodies.CreateUserRequestBody;
import one.thebox.android.api.RequestBodies.OtpRequestBody;
import one.thebox.android.api.RequestBodies.SignUpRequestBody;
import one.thebox.android.api.Responses.UserSignInSignUpResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

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
            @Body  CreateUserRequestBody createUserRequestBody
    );

    @POST("/users")
    Call<ApiResponse> test(@Header("authtoken") String authToken);
}
