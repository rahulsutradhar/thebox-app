package one.thebox.android.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import one.thebox.android.Models.AddressAndOrder;
import one.thebox.android.R;
import one.thebox.android.ViewHelper.TimeSlotBottomSheet;
import one.thebox.android.util.CoreGsonUtils;

public class ConfirmTimeSlotActivity extends BaseActivity {

    private ArrayList<AddressAndOrder> addressAndOrders;
    private CheckBox checkBox;
    private TextView proceedToPayment;
    private LinearLayout timeHolderLinearLayout;
    private RecyclerView recyclerView;
    public static final String EXTRA_ADDRESS_AND_ORDERS = "extra_address_and_orders";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_time_slot);
        setTitle("Select Time Slots");
        initViews();
        initVariable();
    }

    private void initVariable() {
        addressAndOrders = CoreGsonUtils.fromJsontoArrayList(getIntent().getStringExtra(EXTRA_ADDRESS_AND_ORDERS), AddressAndOrder.class);
    }

    public static Intent newInstance(Context context, ArrayList<AddressAndOrder> addressAndOrders) {
        return new Intent(context, ConfirmTimeSlotActivity.class).putExtra(EXTRA_ADDRESS_AND_ORDERS, CoreGsonUtils.toJson(addressAndOrders));
    }

    private void initViews() {
        timeHolderLinearLayout = (LinearLayout) findViewById(R.id.holder_time);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        checkBox = (CheckBox) findViewById(R.id.check_box);
        proceedToPayment = (TextView) findViewById(R.id.button_proceed_to_payment);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    recyclerView.setVisibility(View.VISIBLE);
                    timeHolderLinearLayout.setVisibility(View.GONE);
                } else {
                    recyclerView.setVisibility(View.GONE);
                    timeHolderLinearLayout.setVisibility(View.VISIBLE);
                }
            }
        });
        timeHolderLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimeSlotBottomSheet(ConfirmTimeSlotActivity.this, Calendar.getInstance().getTime(), new TimeSlotBottomSheet.OnTimePicked() {
                    @Override
                    public void onTimePicked(Date date) {

                    }
                }).show();
            }
        });
        proceedToPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(ConfirmPaymentDetailsActivity.getInstance(ConfirmTimeSlotActivity.this, addressAndOrders));
            }
        });
    }


}
