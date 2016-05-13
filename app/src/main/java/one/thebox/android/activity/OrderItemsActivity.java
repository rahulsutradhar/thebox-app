package one.thebox.android.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import one.thebox.android.Models.Order;
import one.thebox.android.Models.UserItem;
import one.thebox.android.R;
import one.thebox.android.adapter.UserItemRecyclerAdapter;
import one.thebox.android.app.MyApplication;

public class OrderItemsActivity extends BaseActivity {

    private static final String EXTRA_USER_ITEM_ARRAY_LIST = "user_item_array_list";
    private RecyclerView recyclerView;
    private UserItemRecyclerAdapter userItemRecyclerAdapter;
    private RealmList<UserItem> userItems = new RealmList<>();
    private int orderId;

    public static Intent newInstance(Context context, int orderId) {
        return new Intent(context, OrderItemsActivity.class).putExtra(EXTRA_USER_ITEM_ARRAY_LIST, orderId);
    }

    private void initVariables() {
        orderId = getIntent().getIntExtra(EXTRA_USER_ITEM_ARRAY_LIST, 0);
        Realm realm = MyApplication.getRealm();
        RealmQuery<Order> query = realm.where(Order.class)
                .notEqualTo(Order.FIELD_ID, 0).equalTo(Order.FIELD_ID, orderId);

        RealmResults<Order> realmResults = query.findAll();
        Order order = realmResults.get(0);
        userItems.addAll(order.getUserItems());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_items);
        setTitle("Order Items");
        initVariables();
        initViews();
        setupRecyclerView();
    }

    private void initViews() {
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
    }

    private void setupRecyclerView() {
        userItemRecyclerAdapter = new UserItemRecyclerAdapter(this, userItems, true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(userItemRecyclerAdapter);
    }


}
