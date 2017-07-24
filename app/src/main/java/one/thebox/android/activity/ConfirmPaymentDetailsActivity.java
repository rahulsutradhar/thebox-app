package one.thebox.android.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import one.thebox.android.Helpers.cart.ProductQuantity;
import one.thebox.android.Models.address.Address;
import one.thebox.android.Models.checkout.PurchaseData;
import one.thebox.android.Models.update.Setting;
import one.thebox.android.R;
import one.thebox.android.ViewHelper.BoxLoader;
import one.thebox.android.adapter.checkout.PaymentDetailsAdapter;
import one.thebox.android.api.RequestBodies.cart.PaymentSummaryRequest;
import one.thebox.android.api.Responses.cart.PaymentSummaryResponse;
import one.thebox.android.app.Constants;
import one.thebox.android.app.TheBox;
import one.thebox.android.services.AuthenticationService;
import one.thebox.android.services.SettingService;
import one.thebox.android.util.CoreGsonUtils;
import one.thebox.android.util.PrefUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConfirmPaymentDetailsActivity extends BaseActivity {
    private RecyclerView recyclerViewPaymentDetail;
    private PaymentDetailsAdapter adapter;
    private TextView payButton;
    private String amountToPay;
    private Address address;
    private long timeSlotTimeStamp;
    private boolean isMerge;
    private String orderUuid;

    private LinearLayout progressIndicatorLayout;
    private View progressStep1, progressStep2, progressStep3, progressStep4, progressStep5, progressStep6;
    private TextView progressStepToCheckoutText;
    private Toolbar toolbar;
    private Setting setting;


    public static Intent getInstance(Context context, boolean isMerge, Address address, long timeSlotTimeStamp) {
        Intent intent = new Intent(context, ConfirmPaymentDetailsActivity.class);
        intent.putExtra(Constants.EXTRA_IS_CART_MERGING, isMerge);
        intent.putExtra(Constants.EXTRA_SELECTED_ADDRESS, CoreGsonUtils.toJson(address));
        intent.putExtra(Constants.EXTRA_TIMESLOT_SELECTED, timeSlotTimeStamp);
        return intent;
    }

    public static Intent getInstance(Context context, boolean isMerge, Address address, String orderUuid) {
        Intent intent = new Intent(context, ConfirmPaymentDetailsActivity.class);
        intent.putExtra(Constants.EXTRA_IS_CART_MERGING, isMerge);
        intent.putExtra(Constants.EXTRA_SELECTED_ADDRESS, CoreGsonUtils.toJson(address));
        intent.putExtra(Constants.EXTRA_SELECTED_ORDER_UUID, orderUuid);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_details);

        //Tootalbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        initViews();
        initVariables();
    }

    private void initVariables() {
        try {
            isMerge = getIntent().getBooleanExtra(Constants.EXTRA_IS_CART_MERGING, false);
            address = CoreGsonUtils.fromJson(getIntent().getStringExtra(Constants.EXTRA_SELECTED_ADDRESS), Address.class);
            timeSlotTimeStamp = getIntent().getLongExtra(Constants.EXTRA_TIMESLOT_SELECTED, 0);
            orderUuid = getIntent().getStringExtra(Constants.EXTRA_SELECTED_ORDER_UUID);

            setting = new SettingService().getSettings(this);
            if (setting.isFirstOrder()) {
                progressIndicatorLayout.setVisibility(View.VISIBLE);
            } else {
                progressIndicatorLayout.setVisibility(View.GONE);
            }

            if (!isMerge) {
                //API Request for 1st order; When there is no previous order
                fetchPaymentSummaryForCart(true);
            } else {
                fetchPaymentForMergeDeliveries();

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void initViews() {
        recyclerViewPaymentDetail = (RecyclerView) findViewById(R.id.recycler_view_payment_detail);
        payButton = (TextView) findViewById(R.id.button_pay);
        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /**
                 * Save CleverTap Event; PaymentDetailsActivity
                 */
                setCleverTapEventPaymentDetailActivity();

                Intent intent = new Intent(ConfirmPaymentDetailsActivity.this, PaymentOptionActivity.class);
                intent.putExtra(Constants.EXTRA_AMOUNT_TO_PAY, amountToPay);
                intent.putExtra(Constants.EXTRA_IS_CART_MERGING, isMerge);
                intent.putExtra(Constants.EXTRA_SELECTED_ADDRESS, CoreGsonUtils.toJson(address));
                intent.putExtra(Constants.EXTRA_TIMESLOT_SELECTED, timeSlotTimeStamp);
                intent.putExtra(Constants.EXTRA_SELECTED_ORDER_UUID, orderUuid);
                startActivity(intent);

            }
        });

        recyclerViewPaymentDetail.setItemViewCacheSize(3);
        recyclerViewPaymentDetail.setDrawingCacheEnabled(true);
        recyclerViewPaymentDetail.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_AUTO);

        //toolbar
        progressIndicatorLayout = (LinearLayout) findViewById(R.id.progress_indicator);
        progressStepToCheckoutText = (TextView) findViewById(R.id.progress_step_text);
        progressStep1 = (View) findViewById(R.id.progress_step1);
        progressStep2 = (View) findViewById(R.id.progress_step2);
        progressStep3 = (View) findViewById(R.id.progress_step3);
        progressStep4 = (View) findViewById(R.id.progress_step4);
        progressStep5 = (View) findViewById(R.id.progress_step5);
        progressStep5 = (View) findViewById(R.id.progress_step6);

    }


    /**
     * API call for Payment Summary For 1st order; if previous order doesnot exist
     */
    public void fetchPaymentSummaryForCart(boolean isCart) {
        final BoxLoader dialog = new BoxLoader(this).show();
        TheBox.getAPIService()
                .getPaymentSummaryForCart(PrefUtils.getToken(this), isCart,
                        new PaymentSummaryRequest(ProductQuantity.getProductQuantities()))
                .enqueue(new Callback<PaymentSummaryResponse>() {
                    @Override
                    public void onResponse(Call<PaymentSummaryResponse> call, Response<PaymentSummaryResponse> response) {
                        dialog.dismiss();
                        try {
                            if (response.isSuccessful()) {
                                if (response.body() != null) {

                                    amountToPay = response.body().getAmountToPay();
                                    payButton.setText("Amount to pay " + Constants.RUPEE_SYMBOL + " " + amountToPay);
                                    setUpData(response.body().getPurchaseDatas());
                                }
                            } else {
                                if (response.code() == Constants.UNAUTHORIZED) {
                                    //unauthorized user navigate to login
                                    new AuthenticationService().navigateToLogin(ConfirmPaymentDetailsActivity.this);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<PaymentSummaryResponse> call, Throwable t) {

                    }
                });
    }


    /**
     * Api Call for Payment Summary For Merge Orders
     */
    public void fetchPaymentForMergeDeliveries() {
        final BoxLoader dialog = new BoxLoader(this).show();

        TheBox.getAPIService()
                .getPaymentSummaryForMergeDeliveries(PrefUtils.getToken(this), orderUuid,
                        new PaymentSummaryRequest(ProductQuantity.getProductQuantities()))
                .enqueue(new Callback<PaymentSummaryResponse>() {
                    @Override
                    public void onResponse(Call<PaymentSummaryResponse> call, Response<PaymentSummaryResponse> response) {
                        dialog.dismiss();
                        try {
                            if (response.isSuccessful()) {
                                if (response.body() != null) {
                                    amountToPay = response.body().getAmountToPay();
                                    payButton.setText("Amount to pay " + Constants.RUPEE_SYMBOL + " " + amountToPay);
                                    setUpData(response.body().getPurchaseDatas());
                                }
                            } else {
                                if (response.code() == Constants.UNAUTHORIZED) {
                                    //unauthorized user navigate to login
                                    new AuthenticationService().navigateToLogin(ConfirmPaymentDetailsActivity.this);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<PaymentSummaryResponse> call, Throwable t) {

                    }
                });

    }

    /**
     * Set the list by removing the null values
     */
    public void setUpData(ArrayList<PurchaseData> purchaseDatas) {
        ArrayList<PurchaseData> updatePurchaseData = new ArrayList<>();

        for (PurchaseData purchaseData : purchaseDatas) {
            if (purchaseData.getProductDetails() != null) {
                if (purchaseData.getProductDetails().size() > 0) {
                    updatePurchaseData.add(purchaseData);
                }
            }
        }

        setUpRecyclerView(updatePurchaseData);
    }

    public void setUpRecyclerView(ArrayList<PurchaseData> purchaseDatas) {

        if (recyclerViewPaymentDetail != null) {
            final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerViewPaymentDetail.setLayoutManager(linearLayoutManager);
            adapter = new PaymentDetailsAdapter(this, purchaseDatas);
            recyclerViewPaymentDetail.setAdapter(adapter);
        }


    }

    public void setCleverTapEventPaymentDetailActivity() {
        HashMap<String, Object> objectHashMap = new HashMap<>();
        objectHashMap.put("amount_to_pay", amountToPay);

        TheBox.getCleverTap().event.push("payment_details_activity", objectHashMap);
    }

}
