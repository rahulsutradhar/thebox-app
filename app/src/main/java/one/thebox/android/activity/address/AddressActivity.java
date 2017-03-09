package one.thebox.android.activity.address;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import io.realm.RealmList;
import one.thebox.android.Models.Address;
import one.thebox.android.Models.Order;
import one.thebox.android.Models.User;
import one.thebox.android.R;
import one.thebox.android.ViewHelper.AddressBottomSheet;
import one.thebox.android.activity.BaseActivity;
import one.thebox.android.adapter.AddressesAdapter;
import one.thebox.android.app.Constants;
import one.thebox.android.fragment.BaseFragment;
import one.thebox.android.fragment.address.AddAddressFragment;
import one.thebox.android.fragment.address.DeliveryAddressFragment;
import one.thebox.android.util.CoreGsonUtils;
import one.thebox.android.util.PrefUtils;

public class AddressActivity extends BaseActivity {

    private int type;
    private int calledFrom;

    /**
     * AddAddressFragment
     */
    private AddAddressFragment addAddressFragment;

    /**
     * Delivery Address Fragment
     */
    private DeliveryAddressFragment deliveryAddressFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addresses);

        setToolbar((Toolbar) findViewById(R.id.toolbar));
        setSupportActionBar(getToolbar());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            type = getIntent().getIntExtra(Constants.EXTRA_ADDRESS_TYPE, 0);
            calledFrom = getIntent().getIntExtra("called_from", 0);
        }

        if (type == 1) {
            if (calledFrom == 1) {
                addAddressFragment = new AddAddressFragment(calledFrom, type);
            } else {
                //take orders list
                addAddressFragment = new AddAddressFragment(calledFrom, type,
                        CoreGsonUtils.fromJsontoRealmList(getIntent().getStringExtra(Constants.EXTRA_LIST_ORDER), Order.class));
            }
            transactToFragment(addAddressFragment);
            getSupportActionBar().setTitle("Add Delivery Address");
        } else if (type == 2) {
            addAddressFragment = new AddAddressFragment(calledFrom, type,
                    CoreGsonUtils.fromJson(getIntent().getStringExtra("edit_delivery_address"), Address.class));
            transactToFragment(addAddressFragment);
            getSupportActionBar().setTitle("Edit Delivery Address");
        }
        //Address is saved so transact to Delivery Address Fragment
        else if (calledFrom == 2) {
            deliveryAddressFragment = new DeliveryAddressFragment(
                    CoreGsonUtils.fromJson(getIntent().getStringExtra("delivery_address"), Address.class),
                    CoreGsonUtils.fromJsontoRealmList(getIntent().getStringExtra(Constants.EXTRA_LIST_ORDER), Order.class));

            transactToFragment(deliveryAddressFragment);
            getSupportActionBar().setTitle("Delivery Address");
        }

    }

    public void transactToFragment(AddAddressFragment fragment) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment, "Add_Address");
        fragmentTransaction.commit();
    }

    public void transactToFragment(DeliveryAddressFragment fragment) {
        getToolbar().setTitle("Delivery Address");
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment, "Delivery_Address");
        fragmentTransaction.commit();
    }

}
