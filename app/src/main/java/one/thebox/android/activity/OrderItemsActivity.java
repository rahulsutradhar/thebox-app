package one.thebox.android.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

import one.thebox.android.Models.UserItem;
import one.thebox.android.R;
import one.thebox.android.adapter.UserItemRecyclerAdapter;
import one.thebox.android.util.CoreGsonUtils;

public class OrderItemsActivity extends BaseActivity {

    private static final String EXTRA_USER_ITEM_ARRAY_LIST = "user_item_array_list";
    private RecyclerView recyclerView;
    private UserItemRecyclerAdapter userItemRecyclerAdapter;
    private ArrayList<UserItem> userItems;

    public static Intent newInstance(Context context, ArrayList<UserItem> userItems) {
        return new Intent(context, OrderItemsActivity.class)
                .putExtra(EXTRA_USER_ITEM_ARRAY_LIST, CoreGsonUtils.toJson(userItems));
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

    private void initVariables() {
        userItems = CoreGsonUtils.fromJsontoArrayList(getIntent().getStringExtra(EXTRA_USER_ITEM_ARRAY_LIST), UserItem.class);
    }


}
