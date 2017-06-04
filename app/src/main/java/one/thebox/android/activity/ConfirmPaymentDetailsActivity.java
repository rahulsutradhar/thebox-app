package one.thebox.android.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import one.thebox.android.Helpers.cart.ProductQuantity;
import one.thebox.android.Models.AddressAndOrder;
import one.thebox.android.Models.User;
import one.thebox.android.Models.address.Address;
import one.thebox.android.Models.checkout.PurchaseData;
import one.thebox.android.R;
import one.thebox.android.ViewHelper.BoxLoader;
import one.thebox.android.adapter.checkout.PaymentDetailsAdapter;
import one.thebox.android.api.RequestBodies.cart.PaymentSummaryRequest;
import one.thebox.android.api.Responses.PaymentDetailsResponse;
import one.thebox.android.api.Responses.cart.PaymentSummaryResponse;
import one.thebox.android.app.Constants;
import one.thebox.android.app.TheBox;
import one.thebox.android.util.CoreGsonUtils;
import one.thebox.android.util.PrefUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConfirmPaymentDetailsActivity extends BaseActivity {
    private static final String EXTRA_ARRAY_LIST_ORDER = "array_list_order";
    private static final String EXTRA_MERGE_ORDER_ID = "merge_order_id";
    private static final String EXTRA_TOTAL_CART_AMOUNT = "total_cart_amount";
    private static final String EXTRA_IS_MERGING = "is_merging_order";
    private static final String EXTRA_ORDER_ID = "extra_order_id";
    private static final String EXTRA_PAY_FROM_ORDER = "extra_pay_From_order";


    private static final String EXTRA_IS_CART = "is_from_cart";

    private RecyclerView recyclerViewPaymentDetail;
    private PaymentDetailsAdapter adapter;
    private TextView payButton;
    private User user;
    private int mergeOrderId;
    private String amountToPay;
    private boolean isMerging, isCart, payFromOrder;
    private int orderId;


    private Address address;
    private long timeSlotTimeStamp;
    private boolean isMerge;
    private String orderUuid;

    /**
     * Refactor
     */
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

    /**
     * Old
     */
    /*public static Intent getInstance(Context context, ArrayList<AddressAndOrder> addressAndOrders, boolean isCart) {
        Intent intent = new Intent(context, ConfirmPaymentDetailsActivity.class);
        intent.putExtra(EXTRA_ARRAY_LIST_ORDER, CoreGsonUtils.toJson(addressAndOrders));
        intent.putExtra(EXTRA_IS_CART, isCart);
        return intent;
    }
*/
    public static Intent getInstance(Context context, int orderId, boolean payFromOrder, ArrayList<AddressAndOrder> addressAndOrders) {
        Intent intent = new Intent(context, ConfirmPaymentDetailsActivity.class);
        intent.putExtra(EXTRA_ORDER_ID, orderId);
        intent.putExtra(EXTRA_PAY_FROM_ORDER, payFromOrder);
        intent.putExtra(EXTRA_ARRAY_LIST_ORDER, CoreGsonUtils.toJson(addressAndOrders));
        return intent;
    }

    public static Intent getInstance(Context context, ArrayList<AddressAndOrder> addressAndOrders, int mergeOrderId, boolean isMerging) {
        Intent intent = new Intent(context, ConfirmPaymentDetailsActivity.class);
        intent.putExtra(EXTRA_ARRAY_LIST_ORDER, CoreGsonUtils.toJson(addressAndOrders));
        intent.putExtra(EXTRA_MERGE_ORDER_ID, mergeOrderId);
        intent.putExtra(EXTRA_IS_MERGING, isMerging);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_details);
        setTitle("Payment Summary");
        initViews();
        initVariables();
    }

    private void initVariables() {
        try {
            isMerge = getIntent().getBooleanExtra(Constants.EXTRA_IS_CART_MERGING, false);
            address = CoreGsonUtils.fromJson(getIntent().getStringExtra(Constants.EXTRA_SELECTED_ADDRESS), Address.class);
            timeSlotTimeStamp = getIntent().getLongExtra(Constants.EXTRA_TIMESLOT_SELECTED, 0);
            orderUuid = getIntent().getStringExtra(Constants.EXTRA_SELECTED_ORDER_UUID);

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
                intent.putExtra(Constants.EXTRA_SELECTED_ORDER_UUID,orderUuid);
                startActivity(intent);

            }
        });

        recyclerViewPaymentDetail.setItemViewCacheSize(3);
        recyclerViewPaymentDetail.setDrawingCacheEnabled(true);
        recyclerViewPaymentDetail.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_AUTO);
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
     * API call for payment Details When user proceed from Cart
     */
    public void fetchPaymentDetailsData(HashMap<String, Integer> params) {
        final BoxLoader dialog = new BoxLoader(this).show();

        TheBox.getAPIService().getPaymentDetailsData(params)
                .enqueue(new Callback<PaymentDetailsResponse>() {
                    @Override
                    public void onResponse(Call<PaymentDetailsResponse> call, Response<PaymentDetailsResponse> response) {
                        dialog.dismiss();
                        try {
                            if (response.isSuccessful()) {
                                if (response.body() != null) {
                                    amountToPay = response.body().getAmountToPay();
                                    payButton.setText("Amount to pay " + Constants.RUPEE_SYMBOL + " " + amountToPay);
                                    setUpData(response.body().getPurchaseDatas());
                                }
                            }

                        } catch (NullPointerException npe) {
                            npe.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<PaymentDetailsResponse> call, Throwable t) {
                        dialog.dismiss();
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
