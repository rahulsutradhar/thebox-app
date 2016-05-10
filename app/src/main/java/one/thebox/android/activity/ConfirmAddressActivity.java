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

import one.thebox.android.Models.AddressAndOrder;
import one.thebox.android.Models.Order;
import one.thebox.android.Models.User;
import one.thebox.android.R;
import one.thebox.android.ViewHelper.AddressBottomSheet;
import one.thebox.android.adapter.EditDeliveryAddressAdapter;
import one.thebox.android.adapter.SelectDeliveryAddressAdapter;
import one.thebox.android.util.CoreGsonUtils;
import one.thebox.android.util.PrefUtils;

public class ConfirmAddressActivity extends BaseActivity {

    private static final String EXTRA_ARRAY_LIST_ORDER = "array_list_order";
    private TextView selectAddress;
    private RecyclerView recyclerView;
    private SelectDeliveryAddressAdapter selectDeliveryAddressAdapter;
    private EditDeliveryAddressAdapter editDeliveryAddressAdapter;
    private User user;
    private CheckBox checkBox;
    private ArrayList<Order> orders;
    private boolean haveDifferentAddresses;

    public static Intent getInstance(Context context, ArrayList<Order> orders) {
        Intent intent = new Intent(context, ConfirmAddressActivity.class);
        intent.putExtra(EXTRA_ARRAY_LIST_ORDER, CoreGsonUtils.toJson(orders));
        return intent;
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
    }

    private void initVariables() {
        String ordersString = getIntent().getStringExtra(EXTRA_ARRAY_LIST_ORDER);
        orders = CoreGsonUtils.fromJsontoArrayList(ordersString, Order.class);
        user = PrefUtils.getUser(ConfirmAddressActivity.this);
    }

    public void setupRecyclerView(boolean editAddressAdapter) {
        user = PrefUtils.getUser(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        if (editAddressAdapter) {
            editDeliveryAddressAdapter = new EditDeliveryAddressAdapter(this, orders);
            recyclerView.setAdapter(editDeliveryAddressAdapter);
        } else {
            selectDeliveryAddressAdapter = new SelectDeliveryAddressAdapter(this, user.getAddresses());
            recyclerView.setAdapter(selectDeliveryAddressAdapter);
        }
    }

    private void initViews() {
        selectAddress = (TextView) findViewById(R.id.button_select_address);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        checkBox = (CheckBox) findViewById(R.id.check_box);
        if (user.getAddresses() == null || user.getAddresses().isEmpty()) {
            selectAddress.setText("Add Address");
            selectAddress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addAddress();
                }
            });
            checkBox.setVisibility(View.GONE);
        } else {
            selectAddress.setText("Proceed to Time Slots");
            selectAddress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (haveDifferentAddresses) {
                        startActivity(ConfirmTimeSlotActivity.newInstance(ConfirmAddressActivity.this, editDeliveryAddressAdapter.getAddressAndOrders()));
                    } else {
                        ArrayList<AddressAndOrder> addressAndOrders = new ArrayList<AddressAndOrder>();
                        for (Order order : orders) {
                            addressAndOrders.add(new AddressAndOrder(selectDeliveryAddressAdapter.getAddresses().get(selectDeliveryAddressAdapter.getCurrentSelection()), order));
                        }
                        startActivity(ConfirmTimeSlotActivity.newInstance(ConfirmAddressActivity.this, addressAndOrders));
                    }
                }
            });
            checkBox.setVisibility(View.VISIBLE);
        }
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setupRecyclerView(isChecked);
                haveDifferentAddresses = isChecked;
            }
        });
    }

    public void addAddress() {
        new AddressBottomSheet(this, new AddressBottomSheet.OnAddressAdded() {
            @Override
            public void onAddressAdded(User.Address address) {
                User user = PrefUtils.getUser(ConfirmAddressActivity.this);
                ArrayList<User.Address> addresses = user.getAddresses();
                if (addresses == null || addresses.isEmpty()) {
                    addresses = new ArrayList<>();
                }
                addresses.add(address);
                user.setAddresses(addresses);
                PrefUtils.saveUser(ConfirmAddressActivity.this, user);
                setupRecyclerView(false);
            }
        }).show();
    }

}
