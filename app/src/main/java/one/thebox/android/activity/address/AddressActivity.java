package one.thebox.android.activity.address;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import one.thebox.android.Models.address.Address;
import one.thebox.android.Models.update.Setting;
import one.thebox.android.R;
import one.thebox.android.activity.BaseActivity;
import one.thebox.android.app.Constants;
import one.thebox.android.fragment.address.AddAddressFragment;
import one.thebox.android.fragment.address.DeliveryAddressFragment;
import one.thebox.android.services.SettingService;
import one.thebox.android.util.CoreGsonUtils;

public class AddressActivity extends BaseActivity {

    private int type;
    private int calledFrom;
    private boolean isMerge;
    private Toolbar toolbar;
    private LinearLayout progressIndicatorLayout;
    private View progressStep1, progressStep2, progressStep3, progressStep4, progressStep5, progressStep6;
    private TextView progressStepToCheckoutText;
    private TextView toolbarTitle;
    private Setting setting;

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

        initView();
        initVariable();
    }

    public void initView() {
        //Tootalbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);

        progressIndicatorLayout = (LinearLayout) findViewById(R.id.progress_indicator);
        progressStepToCheckoutText = (TextView) findViewById(R.id.progress_step_text);
        progressStep1 = (View) findViewById(R.id.progress_step1);
        progressStep2 = (View) findViewById(R.id.progress_step2);
        progressStep3 = (View) findViewById(R.id.progress_step3);
        progressStep4 = (View) findViewById(R.id.progress_step4);
        progressStep5 = (View) findViewById(R.id.progress_step5);
        progressStep6 = (View) findViewById(R.id.progress_step6);

        toolbarTitle = (TextView) findViewById(R.id.title);
    }

    public void initVariable() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            type = getIntent().getIntExtra(Constants.EXTRA_ADDRESS_TYPE, 0);
            calledFrom = getIntent().getIntExtra("called_from", 0);
            isMerge = getIntent().getBooleanExtra(Constants.EXTRA_IS_CART_MERGING, false);
        }

        setting = new SettingService().getSettings(this);

        if (setting.isFirstOrder()) {
            progressIndicatorLayout.setVisibility(View.VISIBLE);
        } else {
            progressIndicatorLayout.setVisibility(View.GONE);
        }

        if (type == 1) {
            if (calledFrom == 1) {
                //called from My Account
                progressIndicatorLayout.setVisibility(View.GONE);
                addAddressFragment = new AddAddressFragment(calledFrom, type);
            } else if (calledFrom == 2) {
                //called from cart Create Address
                addAddressFragment = new AddAddressFragment(calledFrom, type, isMerge);
            }
            transactToFragment(addAddressFragment);
            toolbarTitle.setText("Add Delivery Address");
        } else if (type == 2) {
            addAddressFragment = new AddAddressFragment(calledFrom, type,
                    CoreGsonUtils.fromJson(getIntent().getStringExtra("edit_delivery_address"), Address.class));
            transactToFragment(addAddressFragment);
            toolbarTitle.setText("Edit Delivery Address");
        }
        //Address is saved so transact to Delivery Address Fragment
        else if (calledFrom == 2) {
            deliveryAddressFragment = new DeliveryAddressFragment(
                    CoreGsonUtils.fromJson(getIntent().getStringExtra("delivery_address"), Address.class), isMerge);

            transactToFragment(deliveryAddressFragment);
            toolbarTitle.setText("Delivery Address");
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

    @Override
    public Toolbar getToolbar() {
        return toolbar;
    }

    @Override
    public void setToolbar(Toolbar toolbar) {
        this.toolbar = toolbar;
    }

    @Override
    public TextView getToolbarTitle() {
        return toolbarTitle;
    }

    public void setToolbarTitle(TextView toolbarTitle) {
        this.toolbarTitle = toolbarTitle;
    }
}
