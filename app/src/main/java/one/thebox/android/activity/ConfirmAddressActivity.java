package one.thebox.android.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import one.thebox.android.Models.Address;
import one.thebox.android.Models.AddressAndOrder;
import one.thebox.android.Models.Order;
import one.thebox.android.Models.User;
import one.thebox.android.R;
import one.thebox.android.ViewHelper.AddressBottomSheet;
import one.thebox.android.adapter.EditDeliveryAddressAdapter;
import one.thebox.android.adapter.SelectDeliveryAddressAdapter;
import one.thebox.android.app.MyApplication;
import one.thebox.android.util.CoreGsonUtils;
import one.thebox.android.util.PrefUtils;

public class ConfirmAddressActivity extends BaseActivity {

    private static final String EXTRA_ARRAY_LIST_ORDER = "array_list_order";
    ArrayList<Integer> orderIds = new ArrayList<>();
    private TextView selectAddress;
    private RecyclerView recyclerView;
    private SelectDeliveryAddressAdapter selectDeliveryAddressAdapter;
    private User user;
    private RealmList<Order> orders = new RealmList<>();

    public static Intent getInstance(Context context, RealmList<Order> orders) {
        ArrayList<Integer> orderIds = new ArrayList<>();
        for (Order order : orders) {
            orderIds.add(order.getId());
        }
        return new Intent(context, ConfirmAddressActivity.class).putExtra(EXTRA_ARRAY_LIST_ORDER, CoreGsonUtils.toJson(orderIds));
    }

    private void initVariables() {
        this.user = PrefUtils.getUser(this);
        orderIds = CoreGsonUtils.fromJsontoArrayList(
                getIntent().getStringExtra(EXTRA_ARRAY_LIST_ORDER), Integer.class);
        if (orderIds.isEmpty()) {
            return;
        }
        Realm realm = MyApplication.getRealm();
        RealmQuery<Order> query = realm.where(Order.class)
                .notEqualTo(Order.FIELD_ID, 0);
        for (int i = 0; i < orderIds.size(); i++) {
            if (orderIds.size() - 1 == i) {
                query.equalTo(Order.FIELD_ID, orderIds.get(i));
            } else {
                query.equalTo(Order.FIELD_ID, orderIds.get(i)).or();
            }
        }
        RealmResults<Order> realmResults = query.findAll();
        for (Order order : realmResults) {
            orders.add(order);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_address);
        setTitle("Select Address");
        initVariables();
        initViews();
        if (user.getAddresses() == null || user.getAddresses().isEmpty()) {
            addAddress();
        } else {
            setupRecyclerView(false);
        }
        if (user.getAddresses() != null && !user.getAddresses().isEmpty()) {
            ArrayList<AddressAndOrder> addressAndOrders = new ArrayList<AddressAndOrder>();
            for (Order order : orders) {
                addressAndOrders.add(new AddressAndOrder(selectDeliveryAddressAdapter.getAddresses().get(selectDeliveryAddressAdapter.getCurrentSelection()).getId(), order.getId()));
            }
            startActivity(ConfirmTimeSlotActivity.newInstance(ConfirmAddressActivity.this, addressAndOrders));
            finish();
        }
    }

    public void setupRecyclerView(boolean editAddressAdapter) {
        user = PrefUtils.getUser(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        selectDeliveryAddressAdapter = new SelectDeliveryAddressAdapter(this, user.getAddresses());
        recyclerView.setAdapter(selectDeliveryAddressAdapter);

    }

    private void initViews() {
        selectAddress = (TextView) findViewById(R.id.button_select_address);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        if (user.getAddresses() == null || user.getAddresses().isEmpty()) {
            selectAddress.setText("Add Address");
            selectAddress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addAddress();
                }
            });
        } else {
            selectAddress.setText("Proceed to Time Slots");
            selectAddress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    ArrayList<AddressAndOrder> addressAndOrders = new ArrayList<AddressAndOrder>();
                    for (Order order : orders) {
                        addressAndOrders.add(new AddressAndOrder(selectDeliveryAddressAdapter.getAddresses().get(selectDeliveryAddressAdapter.getCurrentSelection()).getId(), order.getId()));
                    }
                    startActivity(ConfirmTimeSlotActivity.newInstance(ConfirmAddressActivity.this, addressAndOrders));
                    finish();

                }
            });
        }

    }

    public void addAddress() {
        new AddressBottomSheet(this, new AddressBottomSheet.OnAddressAdded() {
            @Override
            public void onAddressAdded(Address address) {
                User user = PrefUtils.getUser(ConfirmAddressActivity.this);
                RealmList<Address> addresses = user.getAddresses();
                if (addresses == null || addresses.isEmpty()) {
                    addresses = new RealmList<Address>();
                }
                addresses.add(address);
                user.setAddresses(addresses);
                PrefUtils.saveUser(ConfirmAddressActivity.this, user);
                setupRecyclerView(false);
                initViews();
            }
        }).show();
    }

}
