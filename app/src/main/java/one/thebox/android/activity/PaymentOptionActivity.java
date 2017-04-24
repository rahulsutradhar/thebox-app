package one.thebox.android.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.razorpay.Checkout;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.RealmList;
import one.thebox.android.BuildConfig;
import one.thebox.android.Helpers.CartHelper;
import one.thebox.android.Helpers.OrderHelper;
import one.thebox.android.Models.AddressAndOrder;
import one.thebox.android.Models.Category;
import one.thebox.android.Models.Order;
import one.thebox.android.Models.User;
import one.thebox.android.R;
import one.thebox.android.R2;
import one.thebox.android.ViewHelper.BoxLoader;
import one.thebox.android.ViewHelper.ViewPagerAdapter;
import one.thebox.android.api.RequestBodies.MergeCartToOrderRequestBody;
import one.thebox.android.api.RequestBodies.OnlinePaymentRequest;
import one.thebox.android.api.RequestBodies.PaymentRequestBody;
import one.thebox.android.api.Responses.PaymentResponse;
import one.thebox.android.app.Keys;
import one.thebox.android.app.TheBox;
import one.thebox.android.fragment.PaymentSelectorFragment;
import one.thebox.android.util.AppUtil;
import one.thebox.android.util.Constants;
import one.thebox.android.util.CoreGsonUtils;
import one.thebox.android.util.FusedLocationService;
import one.thebox.android.util.PrefUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Ruchit on 9/26/2016.
 */
public class PaymentOptionActivity extends AppCompatActivity {

    private static final String EXTRA_ARRAY_LIST_ORDER = "array_list_order";
    private static final String EXTRA_MERGE_ORDER_ID = "merge_order_id";
    private static final String EXTRA_TOTAL_CART_AMOUNT = "total_cart_amount";
    private static final String EXTRA_IS_MERGING = "is_merging_order";
    private static final int REQ_CODE_GET_LOCATION = 101;
    private ArrayList<AddressAndOrder> addressAndOrders;
    private boolean isMerging;
    private String latitude = "0.0", longitude = "0.0";
    private int locationPermisionCounter = 0;

    @BindView(R2.id.tabsPaymentOption)
    TabLayout tabsPaymentOption;
    @BindView(R2.id.viewPagerPaymentOption)
    ViewPager viewPagerPaymentOption;
    @BindView(R2.id.txtTotalAmount)
    TextView txtTotalAmount;

    @BindView(R2.id.txtPlaceOrder)
    TextView txtPlaceOrder;
    @BindView(R2.id.imgPaymentBack)
    ImageView imgPaymentBack;


