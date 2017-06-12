package one.thebox.android.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;

import java.util.ArrayList;

import one.thebox.android.Models.order.Order;
import one.thebox.android.Models.order.OrderItem;
import one.thebox.android.R;
import one.thebox.android.ViewHelper.BoxLoader;
import one.thebox.android.adapter.orders.OrderItemAdapter;
import one.thebox.android.api.Responses.order.OrderItemResponse;
import one.thebox.android.app.Constants;
import one.thebox.android.app.TheBox;
import one.thebox.android.util.CoreGsonUtils;
import one.thebox.android.util.PrefUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderItemsActivity extends BaseActivity {

    private static final String EXTRA_USER_ITEM_ARRAY_LIST = "user_item_array_list";
    private static final String EXTRA_ORDER_DATE = "extra_order_date";
    private RecyclerView recyclerView;
    private OrderItemAdapter orderItemAdapter;
    private TextView payTextView, emptyState;
    private Toolbar toolbar;
    private Order order = null;
    private int position;
    /**
     * GLide Request Manager
     */
    private RequestManager glideRequestManager;

    /**
     * Refactor
     */
    public static Intent newInstance(Context context, Order order, int position) {
        return new Intent(context, OrderItemsActivity.class)
                .putExtra(Constants.EXTRA_ORDER, CoreGsonUtils.toJson(order))
                .putExtra(Constants.EXTRA_CLICK_POSITION, position);
    }


    /**
     * Old
     */

    public static Intent newInstance(Context context, int orderId, String orderDate) {
        return new Intent(context, OrderItemsActivity.class)
                .putExtra(EXTRA_USER_ITEM_ARRAY_LIST, orderId)
                .putExtra(EXTRA_ORDER_DATE, orderDate);
    }

    private void initVariables() {
        try {
            order = CoreGsonUtils.fromJson(getIntent().getStringExtra(Constants.EXTRA_ORDER), Order.class);
            position = getIntent().getIntExtra(Constants.EXTRA_CLICK_POSITION, -1);
            setPayButton();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_items);
        setTitle("Delivery Items");
        initViews();
        initVariables();

        //fetch Ordered Items
        fetchOrderItemFromServer();
    }

    private void initViews() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        this.glideRequestManager = Glide.with(this);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        payTextView = (TextView) findViewById(R.id.button_pay);
        emptyState = (TextView) findViewById(R.id.empty_state);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            toolbar.setSubtitle(order.getOrderDate());
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResultForActivity();
            }
        });
    }

    public void setResultForActivity() {
        Intent intent = new Intent(OrderItemsActivity.this, MainActivity.class);
        intent.putExtra(Constants.EXTRA_ORDER, CoreGsonUtils.toJson(order));
        intent.putExtra(Constants.EXTRA_CLICK_POSITION, position);
        setResult(4, intent);
        finish();
    }

    private void setPayButton() {
        if (order.isPaymentComplete()) {
            payTextView.setText("Payment Complete");
            payTextView.setBackgroundColor(Color.LTGRAY);
            payTextView.setClickable(false);
            payTextView.setEnabled(false);
        } else {
            payTextView.setText("Pay " + Constants.RUPEE_SYMBOL + " " + order.getAmountToPay());
            payTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(ConfirmTimeSlotActivity.newInstance(OrderItemsActivity.this, order, true, false));
                }
            });
            payTextView.setClickable(true);
            payTextView.setEnabled(true);
        }
    }

    /**
     * Called from OrderItemAdapter
     */
    public void updateView(Order order) {
        this.order = order;
        if (order.getNoOfItems() == 0) {
            emptyState.setVisibility(View.VISIBLE);
            payTextView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
        } else {
            payTextView.setText("Pay " + Constants.RUPEE_SYMBOL + " " + order.getAmountToPay());
        }
    }


    private void setupRecyclerView(ArrayList<OrderItem> orderItems, boolean isChangesApplicable) {

        if (orderItems.size() > 0) {
            emptyState.setVisibility(View.GONE);
            payTextView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.VISIBLE);

            if (orderItemAdapter == null) {
                orderItemAdapter = new OrderItemAdapter(this, this, glideRequestManager, orderItems, isChangesApplicable);
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
                recyclerView.setAdapter(orderItemAdapter);
            }

        } else {
            emptyState.setVisibility(View.VISIBLE);
            payTextView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
        }

    }

    /**
     * Request Server for Ordered Items
     */
    public void fetchOrderItemFromServer() {

        final BoxLoader dialog = new BoxLoader(this).show();
        TheBox.getAPIService()
                .getOrderItems(PrefUtils.getToken(this), order.getUuid())
                .enqueue(new Callback<OrderItemResponse>() {
                    @Override
                    public void onResponse(Call<OrderItemResponse> call, Response<OrderItemResponse> response) {
                        dialog.dismiss();
                        try {
                            if (response.isSuccessful()) {
                                if (response.body() != null) {
                                    setupRecyclerView(response.body().getOrderItems(), response.body().isChangesApplicable());
                                }
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(TheBox.getAppContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<OrderItemResponse> call, Throwable t) {
                        dialog.dismiss();
                        Toast.makeText(TheBox.getAppContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            setResultForActivity();
        }
        return super.onKeyDown(keyCode, event);
    }
}
