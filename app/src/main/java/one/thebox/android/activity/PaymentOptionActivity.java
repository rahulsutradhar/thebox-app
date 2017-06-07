package one.thebox.android.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.razorpay.Checkout;

import org.json.JSONObject;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import one.thebox.android.BuildConfig;
import one.thebox.android.Helpers.cart.CartHelper;
import one.thebox.android.Helpers.cart.ProductQuantity;
import one.thebox.android.Models.Category;
import one.thebox.android.Models.order.Order;
import one.thebox.android.Models.User;
import one.thebox.android.Models.address.Address;
import one.thebox.android.R;
import one.thebox.android.R2;
import one.thebox.android.ViewHelper.BoxLoader;
import one.thebox.android.ViewHelper.ViewPagerAdapter;
import one.thebox.android.api.RequestBodies.payment.MakePaymentRequest;
import one.thebox.android.api.Responses.payment.MakePaymentResponse;
import one.thebox.android.app.Constants;
import one.thebox.android.app.Keys;
import one.thebox.android.app.TheBox;
import one.thebox.android.fragment.PaymentSelectorFragment;
import one.thebox.android.util.CoreGsonUtils;
import one.thebox.android.util.FusedLocationService;
import one.thebox.android.util.PrefUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by Ruchit on 9/26/2016.
 * <p>
 * Modified by Developers on 06/04/2017.
 */
public class PaymentOptionActivity extends AppCompatActivity {

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

    private FusedLocationService.MyLocation latLng = new FusedLocationService.MyLocation("0.0", "0.0");

    private boolean isMerge;
    private Address address;
    private long timeSlotTimeStamp;
    private String orderUuid;
    private Order order;
    private boolean isPayFromOrder = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_options);
        ButterKnife.bind(this);
        if (getIntent().hasExtra(Constants.EXTRA_AMOUNT_TO_PAY)) {
            totalPayment = getIntent().getStringExtra(Constants.EXTRA_AMOUNT_TO_PAY);
            txtTotalAmount.setText(fmt(Double.parseDouble(totalPayment)));
        }
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
        user = PrefUtils.getUser(this);
        address = CoreGsonUtils.fromJson(getIntent().getStringExtra(Constants.EXTRA_SELECTED_ADDRESS), Address.class);
        isMerge = getIntent().getBooleanExtra(Constants.EXTRA_IS_CART_MERGING, false);
        timeSlotTimeStamp = getIntent().getLongExtra(Constants.EXTRA_TIMESLOT_SELECTED, 0);
        orderUuid = getIntent().getStringExtra(Constants.EXTRA_SELECTED_ORDER_UUID);
        order = CoreGsonUtils.fromJson(getIntent().getStringExtra(Constants.EXTRA_ORDER), Order.class);
        isPayFromOrder = getIntent().getBooleanExtra(Constants.EXTRA_IS_FROM_ORDER, false);

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
            /**
             * Set Data for COD
             */
            setRequestDataForCod();
        }
    }

    /**
     * Request Data for COD
     */
    public void setRequestDataForCod() {
        MakePaymentRequest makePaymentRequest;
        if (isPayFromOrder) {
            //paying from order
            makePaymentRequest = new MakePaymentRequest(true, order.getUuid(), timeSlotTimeStamp, order.getAmountToPay());
        } else {
            if (isMerge) {
                //merge orders
                makePaymentRequest = new MakePaymentRequest(true, true, address.getUuid(), orderUuid, ProductQuantity.getProductQuantities());
            } else {
                //cart
                makePaymentRequest = new MakePaymentRequest(true, false, address.getUuid(), timeSlotTimeStamp, ProductQuantity.getProductQuantities());
            }
        }
        makePayment(makePaymentRequest);
    }

    /**
     * Request Data for ONLINE
     */
    public void setRequestDataForOnline(String razorpayID) {
        MakePaymentRequest makePaymentRequest;
        if (isPayFromOrder) {
            //pay from order
            makePaymentRequest = new MakePaymentRequest(false, razorpayID, order.getUuid(), timeSlotTimeStamp, order.getAmountToPay());
        } else {
            if (isMerge) {
                //merge order
                makePaymentRequest = new MakePaymentRequest(false, true, razorpayID, address.getUuid(), orderUuid, ProductQuantity.getProductQuantities());
            } else {
                //cart
                makePaymentRequest = new MakePaymentRequest(false, false, razorpayID, address.getUuid(), timeSlotTimeStamp, ProductQuantity.getProductQuantities());
            }
        }
        makePayment(makePaymentRequest);
    }

    public static String fmt(double d) {
        if (d == (long) d)
            return String.format("%d", (long) d);
        else
            return String.format("%s", d);
    }


    /**
     * Make Payment COD: Cart
     */
    private void makePayment(MakePaymentRequest makePaymentRequest) {
        final BoxLoader dialog = new BoxLoader(this).show();
        TheBox.getAPIService()
                .makePayment(PrefUtils.getToken(this), makePaymentRequest)
                .enqueue(new Callback<MakePaymentResponse>() {
                    @Override
                    public void onResponse(Call<MakePaymentResponse> call, Response<MakePaymentResponse> response) {
                        dialog.dismiss();
                        try {
                            if (response.isSuccessful()) {
                                if (response.body().isStatus()) {
                                    //payment Successfull
                                    paymentCompleteClearCartAndMoveToHome();
                                } else {
                                    Toast.makeText(PaymentOptionActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (Exception e) {
                            Toast.makeText(PaymentOptionActivity.this, "Something went wrong, please try again.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<MakePaymentResponse> call, Throwable t) {
                        Toast.makeText(PaymentOptionActivity.this, "Something went wrong, please try again.", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    public void paymentCompleteClearCartAndMoveToHome() {
        if (!isPayFromOrder) {
            //clear Cart
            CartHelper.clearCart(true);
            ProductQuantity.trash();
        }

        //set the flags
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
        setRequestDataForOnline(razorpayPaymentID);
    }

    private void payOnLine(String razorpayPaymentID) {
        setRequestDataForOnline(razorpayPaymentID);
    }


    public void onPaymentError(int code, String response) {
        try {
            Toast.makeText(this, "Payment failed, Try Cash on Delivery ", Toast.LENGTH_SHORT).show();
            setCleverTapEventPaymentModeFailure(1);
        } catch (Exception e) {
            Log.e("com.merchant", e.getMessage(), e);
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
