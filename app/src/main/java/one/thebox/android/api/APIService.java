package one.thebox.android.api;


import java.util.Map;

import one.thebox.android.api.RequestBodies.address.AddressRequest;
import one.thebox.android.api.RequestBodies.authentication.ResendOtpRequestBody;
import one.thebox.android.api.RequestBodies.authentication.SmsOtpRequestBody;
import one.thebox.android.api.RequestBodies.authentication.VerifyOtpRequestBody;
import one.thebox.android.api.RequestBodies.cart.CartItemRequest;
import one.thebox.android.api.RequestBodies.cart.PaymentSummaryRequest;
import one.thebox.android.api.RequestBodies.order.UpdateQuantityOrderItemRequest;
import one.thebox.android.api.RequestBodies.payment.MakePaymentCodCartRequest;
import one.thebox.android.api.RequestBodies.payment.MakePaymentCodMergeRequest;
import one.thebox.android.api.RequestBodies.payment.MakePaymentOnlineCartRequest;
import one.thebox.android.api.RequestBodies.payment.MakePaymentOnlineMergeRequest;
import one.thebox.android.api.RequestBodies.subscribeitem.UpdateQuantitySubscribeItemRequest;
import one.thebox.android.api.RequestBodies.user.UpdateUserInforRequest;
import one.thebox.android.api.Responses.LocalityResponse;
import one.thebox.android.api.Responses.PaymentDetailsResponse;
import one.thebox.android.api.Responses.RescheduleResponse;
import one.thebox.android.api.Responses.CarouselApiResponse;
import one.thebox.android.api.Responses.TimeSlotResponse;
import one.thebox.android.api.Responses.UserItemResponse;
import one.thebox.android.api.RequestBodies.AddAddressRequestBody;
import one.thebox.android.api.RequestBodies.AddToMyBoxRequestBody;
import one.thebox.android.api.RequestBodies.CancelSubscriptionRequest;
import one.thebox.android.api.RequestBodies.CreateUserRequestBody;
import one.thebox.android.api.RequestBodies.MergeCartToOrderRequestBody;
import one.thebox.android.api.RequestBodies.MergeSubscriptionRequest;
import one.thebox.android.api.RequestBodies.OnlinePaymentRequest;
import one.thebox.android.api.RequestBodies.authentication.OtpRequestBody;
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
import one.thebox.android.api.Responses.address.AddressResponse;
import one.thebox.android.api.Responses.authentication.RequestOtpResponse;
import one.thebox.android.api.Responses.authentication.ResendOtpResponse;
import one.thebox.android.api.Responses.authentication.VerifyOtpResponse;
import one.thebox.android.api.Responses.boxes.BoxResponse;
import one.thebox.android.api.Responses.boxes.SubscriptionResponse;
import one.thebox.android.api.Responses.cart.CartItemResponse;
import one.thebox.android.api.Responses.cart.PaymentSummaryResponse;
import one.thebox.android.api.Responses.category.BoxCategoryItemResponse;
import one.thebox.android.api.Responses.order.OrderItemResponse;
import one.thebox.android.api.Responses.order.OrdersResponse;
import one.thebox.android.api.Responses.order.UpdateQuantityOrderItemResponse;
import one.thebox.android.api.Responses.payment.MakePaymentResponse;
import one.thebox.android.api.Responses.setting.SettingsResponse;
import one.thebox.android.api.Responses.subscribeitem.UpdateQuantitySubscribeItemResponse;
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
     * Refactoring API
     */

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
     * User Address
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
    Call<BoxCategoryItemResponse> getCategoryItem(@Header("Authorization") String accessToken, @Query("uuid") String uuid);


    /**
     * Sync Cart with Server
     */
    @POST("/consumer/api/v1/carts")
    Call<CartItemResponse> syncCart(@Header("Authorization") String accessToken, @Body CartItemRequest cartItemRequest);


    /**
     * Time Slot
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
     * Make Payment
     * <p>
     * COD - Cart
     */
    @POST("/consumer/api/v1/orders/payment")
    Call<MakePaymentResponse> makePaymentCodCart(@Header("Authorization") String accessToken,
                                                 @Body MakePaymentCodCartRequest makePaymentCodCartRequest);

    /**
     * Make Payment
     * <p>
     * COD - Merge
     */
    @POST("/consumer/api/v1/orders/payment")
    Call<MakePaymentResponse> makePaymentCodMerge(@Header("Authorization") String accessToken,
                                                  @Body MakePaymentCodMergeRequest makePaymentCodMergeRequest);

    /**
     * Make Payment
     * <p>
     * Online - Cart
     */
    @POST("/consumer/api/v1/orders/payment")
    Call<MakePaymentResponse> makePaymentOnlineCart(@Header("Authorization") String accessToken,
                                                    @Body MakePaymentOnlineCartRequest makePaymentOnlineCartRequest);

    /**
     * Make Payment
     * <p>
     * Online - Merge
     */
    @POST("/consumer/api/v1/orders/payment")
    Call<MakePaymentResponse> makePaymentOnlineMerge(@Header("Authorization") String accessToken,
                                                     @Body MakePaymentOnlineMergeRequest makePaymentOnlineMergeRequest);


    /**
     * Subscription; Subscribe Item (formally UserItem)
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
     * Orders
     */
    @GET("/consumer/api/v1/orders")
    Call<OrdersResponse> getOrders(@Header("Authorization") String accessToken);

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

    @POST("/users")
    Call<ApiResponse> test(@Header("authtoken") String authToken);

    @POST("/sign_up/store_user_details")
    Call<UserSignInSignUpResponse>
    storeUserInfo(@Header("authtoken") String authToken,
                  @Body StoreUserInfoRequestBody storeUserInfoRequestBody);

    @GET("/get_all_localities")
    Call<LocalitiesResponse> getAllLocalities(@Header("authtoken") String authToken, @Query("query") String query);

   /* @GET("/users/setting")
    Call<SettingsResponse> getSettings(@Header("authtoken") String authToken, @Query("app_version") String appVersion);
*/

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

    @GET("/items")
    Call<CategoryBoxItemsResponse> getItems(@Header("authtoken") String authToken,
                                            @Query("id") int id, @Query("page") int pageNumber, @Query("per_page") int perPage);

    @GET("/explore_box")
    Call<ExploreBoxResponse> getExploreBox(@Header("authtoken") String authToken,
                                           @Query("id") int id);

    @GET("/gogetmybox")
    Call<MyBoxResponse> getMyBoxes(@Header("authtoken") String authToken);

    @GET("/gogetmyuseritems")
    Call<UserItemResponse> getMyItems(@Header("authtoken") String authToken);

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


    /**
     * Carousel API service
     */
    @GET("/public/app-offers")
    Call<CarouselApiResponse> getCarousel();


    /**
     * Time Slot for a Order
     */
    @GET("/consumer/api/v1/order-slots")
    Call<TimeSlotResponse> getTimeSlotForOrder(@Query("order_id") int orderId);

    /**
     * Reschedule API
     */
    @GET("/consumer/api/v1/slots-available")
    Call<TimeSlotResponse> getRescheduleTimeSlot(@Query("order_id") int orderId, @Query("type") String type);


    /**
     * Get Payment Details
     */
    @GET("/consumer/api/v1/orders/summary")
    Call<PaymentDetailsResponse> getPaymentDetailsData(@QueryMap Map<String, Integer> params);


}
