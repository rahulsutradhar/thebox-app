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

import io.realm.Realm;
import one.thebox.android.Models.AddressAndOrder;
import one.thebox.android.Models.Order;
import one.thebox.android.Models.User;
import one.thebox.android.R;
import one.thebox.android.adapter.PaymentDetailAdapter;
import one.thebox.android.app.Constants;
import one.thebox.android.app.TheBox;
import one.thebox.android.util.CoreGsonUtils;
import one.thebox.android.util.PrefUtils;

public class ConfirmPaymentDetailsActivity extends BaseActivity {
    private static final String EXTRA_ARRAY_LIST_ORDER = "array_list_order";
    private static final String EXTRA_MERGE_ORDER_ID = "merge_order_id";
    private static final String EXTRA_TOTAL_CART_AMOUNT = "total_cart_amount";
    private static final String EXTRA_IS_MERGING = "is_merging_order";
    public static final int RECYCLER_VIEW_TYPE_FOOTER = 302;


    private Order cart;
    private Context context;
    private RecyclerView recyclerViewPaymentDetail;
    private PaymentDetailAdapter paymentDetailAdapter;
    private ArrayList<AddressAndOrder> addressAndOrders;
    private TextView payButton;
    private User user;
    private int mergeOrderId;
    private float amount_to_pay;
    private float totalAmountToPay;
    private boolean isMerging;

    public static Intent getInstance(Context context, ArrayList<AddressAndOrder> addressAndOrders) {
        Intent intent = new Intent(context, ConfirmPaymentDetailsActivity.class);
        intent.putExtra(EXTRA_ARRAY_LIST_ORDER, CoreGsonUtils.toJson(addressAndOrders));
        return intent;
    }

    public static Intent getInstance(Context context, ArrayList<AddressAndOrder> addressAndOrders, int mergeOrderId) {
        Intent intent = new Intent(context, ConfirmPaymentDetailsActivity.class);
        intent.putExtra(EXTRA_ARRAY_LIST_ORDER, CoreGsonUtils.toJson(addressAndOrders));
        intent.putExtra(EXTRA_MERGE_ORDER_ID, mergeOrderId);
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
        setupRecyclerAdapter();
    }

    private void initVariables() {
        String ordersString = getIntent().getStringExtra(EXTRA_ARRAY_LIST_ORDER);
        addressAndOrders = CoreGsonUtils.fromJsontoArrayList(ordersString, AddressAndOrder.class);
        mergeOrderId = getIntent().getIntExtra(EXTRA_MERGE_ORDER_ID, 0);
        isMerging = getIntent().getBooleanExtra(EXTRA_IS_MERGING, false);

        int cartId = PrefUtils.getUser(this).getCartId();
        Realm realm = TheBox.getRealm();
        Order cart = realm.where(Order.class)
                .notEqualTo(Order.FIELD_ID, 0)
                .equalTo(Order.FIELD_ID, cartId).findFirst();
        if (cart != null) {
            this.cart = realm.copyFromRealm(cart);
        }
    }

    private void setupRecyclerAdapter() {

        ArrayList<Order> orders = new ArrayList<>();
        for (AddressAndOrder addressAndOrder : addressAndOrders) {
            orders.add(addressAndOrder.getOrder());
        }
        paymentDetailAdapter = new PaymentDetailAdapter(this);
        paymentDetailAdapter.setOrders(orders);
        recyclerViewPaymentDetail.setItemViewCacheSize(paymentDetailAdapter.getItemsCount());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerViewPaymentDetail.setLayoutManager(linearLayoutManager);
        recyclerViewPaymentDetail.setAdapter(paymentDetailAdapter);
        paymentDetailAdapter.setViewType(RECYCLER_VIEW_TYPE_FOOTER);

        //show the final price
        amount_to_pay = paymentDetailAdapter.getFinalPaymentAmount();
        payButton.setText("Pay: " + Constants.RUPEE_SYMBOL + " " + amount_to_pay);

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
                intent.putExtra(EXTRA_TOTAL_CART_AMOUNT, String.valueOf(amount_to_pay));
                intent.putExtra(EXTRA_ARRAY_LIST_ORDER, getIntent().getStringExtra(EXTRA_ARRAY_LIST_ORDER));
                intent.putExtra(EXTRA_MERGE_ORDER_ID, getIntent().getIntExtra(EXTRA_MERGE_ORDER_ID, 0));
                intent.putExtra(EXTRA_IS_MERGING, isMerging);
                startActivity(intent);

            }
        });

        recyclerViewPaymentDetail.setItemViewCacheSize(20);
        recyclerViewPaymentDetail.setDrawingCacheEnabled(true);
        recyclerViewPaymentDetail.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_AUTO);
    }

    public void setCleverTapEventPaymentDetailActivity() {
        HashMap<String, Object> objectHashMap = new HashMap<>();
        objectHashMap.put("amount_to_pay", amount_to_pay);

        TheBox.getCleverTap().event.push("payment_details_activity", objectHashMap);
    }

}
