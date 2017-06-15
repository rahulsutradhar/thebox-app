package one.thebox.android.activity.address;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;

import one.thebox.android.Models.address.Address;
import one.thebox.android.R;
import one.thebox.android.activity.BaseActivity;
import one.thebox.android.app.Constants;
import one.thebox.android.fragment.address.AddAddressFragment;
import one.thebox.android.fragment.address.DeliveryAddressFragment;
import one.thebox.android.util.CoreGsonUtils;

public class AddressActivity extends BaseActivity {

    private int type;
    private int calledFrom;
    private boolean isMerge;

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
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            type = getIntent().getIntExtra(Constants.EXTRA_ADDRESS_TYPE, 0);
            calledFrom = getIntent().getIntExtra("called_from", 0);
            isMerge = getIntent().getBooleanExtra(Constants.EXTRA_IS_CART_MERGING, false);
        }

        if (type == 1) {
            if (calledFrom == 1) {
                addAddressFragment = new AddAddressFragment(calledFrom, type);
            } else if (calledFrom == 2) {
                //called from cart Create Address
                addAddressFragment = new AddAddressFragment(calledFrom, type, isMerge);
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
                    CoreGsonUtils.fromJson(getIntent().getStringExtra("delivery_address"), Address.class),isMerge );

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
