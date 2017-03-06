package one.thebox.android.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Date;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import one.thebox.android.Events.UpdateOrderItemEvent;
import one.thebox.android.Models.AddressAndOrder;
import one.thebox.android.Models.ItemConfig;
import one.thebox.android.Models.Order;
import one.thebox.android.Models.UserItem;
import one.thebox.android.R;
import one.thebox.android.adapter.SearchDetailAdapter;
import one.thebox.android.app.TheBox;
import one.thebox.android.util.DateTimeUtil;

public class OrderItemsActivity extends BaseActivity {

    private static final String EXTRA_USER_ITEM_ARRAY_LIST = "user_item_array_list";
    private RecyclerView recyclerView;
    private SearchDetailAdapter userItemRecyclerAdapter;
    private RealmList<UserItem> userItems = new RealmList<>();
    private int orderId;
    private Order order;
    private TextView payTextView;
    /**
     * GLide Request Manager
     */
    private RequestManager glideRequestManager;

    public static Intent newInstance(Context context, int orderId) {
        return new Intent(context, OrderItemsActivity.class).putExtra(EXTRA_USER_ITEM_ARRAY_LIST, orderId);
    }

    private void initVariables() {
        if (getIntent().hasExtra(EXTRA_USER_ITEM_ARRAY_LIST)) {
            orderId = getIntent().getIntExtra(EXTRA_USER_ITEM_ARRAY_LIST, 0);
        }
        Realm realm = TheBox.getRealm();
        RealmQuery<Order> query = realm.where(Order.class)
                .notEqualTo(Order.FIELD_ID, 0).equalTo(Order.FIELD_ID, orderId);

        RealmResults<Order> realmResults = query.findAll();
        order = realmResults.get(0);
        order = realm.copyFromRealm(order);
        userItems = order.getUserItems();


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            Date date = null;
            date = DateTimeUtil.convertStringToDate(order.getDeliveryScheduleAt());
            toolbar.setSubtitle(AddressAndOrder.getDateString(date));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_items);
        setTitle("Delivery Items");
        initVariables();
        initViews();
        setupRecyclerView();
    }

    private void initViews() {
        this.glideRequestManager = Glide.with(this);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        payTextView = (TextView) findViewById(R.id.button_pay);
        payTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(OrderItemsActivity.newInstance(OrderItemsActivity.this, orderId));
            }
        });
        setPayButton();
    }

    private void setPayButton() {
        if (order.isPaid()) {
            payTextView.setText("Payment Complete");
            payTextView.setBackgroundColor(Color.LTGRAY);
            payTextView.setOnClickListener(null);
        } else {
            payTextView.setText("Pay Rs " + order.getTotalPrice());
            payTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RealmList<Order> orders = new RealmList<>();
                    orders.add(order);
                    startActivity(ConfirmAddressActivity.getInstance(OrderItemsActivity.this, orders, false));
                }
            });
        }
    }

    public float getTotalPrice() {
        int total = 0;
        for (int i = 0; i < userItemRecyclerAdapter.getUserItems().size(); i++) {
            ItemConfig selectedItemConfig = userItemRecyclerAdapter.getUserItems().get(i).getBoxItem().getItemConfigById(userItemRecyclerAdapter.getUserItems().get(i).getSelectedConfigId());
            total = total + selectedItemConfig.getPrice() * userItemRecyclerAdapter.getUserItems().get(i).getQuantity();
        }
        return total;
    }

    private void setupRecyclerView() {
        userItemRecyclerAdapter = new SearchDetailAdapter(this, glideRequestManager);
        userItemRecyclerAdapter.setBoxItems(null, userItems);
        userItemRecyclerAdapter.setUserItemQuantities(order.getId(), order.getUserItemQuantities());
        userItemRecyclerAdapter.setHasUneditableUserItem(true);
        if (order.isPaid()) {
            userItemRecyclerAdapter.setHide_quantity_selector_in_this_order_item_view(true);
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(userItemRecyclerAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe
    public void onUpdateOrderItemEvent(final UpdateOrderItemEvent updateOrderItemEvent) {
        if (OrderItemsActivity.this != null) {
            OrderItemsActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    orderId = order.getId();
                    initVariables();
                    setPayButton();
                }
            });
        }
    }

}
