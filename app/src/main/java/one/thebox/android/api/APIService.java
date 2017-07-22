package one.thebox.android.api;

import java.util.HashMap;
import java.util.Objects;

import one.thebox.android.api.RequestBodies.address.AddressRequest;
import one.thebox.android.api.RequestBodies.authentication.ResendOtpRequestBody;
import one.thebox.android.api.RequestBodies.authentication.SmsOtpRequestBody;
import one.thebox.android.api.RequestBodies.authentication.VerifyOtpRequestBody;
import one.thebox.android.api.RequestBodies.cart.CartItemRequest;
import one.thebox.android.api.RequestBodies.cart.PaymentSummaryRequest;
import one.thebox.android.api.RequestBodies.order.RescheduleOrderRequest;
import one.thebox.android.api.RequestBodies.order.UpdateQuantityOrderItemRequest;
import one.thebox.android.api.RequestBodies.payment.MakePaymentRequest;
import one.thebox.android.api.RequestBodies.subscribeitem.UpdateItemConfigSubscribeItemRequest;
import one.thebox.android.api.RequestBodies.subscribeitem.UpdateQuantitySubscribeItemRequest;
import one.thebox.android.api.RequestBodies.user.UpdateUserInforRequest;
import one.thebox.android.api.Responses.LocalityResponse;
import one.thebox.android.api.Responses.RescheduleResponse;
import one.thebox.android.api.Responses.CarouselApiResponse;
import one.thebox.android.api.Responses.TimeSlotResponse;
import one.thebox.android.api.RequestBodies.CreateUserRequestBody;
import one.thebox.android.api.RequestBodies.MergeSubscriptionRequest;
import one.thebox.android.api.RequestBodies.authentication.OtpRequestBody;
import one.thebox.android.api.RequestBodies.RegistrationIdRequestBody;
import one.thebox.android.api.RequestBodies.StoreUserInfoRequestBody;
import one.thebox.android.api.Responses.MergeSubscriptionResponse;
import one.thebox.android.api.Responses.search.SearchAutoCompleteResponse;
import one.thebox.android.api.Responses.UserSignInSignUpResponse;
import one.thebox.android.api.Responses.address.AddressResponse;
import one.thebox.android.api.Responses.authentication.LogoutResponse;
import one.thebox.android.api.Responses.authentication.RequestOtpResponse;
import one.thebox.android.api.Responses.authentication.ResendOtpResponse;
import one.thebox.android.api.Responses.authentication.VerifyOtpResponse;
import one.thebox.android.api.Responses.boxes.BoxCategoryResponse;
import one.thebox.android.api.Responses.boxes.BoxResponse;
import one.thebox.android.api.Responses.boxes.SubscriptionResponse;
import one.thebox.android.api.Responses.cart.CartItemResponse;
import one.thebox.android.api.Responses.cart.PaymentSummaryResponse;
import one.thebox.android.api.Responses.category.BoxCategoryItemResponse;
import one.thebox.android.api.Responses.order.OrderItemResponse;
import one.thebox.android.api.Responses.order.OrdersResponse;
import one.thebox.android.api.Responses.order.RescheduleOrderResponse;
import one.thebox.android.api.Responses.order.UpdateQuantityOrderItemResponse;
import one.thebox.android.api.Responses.payment.MakePaymentResponse;
import one.thebox.android.api.Responses.setting.SettingsResponse;
import one.thebox.android.api.Responses.subscribeitem.SavingsResponse;
import one.thebox.android.api.Responses.subscribeitem.UpdateItemConfigSubscribeItemResponse;
import one.thebox.android.api.Responses.subscribeitem.UpdateQuantitySubscribeItemResponse;
import one.thebox.android.api.Responses.user.DeviceTokenResponse;
import one.thebox.android.api.Responses.user.UpdateUserInfoResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface APIService {


    /**
     * AUTHENTICATION
     * <p>
     * Request OTP; SMS Authentication with Phone number
     */
    @POST("/consumer/api/v1/user-sms-auth")
    Call<RequestOtpResponse> requestOtpAuth(@Body SmsOtpRequestBody smsOtpRequestBody);

    /**
     * Verify Otp Authentication
     */
    @POST("/consumer/api/v1/verf-session")
    Call<VerifyOtpResponse> verifyOtpAuth(@Body VerifyOtpRequestBody verifyOtpRequestBody);

    /**
     * Request Resend Otp
     */
    @POST("/consumer/api/v1/resend-verf")
    Call<ResendOtpResponse> resendOtpAuth(@Body ResendOtpRequestBody resendOtpRequestBody);

    /**
     * Signout
     */
    @GET("consumer/api/v1/users/logout")
    Call<LogoutResponse> logOut(@Header("Authorization") String accessToken);

    /**
     * NOTIFICATION
     * Update GCM Token
     */
    @POST("consumer/api/v1/users/device")
    Call<DeviceTokenResponse> storeGcmRegistrationToken(@Header("Authorization") String accessToken, @Body RegistrationIdRequestBody registrationIdRequestBody);

    /**
     * User Setting API
     */
    @GET("/consumer/api/v1/settings")
    Call<SettingsResponse> getSettings(@Header("Authorization") String accessToken, @Query("app_version") int appVersionCode);

    /**
     * Users Update; Get User Information
     */
    @POST("/consumer/api/v1/users/update")
    Call<UpdateUserInfoResponse> updateUserInfo(@Header("Authorization") String accessToken, @Body UpdateUserInforRequest updateUserInforRequest);

    /**
     * USER ADDRESS
     * <p>
     * Locality
     */
    @GET("/public/localities")
    Call<LocalityResponse> getLocality(@Header("Authorization") String accessToken);

    /**
     * Create New Address
     */
    @POST("/consumer/api/v1/addresses/create")
    Call<AddressResponse> createAddress(@Header("Authorization") String accessToken, @Body AddressRequest addressRequest);

    /**
     * Update Address
     */
    @POST("/consumer/api/v1/addresses/{uuid}/update")
    Call<AddressResponse> updateAddress(@Header("Authorization") String accessToken,
                                        @Path("uuid") String addressUuid, @Body AddressRequest addressRequest);


    /**
     * BOX
     * <p>
     * Get all Boxes
     */
    @GET("/consumer/api/v1/boxes")
    Call<BoxResponse> getBoxes(@Header("Authorization") String accessToken);

    /**
     * Get all items for the category
     */
    @GET("/consumer/api/v1/categories")
    Call<BoxCategoryItemResponse> getCategoryItem(@Header("Authorization") String accessToken,
                                                  @Query("uuid") String uuid, @Query("query") String searchQuery,
                                                  @Query("page") int pageNumber, @Query("per_page") int perPage);

    /**
     * Get All Category for Box UUID
     */
    @GET("consumer/api/v1/boxes/{box_uuid}")
    Call<BoxCategoryResponse> getCategories(@Header("Authorization") String accessToken, @Path("box_uuid") String boxUuid);

    /**
     * Sync CartProduct with Server
     */
    @POST("/consumer/api/v1/carts")
    Call<CartItemResponse> syncCart(@Header("Authorization") String accessToken, @Body CartItemRequest cartItemRequest);


    /**
     * TIME SLOTS
     * <p>
     * Available Time Slot General
     */
    @GET("/consumer/api/v1/slots-available")
    Call<TimeSlotResponse> getTimeSlots(@Header("Authorization") String accessToken);

    /**
     * Merge Time Slot
     */
    @GET("/consumer/api/v1/orders/merged")
    Call<TimeSlotResponse> getMergeTimeSlot(@Header("Authorization") String accessToken);

    /**
     * Time Slot for a Order
     */
    @GET("consumer/api/v1/orders/{order_uuid}/slots")
    Call<TimeSlotResponse> getTimeSlotForOrder(@Header("Authorization") String accessToken,
                                               @Path("order_uuid") String orderUuid);

    /**
     * Reschedule Timeslot API
     */
    @GET("consumer/api/v1/slots-available")
    Call<TimeSlotResponse> getRescheduleTimeSlot(@Header("Authorization") String accessToken,
                                                 @Query("order_uuid") String orderUuid, @Query("type") String type);

    /**
     * Payment Details or Summary
     * <p>
     * First Order APi
     */
    @POST("/consumer/api/v1/orders/summary")
    Call<PaymentSummaryResponse> getPaymentSummaryForCart(@Header("Authorization") String accessToken,
                                                          @Query("cart") boolean isCart, @Body PaymentSummaryRequest paymentSummaryRequest);

    /**
     * Payment Summary for Merge Deliverires
     */
    @POST("/consumer/api/v1/orders/{order_uuid}/summary")
    Call<PaymentSummaryResponse> getPaymentSummaryForMergeDeliveries(@Header("Authorization") String accessToken,
                                                                     @Path("order_uuid") String orderUuid, @Body PaymentSummaryRequest paymentSummaryRequest);

    /**
     * PAYMENTS
     * Make Payment
     */
    @POST("/consumer/api/v1/orders/payment")
    Call<MakePaymentResponse> makePayment(@Header("Authorization") String accessToken,
                                          @Body MakePaymentRequest makePaymentRequest);

    /**
     * SUBSCRIPTION
     * Subscribe Item (formally UserItem)
     */
    @GET("/consumer/api/v1/users/subscriptions")
    Call<SubscriptionResponse> getSubscription(@Header("Authorization") String accessToken);

    /**
     * Reschdule Subscribe Item
     */
    @GET("/consumer/api/v1/subscriptions/{subscribe_item_uuid}/reschedule")
    Call<RescheduleResponse> getRescheduleOption(@Header("Authorization") String accessToken, @Path("subscribe_item_uuid") String subscribeItemUuid);

    /**
     * Merge Subscribe Item With Another Order
     */
    @POST("/consumer/api/v1/subscriptions/{subscribe_item_uuid}/reschedule")
    Call<MergeSubscriptionResponse> mergeSubscribeItemWithOrder(@Header("Authorization") String accessToken,
                                                                @Path("subscribe_item_uuid") String subscribeItemUuid, @Body MergeSubscriptionRequest mergeSubscriptionRequest);

    /**
     * Update Subscribe Item Quantity
     */
    @POST("/consumer/api/v1/subscriptions/{subscribe_item_uuid}/update")
    Call<UpdateQuantitySubscribeItemResponse> updateQuantitySubscribeItem(@Header("Authorization") String accessToken,
                                                                          @Path("subscribe_item_uuid") String subscribeItemUuid, @Body UpdateQuantitySubscribeItemRequest updateQuantitySubscribeItemRequest);

    /**
     * Update Subscribe Item ItemConfig
     */
    @POST("consumer/api/v1/subscriptions/{subscribe_item_uuid}/itemconfig")
    Call<UpdateItemConfigSubscribeItemResponse> updateItemConfigSubscribeItem(@Header("Authorization") String accessToken,
                                                                              @Path("subscribe_item_uuid") String subscribeItemUuid, @Body UpdateItemConfigSubscribeItemRequest updateItemConfigSubscribeItemRequest);

    /**
     * SAVINGS
     * Savings on Subscribe Item
     */
    @GET("consumer/api/v1/users/savings")
    Call<SavingsResponse> getSavings(@Header("Authorization") String accessToken);


    /**
     * ORDERS
     * Orders
     */
    @GET("/consumer/api/v1/orders")
    Call<OrdersResponse> getOrders(@Header("Authorization") String accessToken,@QueryMap HashMap<String, Object> params);

    /**
     * Ordered Items
     */
    @GET("/consumer/api/v1/orders/{order_uuid}/invoices")
    Call<OrderItemResponse> getOrderItems(@Header("Authorization") String accessToken, @Path("order_uuid") String orderUuid);

    /**
     * Order Item Quantity Update
     */
    @POST("consumer/api/v1/invoices/{order_item_uuid}/update")
    Call<UpdateQuantityOrderItemResponse> updateQuantityOrderItem(@Header("Authorization") String accessToken,
                                                                  @Path("order_item_uuid") String orderItemUuid, @Body UpdateQuantityOrderItemRequest updateQuantityOrderItemRequest);


    /**
     * Reschedule order
     */
    @POST("/consumer/api/v1/orders/{order_uuid}/reschedule")
    Call<RescheduleOrderResponse> rescheduleOrder(@Header("Authorization") String accessToken,
                                                  @Path("order_uuid") String orderUuid, @Body RescheduleOrderRequest rescheduleOrderRequest);


    /**
     * Carousel API service
     */
    @GET("/public/app-offers")
    Call<CarouselApiResponse> getCarousel(@Header("Authorization") String accessToken);


    /**
     * Search Products
     */
    @GET("/consumer/api/v1/categories/autocomplete")
    Call<SearchAutoCompleteResponse> searchAutoComplete(@Header("Authorization") String accessToken,
                                                        @Query("q") String query);


    /**
     * Refator
     */


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


    @POST("/sign_up/store_user_details")
    Call<UserSignInSignUpResponse>
    storeUserInfo(@Header("authtoken") String authToken,
                  @Body StoreUserInfoRequestBody storeUserInfoRequestBody);


    @POST("/updatemyprofile")
    Call<UserSignInSignUpResponse> updateProfile(@Header("authtoken") String authtoken,
                                                 @Body StoreUserInfoRequestBody storeUserInfoRequestBody);


}