    int POSITION_OF_VIEW_PAGER;
    String totalPayment = "";
    private User user;
    private int mergeOrderId;
    private boolean locationRefreshed;
    private FusedLocationService.MyLocation latLng = new FusedLocationService.MyLocation("0.0", "0.0");
    private String razorpayPaymentID;
    private int cleverTapOrderId;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_options);
        ButterKnife.bind(this);
        if (getIntent().hasExtra(EXTRA_TOTAL_CART_AMOUNT)) {
            totalPayment = getIntent().getStringExtra(EXTRA_TOTAL_CART_AMOUNT);
            txtTotalAmount.setText(fmt(Double.parseDouble(totalPayment)));
        }
        user = PrefUtils.getUser(this);
        initVariables();
        setupViewPagerAndTabsMyBox();
        imgPaymentBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


    }

    private void initVariables() {
        String ordersString = getIntent().getStringExtra(EXTRA_ARRAY_LIST_ORDER);
        addressAndOrders = CoreGsonUtils.fromJsontoArrayList(ordersString, AddressAndOrder.class);
        mergeOrderId = getIntent().getIntExtra(EXTRA_MERGE_ORDER_ID, 0);
        isMerging = getIntent().getBooleanExtra(EXTRA_IS_MERGING, false);
    }


    private void setupViewPagerAndTabsMyBox() {

        final ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(), PaymentOptionActivity.this);


        Category category = new Category();
        category.setTitle("CARD");
        adapter.addFragment(PaymentSelectorFragment.getInstance("CARD", totalPayment), category);
        Category category1 = new Category();
        category1.setTitle("CASH");
        adapter.addFragment(PaymentSelectorFragment.getInstance("CASH", totalPayment), category1);

        viewPagerPaymentOption.setAdapter(adapter);
        tabsPaymentOption.setupWithViewPager(viewPagerPaymentOption);
        int length = tabsPaymentOption.getTabCount();
        tabsPaymentOption.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tab.setCustomView(adapter.getTabView(tab.getCustomView(), tab.getPosition(), true));
                viewPagerPaymentOption.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tab.setCustomView(adapter.getTabView(tab.getCustomView(), tab.getPosition(), false));
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        for (int i = 0; i < length; i++) {
            if (i == 0) {
                tabsPaymentOption.getTabAt(i).setCustomView(adapter.getTabView(i, true));
            } else {
                tabsPaymentOption.getTabAt(i).setCustomView(adapter.getTabView(i, false));
            }
        }
        viewPagerPaymentOption.setCurrentItem(0);
        POSITION_OF_VIEW_PAGER = 0;
        //ExploreItem.setDefaultPositionOfViewPager(getArguments().getString(BOX_NAME), clickPosition);
        viewPagerPaymentOption.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                POSITION_OF_VIEW_PAGER = position;
                // ExploreItem.setDefaultPositionOfViewPager(getArguments().getString(BOX_NAME), position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    @OnClick(R.id.txtPlaceOrder)
    public void submitPlaceOrder() {
        if (POSITION_OF_VIEW_PAGER == 0) {
            startPayment(String.valueOf(Double.parseDouble(totalPayment) * 100), "Subscribe and Forget", user.getEmail(), user.getPhoneNumber(), user.getName());

        } else if (POSITION_OF_VIEW_PAGER == 1) {
            getUserLocation();
        }
    }

    public static String fmt(double d) {
        if (d == (long) d)
            return String.format("%d", (long) d);
        else
            return String.format("%s", d);
    }


    private void merge_cart_to_order_and_pay_offline() {
        final BoxLoader dialog = new BoxLoader(this).show();
        cleverTapOrderId = mergeOrderId;

        TheBox.getAPIService().merge_cart_items_to_order_payment_offline(PrefUtils.getToken(this), new MergeCartToOrderRequestBody(mergeOrderId, totalPayment, String.valueOf(latLng.getLatitude()), String.valueOf(latLng.getLongitude())))
                .enqueue(new Callback<PaymentResponse>() {
                    @Override
                    public void onResponse(Call<PaymentResponse> call, Response<PaymentResponse> response) {
                        dialog.dismiss();
                        if (response.body() != null) {
                            if (response.body().isSuccess()) {

                                /**
                                 * Save CleverTap Event; PaymentMode
                                 */
                                setCleverTapEventPaymentModeSuccess();

                                // Updating "Behaviour Keys" Linked to Order Model
                                PrefUtils.set_model_being_updated_on_server_details(
                                        TheBox.getInstance(),
                                        Constants.PREF_IS_ORDER_IS_LOADING,
                                        Constants.ORDERS_UPDATE_ON_SERVER_STARTED_TIMESTAMP,
                                        (new Date(System.currentTimeMillis())).getTime()
                                );

                                RealmList<Order> orders = new RealmList<>();
                                orders.add(response.body().getOrders());
                                OrderHelper.addAndNotify(orders);

                                //clear cart items
                                CartHelper.clearCart();

                                Toast.makeText(PaymentOptionActivity.this, response.body().getInfo(), Toast.LENGTH_SHORT).show();

                                //payment complete move to home
                                paymentCompleteMoveToHome();

                            } else {
                                Toast.makeText(PaymentOptionActivity.this, response.body().getInfo(), Toast.LENGTH_SHORT).show();
                                setCleverTapEventPaymentModeFailure(3);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<PaymentResponse> call, Throwable t) {
                        dialog.dismiss();
                        setCleverTapEventPaymentModeFailure(2);
                    }
                });
    }

    private void merge_cart_to_order_and_pay_online(String razorpayPaymentID) {
        final BoxLoader dialog = new BoxLoader(this).show();
        cleverTapOrderId = mergeOrderId;

        TheBox.getAPIService().merge_cart_items_to_order_payment_online(PrefUtils.getToken(this),
                new OnlinePaymentRequest(mergeOrderId, razorpayPaymentID, totalPayment, String.valueOf(latLng.getLatitude()), String.valueOf(latLng.getLongitude()), isMerging))
                .enqueue(new Callback<PaymentResponse>() {
                    @Override
                    public void onResponse(Call<PaymentResponse> call, Response<PaymentResponse> response) {
                        dialog.dismiss();
                        if (response.body() != null) {
                            if (response.body().isSuccess()) {

                                /**
                                 * Save CleverTap Event; Payment Mode
                                 */
                                setCleverTapEventPaymentModeSuccess();

                                // Updating "Behaviour Keys" Linked to Order Model
                                PrefUtils.set_model_being_updated_on_server_details(
                                        TheBox.getInstance(),
                                        Constants.PREF_IS_ORDER_IS_LOADING,
                                        Constants.ORDERS_UPDATE_ON_SERVER_STARTED_TIMESTAMP,
                                        (new Date(System.currentTimeMillis())).getTime()
                                );

                                RealmList<Order> orders = new RealmList<>();
                                orders.add(response.body().getOrders());
                                OrderHelper.addAndNotify(orders);

                                //clear cart items
                                CartHelper.clearCart();

                                Toast.makeText(PaymentOptionActivity.this, response.body().getInfo(), Toast.LENGTH_SHORT).show();

                                //payment complete move to home
                                paymentCompleteMoveToHome();

                            } else {
                                Toast.makeText(PaymentOptionActivity.this, response.body().getInfo(), Toast.LENGTH_SHORT).show();
                                setCleverTapEventPaymentModeFailure(3);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<PaymentResponse> call, Throwable t) {
                        dialog.dismiss();
                        setCleverTapEventPaymentModeFailure(2);
                    }
                });
    }

    private void pay_offline() {
        final BoxLoader dialog = new BoxLoader(this).show();
        cleverTapOrderId = addressAndOrders.get(0).getOrderId();

        TheBox.getAPIService().payOrders(PrefUtils.getToken(this), new PaymentRequestBody(addressAndOrders, String.valueOf(latLng.getLatitude()), String.valueOf(latLng.getLongitude())))
                .enqueue(new Callback<PaymentResponse>() {
                    @Override
                    public void onResponse(Call<PaymentResponse> call, Response<PaymentResponse> response) {
                        dialog.dismiss();
                        if (response.body() != null) {
                            if (response.body().isSuccess()) {

                                /**
                                 * Save CleverTap Event; PaymentMode
                                 */
                                setCleverTapEventPaymentModeSuccess();


                                // Updating "Behaviour Keys" Linked to Order Model
                                PrefUtils.set_model_being_updated_on_server_details(
                                        TheBox.getInstance(),
                                        Constants.PREF_IS_ORDER_IS_LOADING,
                                        Constants.ORDERS_UPDATE_ON_SERVER_STARTED_TIMESTAMP,
                                        (new Date(System.currentTimeMillis())).getTime()
                                );

                                RealmList<Order> orders = new RealmList<>();
                                orders.add(response.body().getOrders());
                                OrderHelper.addAndNotify(orders);

                                //clear cart items
                                CartHelper.clearCart();

                                Toast.makeText(PaymentOptionActivity.this, response.body().getInfo(), Toast.LENGTH_SHORT).show();

                                //payment complete move to home
                                paymentCompleteMoveToHome();

                            } else {
                                Toast.makeText(PaymentOptionActivity.this, response.body().getInfo(), Toast.LENGTH_SHORT).show();
                                setCleverTapEventPaymentModeFailure(3);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<PaymentResponse> call, Throwable t) {
                        dialog.dismiss();
                        setCleverTapEventPaymentModeFailure(2);
                    }
                });
    }


    private void pay_online(String razorpayPaymentID) {
        final BoxLoader dialog = new BoxLoader(this).show();
        int orderId = 0;
        if (isMerging) {
            orderId = mergeOrderId;
        } else {
            orderId = addressAndOrders.get(0).getOrderId();
        }
        cleverTapOrderId = orderId;

        TheBox.getAPIService().payOrderOnline(PrefUtils.getToken(this), new OnlinePaymentRequest(orderId, razorpayPaymentID, totalPayment, addressAndOrders.get(0).getOderDate().toString(),
                latitude, longitude, isMerging))
                .enqueue(new Callback<PaymentResponse>() {
                    @Override
                    public void onResponse(Call<PaymentResponse> call, Response<PaymentResponse> response) {
                        dialog.dismiss();
                        if (response.body() != null) {
                            if (response.body().isSuccess()) {

                                /**
                                 * Save CleverTap Event; PaymentMode
                                 */
                                setCleverTapEventPaymentModeSuccess();

                                // Updating "Behaviour Keys" Linked to Order Model
                                PrefUtils.set_model_being_updated_on_server_details(
                                        TheBox.getInstance(),
                                        Constants.PREF_IS_ORDER_IS_LOADING,
                                        Constants.ORDERS_UPDATE_ON_SERVER_STARTED_TIMESTAMP,
                                        (new Date(System.currentTimeMillis())).getTime()
                                );


                                RealmList<Order> orders = new RealmList<>();
                                orders.add(response.body().getOrders());
                                OrderHelper.addAndNotify(orders);
                                //clear cart items
                                CartHelper.clearCart();

                                Toast.makeText(PaymentOptionActivity.this, response.body().getInfo(), Toast.LENGTH_SHORT).show();

                                //payment complete move to home
                                paymentCompleteMoveToHome();

                            } else {
                                Toast.makeText(PaymentOptionActivity.this, response.body().getInfo(), Toast.LENGTH_SHORT).show();
                                setCleverTapEventPaymentModeFailure(3);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<PaymentResponse> call, Throwable t) {
                        dialog.dismiss();
                        setCleverTapEventPaymentModeFailure(2);
                    }
                });
    }

    public void paymentCompleteMoveToHome() {
        PrefUtils.putBoolean(PaymentOptionActivity.this, Keys.LOAD_ORDERED_MY_DELIVERIES, true);
        PrefUtils.putBoolean(PaymentOptionActivity.this, Keys.LOAD_ORDERED_USER_ITEM, true);
        PrefUtils.putBoolean(this, Keys.LOAD_CAROUSEL, true);

        Intent intent = new Intent(PaymentOptionActivity.this, MainActivity.class);
        intent.putExtra(MainActivity.EXTRA_ATTACH_FRAGMENT_NO, 1);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }


    public void startPayment(String amount, String description, String email, String phonenumber, String name) {

        /**
         * Put your key id generated in Razorpay dashboard here
         */
        final String test_key_id = "rzp_test_R4R82jOEbvmNDW";
        final String live_key_id = "rzp_live_e1nfI8frHunaFM";

        Checkout razorpayCheckout = new Checkout();
        if (BuildConfig.DEBUG) {
            razorpayCheckout.setKeyID(test_key_id);
        } else {
            razorpayCheckout.setKeyID(live_key_id);
        }

        /**
         * Image for checkout form can passed as reference to a drawable
         */
        razorpayCheckout.setImage(R.mipmap.ic_launcher);

        /**
         * Reference to current activity
         */
        Activity activity = this;

        try {
            JSONObject options = new JSONObject("{" +
                    "description: '" + description + "'," +
                    "currency: 'INR'}"
            );

            options.put("amount", amount);
            options.put("name", "The Box Cart");
            options.put("prefill", new JSONObject("{email: '" + email + "', contact: '" + phonenumber + "', name: '" + name + "'}"));

            razorpayCheckout.open(activity, options);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void onPaymentSuccess(String razorpayPaymentID) {
        this.razorpayPaymentID = razorpayPaymentID;
        getUserLocation();
    }

    private void payOnLine(String razorpayPaymentID) {
        if (mergeOrderId == 0) {
            pay_online(razorpayPaymentID);
        } else {
            merge_cart_to_order_and_pay_online(razorpayPaymentID);
        }
    }


    public void onPaymentError(int code, String response) {
        try {
            Toast.makeText(this, "Payment failed, Try Cash on Delivery ", Toast.LENGTH_SHORT).show();
            setCleverTapEventPaymentModeFailure(1);
        } catch (Exception e) {
            Log.e("com.merchant", e.getMessage(), e);
        }
    }

    private void getUserLocation() {
        checkGPSenable();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE_GET_LOCATION) {
            checkGPSenable();
            return;
        }
    }

    // Location Marking Issue
    private void checkGPSenable() {
        locationPermisionCounter++;
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            getLocationPermission();
        } else {
            if (locationPermisionCounter > 1) {
                proceedAndCompletePayment();
            } else {
                buildAlertMessageNoGps();
            }
        }
    }


    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.gps_error)
                .setCancelable(false)
                .setPositiveButton("Turn On", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), REQ_CODE_GET_LOCATION);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        //if location not provides allow to pay users
                        dialog.cancel();
                        proceedAndCompletePayment();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private void getLocationPermission() {
        if (AppUtil.checkPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)) {
            setLocation();
            return;
        }
        new TedPermission(this).setPermissions(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
                .setPermissionListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        setLocation();
                    }

                    @Override
                    public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                        //permission denied but complete the payment
                        proceedAndCompletePayment();
                    }
                }).check();
    }

    private void setLocation() {
        new FusedLocationService(this) {
            @Override
            protected void onSuccess(MyLocation mLastKnownLocation) {
                if (mLastKnownLocation != null) {
                    if (!locationRefreshed) {
                        locationRefreshed = true;
                        setLocation();
                        return;
                    }
                    latLng = new MyLocation(mLastKnownLocation.getLongitude(), mLastKnownLocation.getLatitude());
                    if (latLng != null) {
                        latitude = String.valueOf(latLng.getLatitude());
                        longitude = String.valueOf(latLng.getLongitude());
                    }
                    proceedAndCompletePayment();
                }
            }

            @Override
            protected void onFailed(ConnectionResult connectionResult) {
                latLng = new MyLocation("0.0", "0.0");
                locationRefreshed = false;
                proceedAndCompletePayment();
            }
        };
    }

    private void proceedAndCompletePayment() {
        if (TextUtils.isEmpty(razorpayPaymentID)) {
            fillUserInfo();
        } else {
            pay_online(razorpayPaymentID);
        }
    }

    private void fillUserInfo() {
        if (mergeOrderId == 0) {
            pay_offline();
        } else {
            merge_cart_to_order_and_pay_offline();
        }

    }

    /**
     * Clever Tap Event
     * Success
     */
    public void setCleverTapEventPaymentModeSuccess() {
        HashMap<String, Object> hashMap = new HashMap<>();
        if (POSITION_OF_VIEW_PAGER == 0) {
            hashMap.put("mode", "razorpay");
        } else if (POSITION_OF_VIEW_PAGER == 1) {
            hashMap.put("mode", "cod");
        }
        hashMap.put("amount", totalPayment);
        hashMap.put("payment_status", "success");
        hashMap.put("order_id", cleverTapOrderId);

        TheBox.getCleverTap().event.push("payment_mode", hashMap);
    }

    public void setCleverTapEventPaymentModeFailure(int failedMode) {
        HashMap<String, Object> hashMap = new HashMap<>();
        if (POSITION_OF_VIEW_PAGER == 0) {
            hashMap.put("mode", "razorpay");
        } else if (POSITION_OF_VIEW_PAGER == 1) {
            hashMap.put("mode", "cod");
        }
        hashMap.put("amount", totalPayment);
        hashMap.put("payment_status", "failed");
        hashMap.put("order_id", cleverTapOrderId);

        /**
         * 1- razorpay
         * 2- server failed or bad request
         * 3- response returns failed
         */
        switch (failedMode) {
            case 1:
                hashMap.put("failed_mode", "razorpay");
                break;
            case 2:
                hashMap.put("failed_mode", "server");
                break;
            case 3:
                hashMap.put("failed_mode", "response");
                break;
        }


        TheBox.getCleverTap().event.push("payment_mode", hashMap);
    }


}
