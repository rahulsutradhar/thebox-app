package one.thebox.android.activity;

import android.app.Activity;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

import one.thebox.android.Models.Address;
import one.thebox.android.Models.User;
import one.thebox.android.R;
import one.thebox.android.ViewHelper.AddEditAddressViewHelper;
import one.thebox.android.adapter.AddressesAdapter;
import one.thebox.android.adapter.ChangeAddressAdapter;
import one.thebox.android.adapter.EditDeliveryAddressAdapter;
import one.thebox.android.util.PrefUtils;

public class AddressesActivity extends BaseActivity implements View.OnClickListener{

    private RecyclerView recyclerView;
    private AddressesAdapter addressesAdapter;
    private TextView buttonCreateNew;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addresses);
        initViews();
        setTitle("Addresses");
        setupRecyclerViews();
    }

    private void setupRecyclerViews() {
        user = PrefUtils.getUser(this);
        addressesAdapter = new AddressesAdapter(this);
        addressesAdapter.setAddresses(user.getAddresses());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(addressesAdapter);
    }

    private void initViews() {
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        buttonCreateNew = (TextView) findViewById(R.id.button_create_new);
        buttonCreateNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAddAddressBottomSheet();
            }
        });
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.create_new_button: {
                openAddAddressBottomSheet();
                break;
            }
        }
    }

    private void openAddAddressBottomSheet() {
        new AddEditAddressViewHelper(this, new AddEditAddressViewHelper.OnAddressAdded() {
            @Override
            public void onAddressAdded(User.Address address) {
                User user = PrefUtils.getUser(AddressesActivity.this);
                ArrayList<User.Address> addresses = user.getAddresses();

                addresses.add(address);
                user.setAddresses(addresses);
                PrefUtils.saveUser(AddressesActivity.this, user);
                setupRecyclerViews();
            }
        }).show();
    }
}
