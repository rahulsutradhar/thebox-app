package one.thebox.android.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import one.thebox.android.Models.AddressAndOrder;
import one.thebox.android.Models.Order;
import one.thebox.android.Models.User;
import one.thebox.android.Models.checkout.PurchaseData;
import one.thebox.android.R;
import one.thebox.android.ViewHelper.BoxLoader;
import one.thebox.android.adapter.checkout.PaymentDetailsAdapter;
import one.thebox.android.api.Responses.PaymentDetailsResponse;
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
    private static final String EXTRA_IS_CART = "is_from_cart";
    private static final String EXTRA_ORDER_ID = "extra_order_id";
    private static final String EXTRA_PAY_FROM_ORDER = "extra_pay_From_order";


    private RecyclerView recyclerViewPaymentDetail;
    private PaymentDetailsAdapter adapter;
    private TextView payButton;
    private User user;
    private int mergeOrderId;
    private String amountToPay;
    private boolean isMerging, isCart, payFromOrder;
    private int orderId;


    public static Intent getInstance(Context context, ArrayList<AddressAndOrder> addressAndOrders, boolean isCart) {
        Intent intent = new Intent(context, ConfirmPaymentDetailsActivity.class);
        intent.putExtra(EXTRA_ARRAY_LIST_ORDER, CoreGsonUtils.toJson(addressAndOrders));
        intent.putExtra(EXTRA_IS_CART, isCart);
        return intent;
    }

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
        user = PrefUtils.getUser(this);
        setTitle("Payment Details");
        initViews();
        initVariables();


       /* setupRecyclerAdapter();*/
    }

    private void initVariables() {
        mergeOrderId = getIntent().getIntExtra(EXTRA_MERGE_ORDER_ID, 0);
        isMerging = getIntent().getBooleanExtra(EXTRA_IS_MERGING, false);
        isCart = getIntent().getBooleanExtra(EXTRA_IS_CART, false);
        orderId = getIntent().getIntExtra(EXTRA_ORDER_ID, 0);
        payFromOrder = getIntent().getBooleanExtra(EXTRA_PAY_FROM_ORDER, false);


        int cartId = user.getCartId();

        HashMap<String, Integer> param = new HashMap<>();

        //condition for dynamic query in the API
        if (isCart) {
            param.put("cart_id", cartId);
        } else if (isMerging) {
            param.put("cart_id", cartId);
            param.put("order_id", mergeOrderId);
        } else if (payFromOrder) {
            param.put("order_id", orderId);
        }

        //call server to fetch data
        fetchPaymentDetailsData(param);
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
                intent.putExtra(EXTRA_TOTAL_CART_AMOUNT, amountToPay);
                intent.putExtra(EXTRA_ARRAY_LIST_ORDER, getIntent().getStringExtra(EXTRA_ARRAY_LIST_ORDER));
                intent.putExtra(EXTRA_MERGE_ORDER_ID, getIntent().getIntExtra(EXTRA_MERGE_ORDER_ID, 0));
                intent.putExtra(EXTRA_IS_MERGING, isMerging);
                startActivity(intent);

            }
        });

        recyclerViewPaymentDetail.setItemViewCacheSize(3);
        recyclerViewPaymentDetail.setDrawingCacheEnabled(true);
        recyclerViewPaymentDetail.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_AUTO);
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
