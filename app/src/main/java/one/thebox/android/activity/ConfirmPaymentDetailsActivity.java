package one.thebox.android.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;

import one.thebox.android.Models.AddressAndOrder;
import one.thebox.android.Models.Order;
import one.thebox.android.Models.User;
import one.thebox.android.R;
import one.thebox.android.adapter.PaymentDetailAdapter;
import one.thebox.android.util.CoreGsonUtils;
import one.thebox.android.util.PrefUtils;

public class ConfirmPaymentDetailsActivity extends BaseActivity {
    private RecyclerView recyclerViewPaymentDetail;
    private PaymentDetailAdapter paymentDetailAdapter;
    private CheckBox checkBox;
    private ArrayList<AddressAndOrder> addressAndOrders;
    private static final String EXTRA_ARRAY_LIST_ORDER = "array_list_order";
    private TextView selectDeliveryAddress;
    private User user;

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
    }

    public static Intent getInstance(Context context, ArrayList<AddressAndOrder> addressAndOrders) {
        Intent intent = new Intent(context, ConfirmPaymentDetailsActivity.class);
        intent.putExtra(EXTRA_ARRAY_LIST_ORDER, CoreGsonUtils.toJson(addressAndOrders));
        return intent;
    }

    private void setupRecyclerAdapter() {
        ArrayList<Order> orders = new ArrayList<>();
        for (AddressAndOrder addressAndOrder : addressAndOrders) {
            orders.add(addressAndOrder.getOrder());
        }
        paymentDetailAdapter = new PaymentDetailAdapter(this);
        paymentDetailAdapter.setOrders(orders);
        recyclerViewPaymentDetail.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewPaymentDetail.setAdapter(paymentDetailAdapter);
    }

    private void initViews() {
        recyclerViewPaymentDetail = (RecyclerView) findViewById(R.id.recycler_view_payment_detail);
    }
}
