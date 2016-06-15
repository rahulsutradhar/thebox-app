package one.thebox.android.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.text.ParseException;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import one.thebox.android.Events.UpdateOrderItemEvent;
import one.thebox.android.Models.AddressAndOrder;
import one.thebox.android.Models.ItemConfig;
import one.thebox.android.Models.Order;
import one.thebox.android.Models.User;
import one.thebox.android.Models.UserItem;
import one.thebox.android.R;
import one.thebox.android.adapter.SearchDetailAdapter;
import one.thebox.android.app.MyApplication;
import one.thebox.android.util.DateTimeUtil;

public class OrderItemsActivity extends BaseActivity {

    private static final String EXTRA_USER_ITEM_ARRAY_LIST = "user_item_array_list";
    private RecyclerView recyclerView;
    private SearchDetailAdapter userItemRecyclerAdapter;
    private RealmList<UserItem> userItems = new RealmList<>();
    private int orderId;
    private Order order;
    private TextView payTextView;

    public static Intent newInstance(Context context, int orderId) {
        return new Intent(context, OrderItemsActivity.class).putExtra(EXTRA_USER_ITEM_ARRAY_LIST, orderId);
    }

    private void initVariables() {
        orderId = getIntent().getIntExtra(EXTRA_USER_ITEM_ARRAY_LIST, 0);
        Realm realm = MyApplication.getRealm();
        RealmQuery<Order> query = realm.where(Order.class)
                .notEqualTo(Order.FIELD_ID, 0).equalTo(Order.FIELD_ID, orderId);

        RealmResults<Order> realmResults = query.findAll();
        order = realmResults.get(0);
        order = realm.copyFromRealm(order);
        userItems.addAll(order.getUserItems());
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            Date date = null;
            try {
                date = DateTimeUtil.convertStringToDate(order.getDeliveryScheduleAt());
                toolbar.setSubtitle(AddressAndOrder.getDateString(date));
            } catch (ParseException e) {
                e.printStackTrace();
            }
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
            payTextView.setText("Paid");
            payTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
        } else {
            payTextView.setText("Pay Rs " + order.getTotalPrice());
            payTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RealmList<Order> orders = new RealmList<>();
                    orders.add(order);
                    startActivity(ConfirmAddressActivity.getInstance(OrderItemsActivity.this, orders));
                }
            });
        }
    }

    public float getTotalPrice() {
        int total = 0;
        for (int i = 0; i < order.getUserItems().size(); i++) {
            ItemConfig selectedItemConfig = order.getUserItems().get(i).getBoxItem().getItemConfigById(order.getUserItems().get(i).getSelectedConfigId());
            total = total + selectedItemConfig.getPrice() * order.getUserItems().get(i).getQuantity();
        }
        return total;
    }

    private void setupRecyclerView() {
        userItemRecyclerAdapter = new SearchDetailAdapter(this);
        userItemRecyclerAdapter.setBoxItems(null, userItems);
        userItemRecyclerAdapter.setHasUneditableUserItem(true);
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
    public void onUpdateOrderItemEvent(UpdateOrderItemEvent updateOrderItemEvent) {
        this.userItems = userItemRecyclerAdapter.getUserItems();
        if (!order.isPaid())
            payTextView.setText("Pay Rs " + getTotalPrice());

        order.getUserItems().get(updateOrderItemEvent.getPosition()).setQuantity(updateOrderItemEvent.getUserItem().getQuantity());
        order.getUserItems().get(updateOrderItemEvent.getPosition()).setSelectedConfigId(updateOrderItemEvent.getUserItem().getSelectedConfigId());
        Realm realm = MyApplication.getRealm();
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(order);
        realm.commitTransaction();


    }

}
