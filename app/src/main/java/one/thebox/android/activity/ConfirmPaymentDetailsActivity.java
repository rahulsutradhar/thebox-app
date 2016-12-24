package one.thebox.android.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.common.api.Api;
import com.razorpay.Checkout;

import org.json.JSONObject;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmList;
import one.thebox.android.Helpers.CartHelper;
import one.thebox.android.Helpers.OrderHelper;
import one.thebox.android.Models.AddressAndOrder;
import one.thebox.android.Models.Order;
import one.thebox.android.Models.User;
import one.thebox.android.R;
import one.thebox.android.Services.UpdateOrderService;
import one.thebox.android.ViewHelper.BoxLoader;
import one.thebox.android.adapter.PaymentDetailAdapter;
import one.thebox.android.api.ApiResponse;
import one.thebox.android.api.RequestBodies.MergeCartToOrderRequestBody;
import one.thebox.android.api.RequestBodies.PaymentRequestBody;
import one.thebox.android.api.Responses.PaymentResponse;
import one.thebox.android.app.MyApplication;
import one.thebox.android.util.Constants;
import one.thebox.android.util.CoreGsonUtils;
import one.thebox.android.util.PrefUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConfirmPaymentDetailsActivity extends BaseActivity {
    private static final String EXTRA_ARRAY_LIST_ORDER = "array_list_order";
    private static final String EXTRA_MERGE_ORDER_ID = "merge_order_id";
    private static final String EXTRA_TOTAL_CART_AMOUNT="total_cart_amount";

    private Order cart;
    private Context context;
    private RecyclerView recyclerViewPaymentDetail;
    private PaymentDetailAdapter paymentDetailAdapter;
    private ArrayList<AddressAndOrder> addressAndOrders;
    private TextView payButton;
    private User user;
    private int mergeOrderId;
    private float amount_to_pay;

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

        int cartId = PrefUtils.getUser(this).getCartId();
        Realm realm = MyApplication.getRealm();
        Order cart = realm.where(Order.class)
                .notEqualTo(Order.FIELD_ID, 0)
                .equalTo(Order.FIELD_ID, cartId).findFirst();
        if(cart!=null) {
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
        recyclerViewPaymentDetail.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewPaymentDetail.setAdapter(paymentDetailAdapter);


        if (mergeOrderId == 0) {
            amount_to_pay = paymentDetailAdapter.getFinalPaymentAmount();
        }
        else {
            amount_to_pay = cart.getTotalPriceOfUserItemsForCart();
        }

        payButton.setText("Pay: Rs " + amount_to_pay);

    }

    private void initViews() {
        recyclerViewPaymentDetail = (RecyclerView) findViewById(R.id.recycler_view_payment_detail);
        payButton = (TextView) findViewById(R.id.button_pay);
        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(ConfirmPaymentDetailsActivity.this,PaymentOptionActivity.class);
                intent.putExtra(EXTRA_TOTAL_CART_AMOUNT,String.valueOf(amount_to_pay));
                intent.putExtra(EXTRA_ARRAY_LIST_ORDER,getIntent().getStringExtra(EXTRA_ARRAY_LIST_ORDER));
                intent.putExtra(EXTRA_MERGE_ORDER_ID,getIntent().getIntExtra(EXTRA_MERGE_ORDER_ID, 0));
                startActivity(intent);

            }
        });
    }

}
