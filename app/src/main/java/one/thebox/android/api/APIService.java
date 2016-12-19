package one.thebox.android.api;


import one.thebox.android.api.RequestBodies.AddAddressRequestBody;
import one.thebox.android.api.RequestBodies.AddToMyBoxRequestBody;
import one.thebox.android.api.RequestBodies.CancelSubscriptionRequest;
import one.thebox.android.api.RequestBodies.CreateUserRequestBody;
import one.thebox.android.api.RequestBodies.MergeCartToOrderRequestBody;
import one.thebox.android.api.RequestBodies.MergeSubscriptionRequest;
import one.thebox.android.api.RequestBodies.OnlinePaymentRequest;
import one.thebox.android.api.RequestBodies.OtpRequestBody;
import one.thebox.android.api.RequestBodies.PaymentRequestBody;
import one.thebox.android.api.RequestBodies.RegistrationIdRequestBody;
import one.thebox.android.api.RequestBodies.RescheduleRequestBody;
import one.thebox.android.api.RequestBodies.SearchDetailResponse;
import one.thebox.android.api.RequestBodies.StoreUserInfoRequestBody;
import one.thebox.android.api.RequestBodies.UpdateAddressRequestBody;
import one.thebox.android.api.RequestBodies.UpdateItemConfigurationRequest;
import one.thebox.android.api.RequestBodies.UpdateItemQuantityRequestBody;
import one.thebox.android.api.RequestBodies.UpdateOrderItemQuantityRequestBody;
import one.thebox.android.api.Responses.AddToMyBoxResponse;
import one.thebox.android.api.Responses.AddressesApiResponse;
import one.thebox.android.api.Responses.AdjustDeliveryResponse;
import one.thebox.android.api.Responses.CancelSubscriptionResponse;
import one.thebox.android.api.Responses.CartResponse;
import one.thebox.android.api.Responses.CategoryBoxItemsResponse;
import one.thebox.android.api.Responses.ExploreBoxResponse;
import one.thebox.android.api.Responses.ExploreItemResponse;
import one.thebox.android.api.Responses.GetAllAddressResponse;
import one.thebox.android.api.Responses.LocalitiesResponse;
import one.thebox.android.api.Responses.MergeSubscriptionResponse;
import one.thebox.android.api.Responses.MyBoxResponse;
import one.thebox.android.api.Responses.OrdersApiResponse;
import one.thebox.android.api.Responses.PaymentResponse;
import one.thebox.android.api.Responses.RescheduleResponseBody;
import one.thebox.android.api.Responses.SearchAutoCompleteResponse;
import one.thebox.android.api.Responses.UpdateItemConfigResponse;
import one.thebox.android.api.Responses.UpdateOrderItemResponse;
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

    @POST("/sign_in/verify_otp")
    Call<UserSignInSignUpResponse> verifyOtp(
            @Body OtpRequestBody otpRequestBody
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
    Call<SearchDetailResponse> getSearchResults(@Header("authtoken") String authToken,
                                                @Query("query") String query);

    @POST("/users/sign_out")
    Call<ApiResponse> signOut(@Header("authtoken") String authToken);

    @POST("/updatemyprofile")
    Call<UserSignInSignUpResponse> updateProfile(@Header("authtoken") String authtoken,
                                                 @Body StoreUserInfoRequestBody storeUserInfoRequestBody);

    @POST("/addtomybox")
    Call<AddToMyBoxResponse> addToMyBox(@Header("authtoken") String authToken,
                                        @Body AddToMyBoxRequestBody addToMyBoxRequestBody);

    @GET("/allitemsforcategory")
    Call<CategoryBoxItemsResponse> getCategoryBoxItems(@Header("authtoken") String authToken,
                                                       @Query("id") int id);

    @GET("/explore_box")
    Call<ExploreBoxResponse> getExploreBox(@Header("authtoken") String authToken,
                                           @Query("id") int id);

    @GET("/gogetmybox")
    Call<MyBoxResponse> getMyBoxes(@Header("authtoken") String authToken);

    @GET("/gogetmyuseritems")
    Call<MyBoxResponse> getMySItems(@Header("authtoken") String authToken);

    @POST("/updateitemquantity")
    Call<UpdateItemConfigResponse> updateQuantity(@Header("authtoken") String authToken,
                                                  @Body UpdateItemQuantityRequestBody updateItemQuantityRequestBody);

    @POST("/updateorderitemquantity")
    Call<UpdateOrderItemResponse> updateOrderQuantity(@Header("authtoken") String authToken,
                                                      @Body UpdateOrderItemQuantityRequestBody updateItemQuantityRequestBody);

    @POST("/updateitemconfig")
    Call<UpdateItemConfigResponse> updateItemConfig(@Header("authtoken") String authToken,
                                                    @Body UpdateItemConfigurationRequest updateItemConfigurationRequest);

    @POST("/addmyaddress")
    Call<AddressesApiResponse> addAddress(@Header("authtoken") String authToken,
                                          @Body AddAddressRequestBody addAddressRequestBody);

    @POST("/updatemyaddress")
    Call<AddressesApiResponse> updateAddress(@Header("authtoken") String authTokken,
                                             @Body UpdateAddressRequestBody updateAddressRequestBody);

    @GET("/getmyaddresses")
    Call<GetAllAddressResponse> getAllAddresses(@Header("authtoken") String authToken);

    @GET("/getmyorders")
    Call<OrdersApiResponse> getMyOrders(@Header("authtoken") String authToken);

    @POST("/payfortheseorders")
    Call<PaymentResponse> payOrders(@Header("authtoken") String authToken,
                                    @Body PaymentRequestBody paymentRequestBody);

    @POST("/capturepayment_for_order")
    Call<PaymentResponse> payOrderOnline(@Header("authtoken") String authToken,
                                    @Body OnlinePaymentRequest onlinepaymentRequestBody);

    @POST("/reschedulethisorder")
    Call<RescheduleResponseBody> reschedulethisOrder(@Header("authtoken") String authToken,
                                                     @Body RescheduleRequestBody rescheduleRequestBody);


    @POST("/cancel_subscription")
    Call<CancelSubscriptionResponse> cancelSubscription(@Header("authtoken") String authToken,
                                                        @Body CancelSubscriptionRequest cancelSubscriptionRequest);

    @POST("/delay_delivery")
    Call<CancelSubscriptionResponse> delayDeliveryByOneCycle(@Header("authtoken") String authToken,
                                                             @Body CancelSubscriptionRequest cancelSubscriptionRequest);

    @POST("/update_item_delivery_time")
    Call<MergeSubscriptionResponse> mergeUserItemWithOrder(@Header("authtoken") String authToken,
                                                           @Body MergeSubscriptionRequest mergeSubscriptionRequest);

    @GET("get_delay_delivery_options")
    Call<AdjustDeliveryResponse> getAdjustDeliveryOrders(@Header("authtoken") String authToken,
                                                         @Query("useritem[id]") int userItemId);

    @GET("getmycart")
    Call<CartResponse> getMyCart(@Header("authtoken") String authToken);

    @POST("merge_cartitems_with_order_payment_offline")
    Call<PaymentResponse> merge_cart_items_to_order_payment_offline(@Header("authtoken") String authToken, @Body MergeCartToOrderRequestBody mergeCartToOrderRequestBody);

    @POST("merge_cartitems_with_order_payment_online")
    Call<PaymentResponse> merge_cart_items_to_order_payment_online(@Header("authtoken") String authToken, @Body OnlinePaymentRequest mergeCartToOrderRequestBody);

    @POST("/devices")
    Call<ApiResponse> postRegistrationId(@Header("authtoken") String authToken, @Body RegistrationIdRequestBody registrationIdRequestBody);
}
